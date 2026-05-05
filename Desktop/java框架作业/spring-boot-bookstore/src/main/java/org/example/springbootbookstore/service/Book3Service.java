package org.example.springbootbookstore.service;

import org.example.springbootbookstore.entity.Book;
import org.example.springbootbookstore.entity.BookPojo;

import java.util.List;

public interface Book3Service {
    List<Book> allBooks();
    void insertBook(Book book);
    void updateBook(Book book);
    void deleteBook(Integer id);
    Book showComment(Integer id);
    List<Book> searchBook1(BookPojo book);

    List<Book> searchBook2(BookPojo book);
}
