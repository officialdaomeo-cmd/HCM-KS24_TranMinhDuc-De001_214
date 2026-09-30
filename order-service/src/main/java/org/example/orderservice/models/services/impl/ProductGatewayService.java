package org.example.orderservice.models.services.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.clients.ProductClient;
import org.example.orderservice.exceptions.ProductNotFoundException;
import org.example.orderservice.exceptions.ProductServiceException;
import org.example.orderservice.models.dto.responses.ProductResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductGatewayService {

    private final ProductClient productClient;

    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackGetProductById")
    public ProductResponse getProductById(Long productId) {
        return productClient.getProductById(productId);
    }
    
    public ProductResponse fallbackGetProductById(Long productId, Throwable cause) {
        if (cause instanceof FeignException.NotFound) {
            throw new ProductNotFoundException(productId);
        }
        throw new ProductServiceException("Product service is unavailable");
    }
}
