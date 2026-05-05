package org.example.springbootbookstore.controller;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Book4;
import org.example.springbootbookstore.service.Book4Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book4")
public class Book4Controller {

    @Resource
    private Book4Service book4Service;

    @GetMapping("/allBooks")
    public List<Book4> allBooks() {
        return book4Service.allBooks();
    }

    @PostMapping("/insertBook")
    public String insertBook(Book4 book) {
        book4Service.insertBook(book);
        return "添加成功";
    }

    @PostMapping("/updateBook")
    public String updateBook(Book4 book) {
        book4Service.updateBook(book);
        return "修改成功";
    }

    @PostMapping("/deleteBook")
    public String deleteBook(Integer id) {
        book4Service.deleteBook(id);
        return "删除成功";
    }

    @PostMapping("/sortBook")
    public List<Book4> sortBook(String by) {
        return book4Service.sortBook(by);
    }

    @RequestMapping("/searchBook")
    public List<Book4> searchBook(String title, String author, String press,
                                   String min_price, String max_price) {
        Double minPrice = (min_price == null || min_price.isBlank()) ? 0.0 : Double.parseDouble(min_price);
        Double maxPrice = (max_price == null || max_price.isBlank()) ? Double.MAX_VALUE : Double.parseDouble(max_price);
        return book4Service.searchBook(title, author, press, minPrice, maxPrice);
    }

    @RequestMapping("/showComment")
    public Book4 showComment(Integer id) {
        return book4Service.showComment(id);
    }
}
