package org.example.orderservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.models.constants.OrderStatus;
import org.example.orderservice.models.dto.requests.CreateOrderDetailRequest;
import org.example.orderservice.models.dto.requests.CreateOrderRequest;
import org.example.orderservice.models.dto.responses.OrderDetailResponse;
import org.example.orderservice.models.dto.responses.OrderResponse;
import org.example.orderservice.models.dto.responses.ProductResponse;
import org.example.orderservice.models.entities.Order;
import org.example.orderservice.models.entities.OrderDetail;
import org.example.orderservice.models.repositories.OrderDetailRepository;
import org.example.orderservice.models.repositories.OrderRepository;
import org.example.orderservice.models.services.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductGatewayService productGatewayService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerName(request.customerName());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(0.0);
        order = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        List<OrderDetailResponse> detailResponses = new ArrayList<>();
        double total = 0.0;

        for (CreateOrderDetailRequest itemRequest : request.items()) {
            ProductResponse product = productGatewayService.getProductById(itemRequest.productId());
            
            Double unitPrice = product.price();
            Double subtotal = unitPrice * itemRequest.quantity();
            total += subtotal;

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .productId(product.id())
                    .quantity(itemRequest.quantity())
                    .unitPrice(unitPrice)
                    .build();
            
            orderDetails.add(orderDetail);
            
            detailResponses.add(new OrderDetailResponse(
                    null,
                    product.id(),
                    product.name(),
                    itemRequest.quantity(),
                    unitPrice,
                    subtotal
            ));
        }

        orderDetailRepository.saveAll(orderDetails);
        
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetailResponse oldRes = detailResponses.get(i);
            detailResponses.set(i, new OrderDetailResponse(
                    orderDetails.get(i).getId(),
                    oldRes.productId(),
                    oldRes.productName(),
                    oldRes.quantity(),
                    oldRes.unitPrice(),
                    oldRes.subtotal()
            ));
        }

        order.setTotal(total);
        order = orderRepository.save(order);

        kafkaTemplate.send("order-created", request.customerEmail());

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getTotal(),
                order.getStatus(),
                detailResponses
        );
    }
}
