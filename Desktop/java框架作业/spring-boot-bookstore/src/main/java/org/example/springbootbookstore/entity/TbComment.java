package org.example.springbootbookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comment")
@Data
public class TbComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @Column(name = "c_author")
    private String cAuthor;

    @Column(name = "book_id")
    private Integer bookId;
}
