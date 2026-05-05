package org.example.springbootbookstore.entity;

import lombok.Data;

@Data
public class BookPojo extends Book{

    private Double minPrice=0.0;
    private Double maxPrice=Double.MAX_VALUE;

}
