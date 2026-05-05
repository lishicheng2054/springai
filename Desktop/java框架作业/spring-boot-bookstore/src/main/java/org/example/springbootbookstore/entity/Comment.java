package org.example.springbootbookstore.entity;

public class Comment {
    private int id;
    private String content;
    private String cAuthor;
    private int bookId;

    // getter & setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCAuthor() {
        return cAuthor;
    }

    public void setCAuthor(String cAuthor) {
        this.cAuthor = cAuthor;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    // toString()
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", cAuthor='" + cAuthor + '\'' +
                ", bookId=" + bookId +
                '}';
    }
}