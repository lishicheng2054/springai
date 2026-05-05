package org.example.springbootbookstore.controller;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Tbbook;
import org.example.springbootbookstore.service.Book2Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book2")
public class Book2Controller {

    @Resource
    private Book2Service book2Service;

    @GetMapping("/allBooks")
    public Object allBooks() {
        return book2Service.allBooks();
    }

    // 添加
    @PostMapping("/insertBook")
    public String insertBook(Tbbook tbbook){
        book2Service.insertBook(tbbook);
        return "添加成功";
    }

    // 修改
    @PostMapping("/updateBook")
    public String updateBook(Tbbook tbbook){
        book2Service.updateBook(tbbook);
        return "修改成功";
    }

    @PostMapping("/deleteBook")
    public String deleteBook(Integer id){
        book2Service.deleteBook(id);
        return "删除成功";
    }

    @PostMapping("/sortBook")
    @ResponseBody
    public List<Tbbook> sortBook(String by){
        List<Tbbook> books = book2Service.sortBook(by);
        return books;
    }

    @RequestMapping("/searchBook")
    public List<Tbbook> searchBook(Tbbook book, String min_price, String max_price) {
        double minPrice, maxPrice;

        if (min_price == null || min_price.equals("")) {
            minPrice = 0.0;
        } else {
            minPrice = Double.valueOf(min_price);
        }

        if (max_price == null || max_price.equals("")) {
            maxPrice = Double.MAX_VALUE;
        } else {
            maxPrice = Double.valueOf(max_price);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("title", book.getTitle());
        map.put("author", book.getAuthor());
        map.put("press", book.getPress());
        map.put("minPrice", minPrice);
        map.put("maxPrice", maxPrice);

        return book2Service.searchBook(map);
    }

    @RequestMapping("/showComment")
    public Tbbook showComment(Integer id){
        return book2Service.showComment(id);
    }

}