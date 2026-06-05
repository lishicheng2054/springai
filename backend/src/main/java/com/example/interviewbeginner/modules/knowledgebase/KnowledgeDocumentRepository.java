package com.example.interviewbeginner.modules.knowledgebase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 知识库文档 Repository。
 */
@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocumentEntity, Long> {

    /**
     * 查询知识库下的所有文档。
     */
    List<KnowledgeDocumentEntity> findByKbIdOrderByCreatedAtDesc(Long kbId);

    /**
     * 统计知识库文档数。
     */
    long countByKbId(Long kbId);

    /**
     * PostgreSQL ILIKE 全文搜索。
     */
    @Query("SELECT d FROM KnowledgeDocumentEntity d WHERE d.kbId = :kbId AND d.content ILIKE %:keyword%")
    List<KnowledgeDocumentEntity> searchByKeyword(@Param("kbId") Long kbId, @Param("keyword") String keyword);

    /**
     * PostgreSQL tsvector 全文搜索（分数排序）。
     */
    @Query(value = """
        SELECT *, ts_rank(to_tsvector('simple', content), plainto_tsquery('simple', :keyword)) AS rank
        FROM knowledge_document
        WHERE kb_id = :kbId AND to_tsvector('simple', content) @@ plainto_tsquery('simple', :keyword)
        ORDER BY rank DESC
        """, nativeQuery = true)
    List<KnowledgeDocumentEntity> fullTextSearch(@Param("kbId") Long kbId, @Param("keyword") String keyword);

    /**
     * 删除知识库下所有文档。
     */
    void deleteByKbId(Long kbId);
}
