-- :name upsert-user! :<! :1
INSERT INTO users (email)
VALUES
 (
 :email
 )
ON CONFLICT (email)
DO
 UPDATE
   SET email = :email
RETURNING id

-- :name get-user-by-email :? :1
SELECT * from users
WHERE email = :email