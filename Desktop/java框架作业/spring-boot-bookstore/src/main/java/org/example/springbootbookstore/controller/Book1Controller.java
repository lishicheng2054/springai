package org.example.springbootbookstore.controller;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Tbbook;
import org.example.springbootbookstore.service.Book1Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book1")
public class Book1Controller {
    @Resource
    private Book1Service book1Service;

    // 查询所有
    @GetMapping("/allBooks")
    public Object allBooks() {
        return book1Service.allBooks();
    }

    // 添加
    @PostMapping("/insertBook")
    public String insertBook(Tbbook tbbook){
        book1Service.insertBook(tbbook);
        return "添加成功";
    }

    // 修改
    @PostMapping("/updateBook")
    public String updateBook(Tbbook tbbook){
        book1Service.updateBook(tbbook);
        return "修改成功";
    }

    @PostMapping("/deleteBook")
    public String deleteBook(Integer id){
        book1Service.deleteBook(id);
        return "删除成功";
    }

    @PostMapping("/sortBook")
    @ResponseBody
    public List<Tbbook> sortBook(String by){
        List<Tbbook> books = book1Service.sortBook(by);
        return books;
    }
}