package com.project2spring.service.news;

import com.project2spring.domain.news.Product;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private static String product_url = "https://www.daangn.com/fleamarket/";

    @PostConstruct
    public List<Product> getProductData() throws IOException {
        List<Product> productList = new ArrayList<>();
        Document document = Jsoup.connect(product_url).get();

        Elements contents = document.select("section div.cards-wrap article.card-top");

        for (Element content : contents) {
            Product news = Product.builder()
                    .image(content.select("div img").attr("abs:src")) //이미지
                    .title(content.select("h2").text()) // 제목
                    .url(content.select("a").attr("abs:href"))  // 링크
                    .build();
            productList.add(news);
        }
        return productList;
    }
}
