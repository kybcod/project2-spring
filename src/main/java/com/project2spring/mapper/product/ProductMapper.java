package com.project2spring.mapper.product;

import com.project2spring.domain.news.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO product (image, title) VALUES (#{image}, #{title})")
    int insertProduct(Product product);

    @Select("SELECT COUNT(*) FROM product WHERE image=#{image}")
    int countByImage(String image);
}
