package org.example.springbootbookstore.controller;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Book;
import org.example.springbootbookstore.entity.BookPojo;
import org.example.springbootbookstore.service.Book3Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book3")
public class Book3Controller {

    @Resource
    private Book3Service book3Service;

    @GetMapping("/allBooks")
    public List<Book> allBooks() {
        return book3Service.allBooks();
    }

    @PostMapping("/insertBook")
    public String insertBook(Book book) {
        book3Service.insertBook(book);
        return "添加成功";
    }

    @PostMapping("/updateBook")
    public String updateBook(Book book) {
        book3Service.updateBook(book);
        return "修改成功";
    }

    @RequestMapping("/deleteBook")
    public String deleteBook(Integer id) {
        book3Service.deleteBook(id);
        return "删除成功";
    }

    // 支持 GET（href跳转）和 POST（ajax）两种方式
    @RequestMapping("/showComment")
    public Book showComment(Integer id) {
        return book3Service.showComment(id);
    }

    @RequestMapping("/searchBook1")
    public List<Book> searchBook1(BookPojo bookPojo) {
        if (bookPojo.getMinPrice() == null) {
            bookPojo.setMinPrice(0.0);
        }
        if (bookPojo.getMaxPrice() == null) {
            bookPojo.setMaxPrice(Double.MAX_VALUE);
        }
        return book3Service.searchBook1(bookPojo);
    }

    @RequestMapping("/searchBook2")
    public List<Book> searchBook2(BookPojo book) {
        return book3Service.searchBook2(book);
    }
}
