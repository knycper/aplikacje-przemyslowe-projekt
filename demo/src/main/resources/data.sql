INSERT INTO category (id, name, color) VALUES
(RANDOM_UUID(), 'Work', '#1E90FF'),
(RANDOM_UUID(), 'Home', '#6BCB77'),
(RANDOM_UUID(), 'Learning', '#FFD93D'),
(RANDOM_UUID(), 'Health', '#FF6B6B'),
(RANDOM_UUID(), 'Finances', '#845EC2'),
(RANDOM_UUID(), 'Shopping', '#FF9F1C'),
(RANDOM_UUID(), 'Projects', '#00C9A7'),
(RANDOM_UUID(), 'Important', '#D00000'),
(RANDOM_UUID(), 'Hobby', '#4D96FF');

INSERT INTO users (id, username, password) VALUES (RANDOM_UUID(), 'kacper', '$2a$10$h3v82NtZ00j8q8wIDw0JrucZG22NOIAjAe3xSu1pcUDGhpJVzlv5O');

INSERT INTO task (
    id, title, description, status, due_date,
    category_id, user_id, created_at, updated_at
)
VALUES (
           RANDOM_UUID(),
           'Write tests',
           'Add tests to the work project',
           'DONE',
           TIMESTAMP '2024-12-31 23:59:00',
           (SELECT id FROM category WHERE name = 'Work'),
           (SELECT id FROM users WHERE username = 'kacper'),
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO task (
    id, title, description, status, due_date,
    category_id, user_id, created_at, updated_at
)
VALUES (
           RANDOM_UUID(),
           'Weekly report',
           'Prepare a report for the last week',
           'TODO',
           TIMESTAMP '2024-12-31 23:59:00',
           (SELECT id FROM category WHERE name = 'Work'),
           (SELECT id FROM users WHERE username = 'kacper'),
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO task (
    id, title, description, status, due_date,
    category_id, user_id, created_at, updated_at
)
VALUES (
           RANDOM_UUID(),
           'Fix the faucet',
           'Leaking kitchen faucet',
           'IN_PROGRESS',
           TIMESTAMP '2025-01-05 18:00:00',
           (SELECT id FROM category WHERE name = 'Home'),
           (SELECT id FROM users WHERE username = 'kacper'),
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO task (
    id, title, description, status, due_date,
    category_id, user_id, created_at, updated_at
)
VALUES (
           RANDOM_UUID(),
           'Review Spring Boot',
           'REST, JPA, transactions',
           'TODO',
           TIMESTAMP '2025-01-15 20:00:00',
           (SELECT id FROM category WHERE name = 'Learning'),
           (SELECT id FROM users WHERE username = 'kacper'),
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO task (
    id, title, description, status, due_date,
    category_id, user_id, created_at, updated_at
)
VALUES (
           RANDOM_UUID(),
           'Clean up inbox',
           'Inbox zero',
           'DONE',
           TIMESTAMP '2024-12-31 23:59:00',
           NULL,
           (SELECT id FROM users WHERE username = 'kacper'),
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

