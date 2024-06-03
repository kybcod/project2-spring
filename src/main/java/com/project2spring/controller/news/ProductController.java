package com.project2spring.controller.news;

import com.project2spring.domain.news.Product;
import com.project2spring.service.news.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping("/list")
    public List<Product> product() throws IOException {
        return service.getProduct();
    }
}
