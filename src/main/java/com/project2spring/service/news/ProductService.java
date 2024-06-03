package com.project2spring.service.news;

import com.project2spring.domain.news.Product;
import com.project2spring.mapper.product.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProductService {

    private final ProductMapper mapper;
    private static String product_url = "https://www.daangn.com/fleamarket/";

    public List<Product> getProduct() throws IOException {
        List<Product> productList = new ArrayList<>();
        Document document = Jsoup.connect(product_url).get();

        Elements contents = document.select("section div.cards-wrap article.card-top");

        for (Element content : contents) {
            Product product = Product.builder()
                    .image(content.select("div img").attr("abs:src")) //이미지
                    .title(content.select("h2").text()) // 제목
                    .url(content.select("a").attr("abs:href"))  // 링크
                    .build();
            productList.add(product);

            // 데이터가 이미 존재하는지 확인
            if (mapper.countByImage(product.getImage()) == 0) {
                mapper.insertProduct(product);
            }
        }
        return productList;
    }
}
