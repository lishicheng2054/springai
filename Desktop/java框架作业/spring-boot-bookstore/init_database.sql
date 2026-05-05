-- ============================================
-- Spring Boot Bookstore 数据库初始化脚本
-- ============================================
-- 创建数据库
CREATE DATABASE IF NOT EXISTS books DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE books;

-- ============================================
-- 表1: tbbook (MyBatis注解/XML方式使用 - Book1/Book2)
-- ============================================
DROP TABLE IF EXISTS tbbook;
CREATE TABLE tbbook (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) NOT NULL COMMENT '作者',
    press VARCHAR(100) COMMENT '出版社',
    price DOUBLE COMMENT '价格'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表-MyBatis方式';

-- ============================================
-- 表2: tb_book (JPA方式使用 - Book3)
-- ============================================
DROP TABLE IF EXISTS tb_book;
CREATE TABLE tb_book (
    book_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
    title VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) NOT NULL COMMENT '作者',
    press VARCHAR(100) COMMENT '出版社',
    price DOUBLE COMMENT '价格'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表-JPA方式';

-- ============================================
-- 表3: comment (评论表)
-- ============================================
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    c_author VARCHAR(50) NOT NULL COMMENT '评论作者',
    book_id INT NOT NULL COMMENT '关联图书ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书评论表';

-- ============================================
-- 插入测试数据 - tbbook表
-- ============================================
INSERT INTO tbbook (title, author, press, price) VALUES
('Java编程思想', 'Bruce Eckel', '机械工业出版社', 108.00),
('Spring Boot实战', '陈旭', '人民邮电出版社', 59.00),
('深入理解Java虚拟机', '周志明', '机械工业出版社', 89.00),
('MySQL高性能优化', 'Baron Schwartz', '电子工业出版社', 128.00),
('Python编程从入门到实践', 'Eric Matthes', '人民邮电出版社', 69.00),
('算法导论', 'Thomas H.Cormen', '机械工业出版社', 138.00),
('数据结构与算法分析', 'Mark Allen Weiss', '机械工业出版社', 55.00),
('设计模式', 'Erich Gamma', '机械工业出版社', 35.00);

-- ============================================
-- 插入测试数据 - tb_book表 (JPA)
-- ============================================
INSERT INTO tb_book (title, author, press, price) VALUES
('Java核心技术', 'Cay S.Horstmann', '机械工业出版社', 118.00),
('Spring实战', 'Craig Walls', '人民邮电出版社', 59.00),
('Java并发编程实战', 'Brian Goetz', '机械工业出版社', 69.00),
('Redis设计与实现', '黄健宏', '机械工业出版社', 79.00),
('Linux运维实战', '马哥教育', '电子工业出版社', 88.00);

-- ============================================
-- 插入测试数据 - comment表 (评论关联tbbook)
-- ============================================
INSERT INTO comment (content, c_author, book_id) VALUES
('这本书非常适合Java初学者，内容深入浅出', '张三', 1),
('经典的Java书籍，值得反复阅读', '李四', 1),
('Spring Boot入门首选，讲解很清晰', '王五', 2),
('内容有点简单，适合入门', '赵六', 2),
('JVM领域最权威的书籍', '钱七', 3),
('对理解JVM非常有帮助', '孙八', 3),
('MySQL优化必读书籍', '周九', 4),
('数据库调优的圣经', '吴十', 4),
('Python入门经典，强烈推荐', '郑一', 5),
('适合零基础学习Python', '王二', 5);

-- ============================================
-- 插入测试数据 - comment表 (评论关联tb_book)
-- ============================================
INSERT INTO comment (content, c_author, book_id) VALUES
('Java核心技术卷很详细，适合进阶学习', '读者A', 1),
('Spring框架必读书籍，讲解透彻', '读者B', 2),
('并发编程非常经典，值得推荐', '读者C', 3),
('Redis源码分析很透彻，受益匪浅', '读者D', 4),
('运维实战内容丰富，实用性很强', '读者E', 5);

-- ============================================
-- 数据验证查询
-- ============================================
SELECT 'tbbook表数据' AS 表名;
SELECT * FROM tbbook;

SELECT 'tb_book表数据' AS 表名;
SELECT * FROM tb_book;

SELECT 'comment表数据' AS 表名;
SELECT * FROM comment;

-- ============================================
-- 测试图书评论关联查询
-- ============================================
SELECT '图书评论关联查询测试' AS 说明;
SELECT b.id, b.title, b.author, c.content, c.c_author
FROM tbbook b
LEFT JOIN comment c ON b.id = c.book_id
WHERE b.id = 1;