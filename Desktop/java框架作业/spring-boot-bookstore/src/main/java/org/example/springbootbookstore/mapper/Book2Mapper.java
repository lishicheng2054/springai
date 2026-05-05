package org.example.springbootbookstore.mapper;

import org.example.springbootbookstore.entity.Tbbook;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface Book2Mapper {
    List<Tbbook> allBooks();
    void insertBook(Tbbook tbbook);
    void updateBook(Tbbook tbbook);
    void deleteBook(Integer id);
    List<Tbbook> sortBook(String by);
    List<Tbbook> searchBook(Map<String, Object> map);
    Tbbook showComment(Integer id);
}