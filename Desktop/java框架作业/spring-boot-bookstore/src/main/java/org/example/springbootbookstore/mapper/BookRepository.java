package org.example.springbootbookstore.mapper;

import org.example.springbootbookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    // 多条件查询（原生SQL方式）
    @Query(value = "select * from tb_book where price between :minPrice and :maxPrice " +
            "and title like concat('%',:title,'%') " +
            "and author like concat('%',:author,'%') " +
            "and press like concat('%',:press,'%')",
            nativeQuery = true)
    List<Book> findByConditions(@Param("title") String title,
                                @Param("author") String author,
                                @Param("press") String press,
                                @Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice);
}
