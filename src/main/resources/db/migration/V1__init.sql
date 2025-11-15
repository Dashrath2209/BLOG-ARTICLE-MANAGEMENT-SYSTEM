-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    bio TEXT,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- Categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_slug (slug)
) ENGINE=InnoDB;

-- Tags table
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_name (name),
    INDEX idx_slug (slug)
) ENGINE=InnoDB;

-- Articles table
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    excerpt VARCHAR(255),
    featured_image VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    view_count INT DEFAULT 0,
    author_id BIGINT NOT NULL,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_author (author_id),
    INDEX idx_category (category_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

-- Comments table
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    article_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_article (article_id),
    INDEX idx_user (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;

-- Article-Tags join table
CREATE TABLE article_tags (
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    INDEX idx_article (article_id),
    INDEX idx_tag (tag_id)
) ENGINE=InnoDB;

-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, enabled)
VALUES ('admin', 'admin@blog.com', '$2a$10$xQ5rX1Yx6N6V2Vk8.pN9OuO2Z8sF3xK1wF6G7H8I9J0K1L2M3N4O5', 'Admin User', 'ROLE_ADMIN', TRUE);

-- Insert sample categories
INSERT INTO categories (name, slug, description) VALUES
('Technology', 'technology', 'Articles about technology and innovation'),
('Lifestyle', 'lifestyle', 'Lifestyle and personal development'),
('Travel', 'travel', 'Travel stories and guides'),
('Food', 'food', 'Recipes and food reviews');

-- Insert sample tags
INSERT INTO tags (name, slug) VALUES
('Java', 'java'),
('Spring Boot', 'spring-boot'),
('Tutorial', 'tutorial'),
('Tips', 'tips'),
('Guide', 'guide'),
('Review', 'review');