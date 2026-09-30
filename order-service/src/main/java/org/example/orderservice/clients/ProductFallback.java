package org.example.orderservice.clients;

import feign.FeignException;
import org.example.orderservice.exceptions.ProductNotFoundException;
import org.example.orderservice.exceptions.ProductServiceException;
import org.example.orderservice.models.dto.responses.ProductResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductFallback implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public ProductResponse getProductById(Long id) {
                if (cause instanceof FeignException.NotFound) {
                     throw new ProductNotFoundException(id);
                }
            }
        }
        throw new ProductServiceException("Loi day");

    }
    }
