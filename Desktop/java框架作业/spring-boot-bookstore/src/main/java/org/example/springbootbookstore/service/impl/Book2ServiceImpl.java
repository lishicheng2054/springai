package org.example.springbootbookstore.service.impl;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Tbbook;
import org.example.springbootbookstore.mapper.Book2Mapper;
import org.example.springbootbookstore.service.Book2Service;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class Book2ServiceImpl implements Book2Service {

    @Resource
    private Book2Mapper book2Mapper;

    @Override
    public List<Tbbook> allBooks() {
        return book2Mapper.allBooks();
    }

    @Override
    public void insertBook(Tbbook tbbook) {
        book2Mapper.insertBook(tbbook);
    }

    @Override
    public void updateBook(Tbbook tbbook) {
        book2Mapper.updateBook(tbbook);
    }

    @Override
    public void deleteBook(Integer id) {
        book2Mapper.deleteBook(id);
    }

    @Override
    public List<Tbbook> searchBook(Map<String, Object> map) {
        return book2Mapper.searchBook(map);
    }
    @Override
    public List<Tbbook> sortBook(String by) {
        return book2Mapper.sortBook(by);
    }
    @Override
    public Tbbook showComment(Integer id) {
        return book2Mapper.showComment(id);
    }
}