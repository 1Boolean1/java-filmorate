INSERT INTO RATING(rating_id, rating_name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO USERS (email, login, name, birthday)
VALUES ('test@user.com', 'testlogin', 'Test User Name', '1991-11-11');

INSERT INTO USERS (email, login, name, birthday)
VALUES ('test1@user.com', 'test2login', 'Another User', '1992-11-11');

INSERT INTO USERS (email, login, name, birthday)
VALUES ('test2@user.com', 'test3login', 'Another User1', '1993-11-11');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES ('film1', 'its film1', '2010-10-10', '100', '1'),
       ('film2', 'its film2', '2020-12-20', '200', '2'),
       ('film3', 'its film3', '2003-03-30', '300', '3'),
       ('film4', 'its film4', '2024-04-14', '400', '4');

INSERT INTO LIKES(user_id, film_id)
VALUES (1, 1),
       (1, 3),
       (2, 1),
       (2, 2),
       (2, 4),
       (3, 1),
       (3, 2),
       (3, 4);

INSERT INTO GENRE (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');