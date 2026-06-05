package com.example.interviewbeginner.modules.knowledgebase;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文档解析服务。
 * 使用 Apache Tika 解析 PDF、Word、TXT 等格式，提取纯文本内容。
 */
@Slf4j
@Service
public class DocumentParseService {

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20MB
    private final Tika tika = new Tika();

    /**
     * 解析上传文件，返回纯文本内容。
     *
     * @param file 上传文件
     * @return 解析结果（文件名、内容类型、纯文本内容）
     */
    public ParseResult parse(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件过大，最大支持 20MB");
        }

        try (InputStream in = file.getInputStream()) {
            // Tika 自动检测文件类型并提取文本
            String text = tika.parseToString(in);

            if (text == null || text.isBlank()) {
                log.warn("No text extracted from file: {}", file.getOriginalFilename());
                text = "[无法提取文本内容，文件可能为纯图片格式]";
            }

            // 检测 MIME 类型
            String contentType = tika.detect(file.getOriginalFilename());

            log.info("Parsed file: name={}, type={}, size={}, textLen={}",
                    file.getOriginalFilename(), contentType, file.getSize(),
                    text != null ? text.length() : 0);

            return new ParseResult(
                    file.getOriginalFilename(), contentType, file.getSize(), text
            );
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文档解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析结果。
     */
    public record ParseResult(
            String fileName,
            String contentType,
            long fileSize,
            String text
    ) {
    }
}
