INSERT INTO tbl_category(category_active, category_name, category_type) VALUES(true, 'TESTE', 'NORMAL')

INSERT INTO tbl_user (username, created_at, email, password, updated_at, user_role) VALUES('gabriel0001', NOW(), 'teste@gmail.com', 'aaa', null, 'ADMIN')

INSERT INTO tbl_user_visibility (user_visibility_field_name, user_visibility_is_visible) VALUES ('name', true);
INSERT INTO tbl_user_visibility (user_visibility_field_name, user_visibility_is_visible) VALUES ('SKU', true);