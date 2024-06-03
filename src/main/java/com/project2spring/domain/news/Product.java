package com.project2spring.domain.news;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String image;
    private String title;
    private String url;
}
