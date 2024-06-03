package com.project2spring.domain.news;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Product {
    private String image;
    private String title;
    private String url;
}
