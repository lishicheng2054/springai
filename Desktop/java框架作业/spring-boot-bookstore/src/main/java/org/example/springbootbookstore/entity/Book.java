package org.example.springbootbookstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "tb_book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Integer id;

    private String title;
    private String author;
    private String press;
    private Double price;

    // 一对多：一本书对应多条评论，通过 comment 表的 book_id 列关联
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id")
    private List<TbComment> commentList = new ArrayList<>();
}
