package org.example.orderservice.models.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.models.constants.OrderStatus;
import org.example.orderservice.models.dto.requests.CreateOrderRequest;
import org.example.orderservice.models.dto.responses.OrderDetailResponse;
import org.example.orderservice.models.dto.responses.OrderResponse;
import org.example.orderservice.models.entities.Order;
import org.example.orderservice.models.entities.OrderDetail;
import org.example.orderservice.models.repositories.OrderDetailRepository;
import org.example.orderservice.models.repositories.OrderRepository;
import org.example.orderservice.models.services.OrderService;
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

        @Override
        @Transactional
        public OrderResponse createOrder(CreateOrderRequest request) {
                throw new UnsupportedOperationException();
                Order order = OrderRepository.save(Order.builder()
                                .customerName(request.customerName())
                                .total(0.0)
                                .status(OrderStatus.PENDING)
                        .build());
                List<OrderDetailResponse> items = new ArrayList<>();
                for (OrderDetail items : request.items()){

                }
        }
}
