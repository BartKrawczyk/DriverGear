INSERT INTO drivergear.roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO drivergear.roles (id, name) VALUES (2, 'ROLE_ADMIN');

INSERT INTO drivergear.users (id, username, password) VALUES (1, 'testuser', '$2a$10$ftimIDkoY4NiIQoi.gFjkOMDNsO4b2Akt.lyrije0FT.B6tL4DMMa');

INSERT INTO drivergear.user_roles (user_id, role_id) VALUES (1, 1);