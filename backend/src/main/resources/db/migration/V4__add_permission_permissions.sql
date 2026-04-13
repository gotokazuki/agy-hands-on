DO $$
DECLARE
    admin_group_id UUID;
    perm_read_id UUID;
    perm_write_id UUID;
BEGIN
    -- Get the Administrators group ID
    SELECT id INTO admin_group_id FROM groups WHERE name = 'Administrators' LIMIT 1;

    -- Insert new permissions and capture their IDs
    INSERT INTO permissions (resource, action, name)
    VALUES ('permission', 'read', 'permission:read')
    RETURNING id INTO perm_read_id;

    INSERT INTO permissions (resource, action, name)
    VALUES ('permission', 'write', 'permission:write')
    RETURNING id INTO perm_write_id;

    -- Link new permissions to admin group
    IF admin_group_id IS NOT NULL THEN
        INSERT INTO group_permissions (group_id, permission_id)
        VALUES (admin_group_id, perm_read_id), (admin_group_id, perm_write_id);
    END IF;

END $$;
