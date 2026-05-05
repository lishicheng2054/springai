package org.example.springbootbookstore.service;

import org.example.springbootbookstore.entity.Tbbook;
import java.util.List;
import java.util.Map;

public interface Book2Service {
    List<Tbbook> allBooks();
    void insertBook(Tbbook tbbook);
    void updateBook(Tbbook tbbook);
    void deleteBook(Integer id);
    List<Tbbook> searchBook(Map<String, Object> map);
    List<Tbbook> sortBook(String by);
    Tbbook showComment(Integer id);
}