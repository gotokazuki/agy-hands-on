DO $$
DECLARE
    admin_group_id UUID;
    admin_user_id UUID;
BEGIN
    INSERT INTO groups (name, description)
    VALUES ('Administrators', 'System administrators with full access')
    RETURNING id INTO admin_group_id;

    INSERT INTO users (email, password_hash, last_name, first_name)
    VALUES (
      'admin@example.com',
      '$2a$10$9LolXMEis/2eAPc6WVsB5OfNDU3WTsRlu5pu33/wFRjGLfqKzc/Aa',
      'Admin',
      'System'
    )
    RETURNING id INTO admin_user_id;

    INSERT INTO group_permissions (group_id, permission_id)
    SELECT admin_group_id, id FROM permissions;

    INSERT INTO user_groups (user_id, group_id)
    VALUES (admin_user_id, admin_group_id);

END $$;
