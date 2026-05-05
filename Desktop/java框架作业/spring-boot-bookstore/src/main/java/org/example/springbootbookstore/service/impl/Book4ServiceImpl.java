package org.example.springbootbookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.springbootbookstore.entity.Book4;
import org.example.springbootbookstore.entity.Comment;
import org.example.springbootbookstore.mapper.Book4Mapper;
import org.example.springbootbookstore.mapper.CommentMapper;
import org.example.springbootbookstore.service.Book4Service;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Book4ServiceImpl extends ServiceImpl<Book4Mapper, Book4> implements Book4Service {

    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Book4> allBooks() {
        return list();
    }

    @Override
    public void insertBook(Book4 book) {
        save(book);
    }

    @Override
    public void updateBook(Book4 book) {
        updateById(book);
    }

    @Override
    public void deleteBook(Integer id) {
        removeById(id);
    }

    @Override
    public List<Book4> searchBook(String title, String author, String press, Double minPrice, Double maxPrice) {
        LambdaQueryWrapper<Book4> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(title), Book4::getTitle, title)
               .like(StringUtils.isNotBlank(author), Book4::getAuthor, author)
               .like(StringUtils.isNotBlank(press), Book4::getPress, press)
               .ge(minPrice != null, Book4::getPrice, minPrice)
               .le(maxPrice != null, Book4::getPrice, maxPrice);
        return list(wrapper);
    }

    @Override
    public List<Book4> sortBook(String by) {
        LambdaQueryWrapper<Book4> wrapper = new LambdaQueryWrapper<>();
        switch (by) {
            case "title"  -> wrapper.orderByAsc(Book4::getTitle);
            case "author" -> wrapper.orderByAsc(Book4::getAuthor);
            case "press"  -> wrapper.orderByAsc(Book4::getPress);
            case "price"  -> wrapper.orderByAsc(Book4::getPrice);
            default       -> wrapper.orderByAsc(Book4::getId);
        }
        return list(wrapper);
    }

    @Override
    public Book4 showComment(Integer id) {
        Book4 book = getById(id);
        if (book != null) {
            List<Comment> comments = commentMapper.selectCommentsById(id);
            book.setCommentList(comments);
        }
        return book;
    }
}
