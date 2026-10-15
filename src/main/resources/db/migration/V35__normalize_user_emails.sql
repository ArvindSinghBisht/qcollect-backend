-- Store one canonical email per person when existing rows do not already
-- collide after trimming and lower-casing. Applied migrations are left unchanged.
-- If case-variant duplicates already exist, this migration does not rewrite them
-- and does not add the stricter index, so production users are not deleted.

DO $$
DECLARE
    collision_count integer;
BEGIN
    SELECT COUNT(*) INTO collision_count
    FROM (
        SELECT lower(btrim(email)) AS normalized
        FROM users
        WHERE email IS NOT NULL
        GROUP BY lower(btrim(email))
        HAVING COUNT(*) > 1
    ) collisions;

    IF collision_count = 0 THEN
        UPDATE users
        SET email = lower(btrim(email))
        WHERE email IS DISTINCT FROM lower(btrim(email));

        CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_normalized
            ON users (lower(btrim(email)));
    ELSE
        RAISE NOTICE
            'Skipped user email normalization because % case-insensitive collision group(s) exist',
            collision_count;
    END IF;
END $$;
