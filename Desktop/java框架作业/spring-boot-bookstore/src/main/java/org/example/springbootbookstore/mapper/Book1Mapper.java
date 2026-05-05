package org.example.springbootbookstore.mapper;
import org.apache.ibatis.annotations.*;
import org.example.springbootbookstore.entity.Tbbook;
import org.springframework.context.annotation.Import;

import java.util.List;

@Mapper
public interface Book1Mapper {
    @Select("select * from tbbook")
    List<Tbbook> allBooks();

    @Insert("insert into tbbook(title,author,press,price) values (#{title},#{author},#{press},#{price})")
    void insertBook(Tbbook tbbook);
    @Update("update tbbook set title=#{title},press=#{press},price=#{price} where id=#{id}")
    void updateBook(Tbbook tbbook);
    @Delete("delete from tbbook where id=#{id}")
    void deleteBook(Integer id);
    @Select("select * from tbbook order by ${by}")
    List<Tbbook> sortBook(String by);
}
