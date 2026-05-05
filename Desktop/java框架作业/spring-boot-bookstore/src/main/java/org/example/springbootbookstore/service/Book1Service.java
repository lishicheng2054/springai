package org.example.springbootbookstore.service;

import org.example.springbootbookstore.entity.Tbbook;

import java.util.List;

public interface Book1Service {
    List<Tbbook> allBooks();
    void insertBook(Tbbook tbbook);
    void updateBook(Tbbook tbbook);
    void deleteBook(Integer id);
    List<Tbbook> sortBook(String by);
}
