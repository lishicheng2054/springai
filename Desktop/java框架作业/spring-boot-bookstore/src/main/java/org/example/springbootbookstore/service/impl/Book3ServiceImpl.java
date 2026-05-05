package org.example.springbootbookstore.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.example.springbootbookstore.entity.Book;
import org.example.springbootbookstore.entity.BookPojo;
import org.example.springbootbookstore.mapper.BookRepository;
import org.example.springbootbookstore.service.Book3Service;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Book3ServiceImpl implements Book3Service {

    @Resource
    private BookRepository bookRepository;

    @Override
    public List<Book> allBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void insertBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }

    // 一对多查询：直接 findById，Book 已配置 EAGER 加载 commentList
    @Override
    public Book showComment(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    // 方式1：原生SQL多条件查询
    @Override
    public List<Book> searchBook1(BookPojo book) {
        return bookRepository.findByConditions(
                book.getTitle() == null ? "" : book.getTitle(),
                book.getAuthor() == null ? "" : book.getAuthor(),
                book.getPress() == null ? "" : book.getPress(),
                book.getMinPrice() == null ? 0.0 : book.getMinPrice(),
                book.getMaxPrice() == null ? Double.MAX_VALUE : book.getMaxPrice()
        );
    }

    // 方式2：JPA Specification 动态多条件查询
    @Override
    public List<Book> searchBook2(BookPojo bookPojo) {
        return bookRepository.findAll((root, query, builder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (bookPojo.getMinPrice() != null && bookPojo.getMaxPrice() != null) {
                predicateList.add(builder.between(root.get("price"), bookPojo.getMinPrice(), bookPojo.getMaxPrice()));
            }
            if (StringUtils.isNotBlank(bookPojo.getTitle())) {
                predicateList.add(builder.like(root.get("title"), "%" + bookPojo.getTitle() + "%"));
            }
            if (StringUtils.isNotBlank(bookPojo.getAuthor())) {
                predicateList.add(builder.like(root.get("author"), "%" + bookPojo.getAuthor() + "%"));
            }
            if (StringUtils.isNotBlank(bookPojo.getPress())) {
                predicateList.add(builder.like(root.get("press"), "%" + bookPojo.getPress() + "%"));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            return builder.and(predicateList.toArray(predicates));
        });
    }
}
