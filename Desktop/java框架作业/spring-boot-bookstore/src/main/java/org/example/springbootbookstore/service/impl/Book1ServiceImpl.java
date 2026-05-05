package org.example.springbootbookstore.service.impl;

import org.example.springbootbookstore.entity.Tbbook;
import org.example.springbootbookstore.mapper.Book1Mapper;
import org.example.springbootbookstore.service.Book1Service;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Book1ServiceImpl implements Book1Service {

    @Resource
    private Book1Mapper book1Mapper;

    @Override
    // 方法名必须和接口一致：allBooks（大写B）
    public List<Tbbook> allBooks() {
        // 用实例变量book1Mapper调用，不是类名Book1Mapper
        return book1Mapper.allBooks();
    }
    @Override
    public void insertBook(Tbbook tbbook){
        book1Mapper.insertBook(tbbook);
    }
    @Override
    public void updateBook(Tbbook tbbook){
        book1Mapper.updateBook(tbbook);
    }
    @Override
    public void deleteBook(Integer id){
        book1Mapper.deleteBook(id);
    }
    @Override
    public List<Tbbook> sortBook(String by) {
        return book1Mapper.sortBook(by);
    }
}