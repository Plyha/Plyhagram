INSERT INTO "user" (username,name,lastname,password, email, bio,created_date)
VALUES ('admin', 'ilya', 'khlevnoy', '$2a$04$l6jf/IelD8EcKEx0z5LJFur01DtdBcTLUxfiq79X1GF2hgJdmIeEW', 'mail@mail.ru', 'about me',now()::timestamp);
