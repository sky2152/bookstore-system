import pymysql
import os

# Database connection details - TODO: Make these configurable (e.g., from environment variables)
DB_HOST = os.getenv("DB_HOST", "localhost")
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "123456") # Replace with your MySQL root password
DB_NAME = os.getenv("DB_NAME", "course_knowledge_graph")

DDL_STATEMENTS = """
-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` ENUM('teacher', 'student') NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
  `course_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `course_name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `teacher_id` VARCHAR(36) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`teacher_id`) REFERENCES `user`(`user_id`)
);

-- 知识点元数据表 (关系型数据库中存储元数据)
CREATE TABLE IF NOT EXISTS `knowledge_point_metadata` (
  `kp_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `course_id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(50), -- e.g., '概念', '公式', '定理'
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`course_id`)
);

-- 文档表
CREATE TABLE IF NOT EXISTS `document` (
  `doc_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `course_id` VARCHAR(36) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(255) NOT NULL, -- 实际存储路径或引用
  `file_type` VARCHAR(50) NOT NULL,
  `content_preview` TEXT, -- 存储部分内容预览或摘要
  `uploaded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`course_id`)
);

-- 学习掌握状态表
CREATE TABLE IF NOT EXISTS `mastery_status` (
  `mastery_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `user_id` VARCHAR(36) NOT NULL,
  `kp_id` VARCHAR(36) NOT NULL,
  `mastered` BOOLEAN DEFAULT FALSE,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`),
  FOREIGN KEY (`kp_id`) REFERENCES `knowledge_point_metadata`(`kp_id`)
);

-- 反馈表
CREATE TABLE IF NOT EXISTS `feedback` (
  `feedback_id` VARCHAR(36) NOT NULL PRIMARY KEY,
  `user_id` VARCHAR(36) NOT NULL,
  `kp_id` VARCHAR(36),
  `comment_text` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`),
  FOREIGN KEY (`kp_id`) REFERENCES `knowledge_point_metadata`(`kp_id`)
);
"""

def create_database_and_tables():
    connection = None
    try:
        # Connect to MySQL server (without specifying a database yet)
        connection = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        print(f"Connected to MySQL server at {DB_HOST}")

        with connection.cursor() as cursor:
            # Create database if it doesn't exist
            cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_NAME} DEFAULT CHARACTER SET 'utf8mb4'")
            print(f"Database '{DB_NAME}' ensured to exist.")

        # Reconnect to the newly created/ensured database
        connection.close()
        connection = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        print(f"Connected to database '{DB_NAME}'.")

        with connection.cursor() as cursor:
            # Execute DDL statements
            for statement in DDL_STATEMENTS.split(';'):
                stripped_statement = statement.strip()
                if stripped_statement:
                    try:
                        cursor.execute(stripped_statement)
                        print(f"Executed: {stripped_statement[:70]}...")
                    except pymysql.err.OperationalError as e:
                        if e.args[0] == 1050: # Table already exists
                            print(f"Table already exists, skipping: {stripped_statement[:70]}...")
                        else:
                            raise e
            connection.commit()
        print("All DDL statements executed successfully.")

    except pymysql.Error as e:
        print(f"Error connecting to MySQL or executing DDL: {e}")
        if connection:
            connection.rollback()
    finally:
        if connection:
            connection.close()
            print("MySQL connection closed.")

if __name__ == "__main__":
    create_database_and_tables()
