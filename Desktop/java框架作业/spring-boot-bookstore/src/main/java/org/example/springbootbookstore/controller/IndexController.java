package org.example.springbootbookstore.controller;

import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Tbbook;
import org.example.springbootbookstore.mapper.Book1Mapper;
import org.example.springbootbookstore.service.Book1Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class IndexController {
    @GetMapping("/")
    public String root() {
        return "index1";
    }
    @GetMapping("/index1")
    public String index1(){
        return "index1";
    }

    @GetMapping("/index2")
    public String index2(){
        return "index2";
    }

    @GetMapping("/index3")
    public String index3(){ return "index3"; }

    @GetMapping("/index4")
    public String index4(){ return "index4"; }
    @Resource
    private Book1Service book1Service;
    //    @GetMapping("/book1/allBooks")
    @ResponseBody
    public List<Tbbook> allBooks() {
        return book1Service.allBooks();
    }
}