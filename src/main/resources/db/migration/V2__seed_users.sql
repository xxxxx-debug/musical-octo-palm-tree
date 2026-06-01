-- Password for all seed users: password123
INSERT INTO users (id, name, date_of_birth, password) VALUES
    (1, 'Иван Иванов', '1990-03-15', '$2a$10$lNjOuN0ER7bH9FLyzy2uguLKcwUajk48TlfVEBG5hp9.q3YfysnxC'),
    (2, 'Мария Петрова', '1985-11-22', '$2a$10$lNjOuN0ER7bH9FLyzy2uguLKcwUajk48TlfVEBG5hp9.q3YfysnxC'),
    (3, 'Алексей Сидоров', '1993-05-01', '$2a$10$lNjOuN0ER7bH9FLyzy2uguLKcwUajk48TlfVEBG5hp9.q3YfysnxC');

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

INSERT INTO account (user_id, balance, max_balance) VALUES
    (1, 100.00, 207.00),
    (2, 500.00, 1035.00),
    (3, 1000.00, 2070.00);

INSERT INTO email_data (user_id, email) VALUES
    (1, 'ivan@example.com'),
    (1, 'ivan.work@example.com'),
    (2, 'maria@example.com'),
    (3, 'alex@example.com'),
    (3, 'alex.personal@example.com');

INSERT INTO phone_data (user_id, phone) VALUES
    (1, '79201111111'),
    (2, '79202222222'),
    (2, '79202222223'),
    (3, '79203333333');
