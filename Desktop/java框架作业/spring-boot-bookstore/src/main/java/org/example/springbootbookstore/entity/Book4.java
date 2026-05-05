package org.example.springbootbookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("tbbook")
public class Book4 {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String author;

    private String press;

    private Double price;

    @TableField(exist = false)
    private List<Comment> commentList;
}
