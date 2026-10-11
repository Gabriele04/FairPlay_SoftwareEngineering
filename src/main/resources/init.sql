CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) DEFAULT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('PLAYER', 'PUBLISHER', 'ADMIN') NOT NULL
);

CREATE TABLE IF NOT EXISTS games (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    discount INT DEFAULT 0 CHECK (discount BETWEEN 0 AND 100),
    state ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    id_publisher INT NOT NULL,
    FOREIGN KEY (id_publisher) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS purchases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_player INT NOT NULL,
    id_game INT NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    price_paid DECIMAL(10, 2) NOT NULL,
    UNIQUE KEY unique_player_game (id_player, id_game),
    FOREIGN KEY (id_player) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_game) REFERENCES games(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_player INT NOT NULL,
    id_game INT NOT NULL,
    vote INT NOT NULL CHECK (vote BETWEEN 1 AND 5),
    text TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_player) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (id_game) REFERENCES games(id) ON DELETE CASCADE
);
