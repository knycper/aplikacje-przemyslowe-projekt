INSERT INTO category (id, name, color) VALUES
(RANDOM_UUID(), 'Praca', '#1E90FF'),
(RANDOM_UUID(), 'Dom', '#6BCB77'),
(RANDOM_UUID(), 'Nauka', '#FFD93D'),
(RANDOM_UUID(), 'Zdrowie', '#FF6B6B'),
(RANDOM_UUID(), 'Finanse', '#845EC2'),
(RANDOM_UUID(), 'Zakupy', '#FF9F1C'),
(RANDOM_UUID(), 'Projekty', '#00C9A7'),
(RANDOM_UUID(), 'Ważne', '#D00000'),
(RANDOM_UUID(), 'Hobby', '#4D96FF'),
(RANDOM_UUID(), 'Inne', '#9E9E9E');

INSERT INTO users (id, username, password) VALUES (RANDOM_UUID(), 'kacper', '$2a$10$h3v82NtZ00j8q8wIDw0JrucZG22NOIAjAe3xSu1pcUDGhpJVzlv5O')
