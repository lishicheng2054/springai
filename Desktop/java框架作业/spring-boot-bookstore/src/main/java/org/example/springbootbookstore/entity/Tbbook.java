package org.example.springbootbookstore.entity;

import java.util.List;

public class Tbbook {
    private int id;
    private String title;
    private String author;
    private String press;
    private Double price;
    private List<Comment> commentList;

    // getter & setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    // toString()
    @Override
    public String toString() {
        return "Tbbook{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", press='" + press + '\'' +
                ", price=" + price +
                ", commentList=" + commentList +
                '}';
    }
}