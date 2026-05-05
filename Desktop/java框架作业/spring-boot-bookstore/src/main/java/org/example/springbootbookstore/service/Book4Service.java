package org.example.springbootbookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springbootbookstore.entity.Book4;

import java.util.List;

public interface Book4Service extends IService<Book4> {

    List<Book4> allBooks();

    void insertBook(Book4 book);

    void updateBook(Book4 book);

    void deleteBook(Integer id);

    /** 多条件查询：书名、作者、出版社、价格区间 */
    List<Book4> searchBook(String title, String author, String press, Double minPrice, Double maxPrice);

    /** 排序查询 */
    List<Book4> sortBook(String by);

    /** 一对多：查询图书及其评论 */
    Book4 showComment(Integer id);
}
