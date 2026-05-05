package org.example.springbootbookstore.mapper;

import org.example.springbootbookstore.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentsById(Integer id);
}