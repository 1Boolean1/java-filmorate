DELETE
FROM FILMS;
DELETE
FROM LIKES;
DELETE
FROM GENRE;
DELETE
FROM RATING;
DELETE
FROM USERS;
DELETE
FROM FILM_GENRE;
DELETE
FROM FRIENDSHIP;

INSERT INTO RATING(rating_id, rating_name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO USERS (id, email, login, name, birthday)
VALUES (1, 'test@user.com', 'testlogin', 'Test User Name', '1991-11-11');

INSERT INTO USERS (id, email, login, name, birthday)
VALUES (2, 'test1@user.com', 'test2login', 'Another User', '1992-11-11');

INSERT INTO FILMS (id, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES (1, 'film1', 'its film1', '2010-10-10', '100', '1'),
       (2, 'film2', 'its film2', '2020-12-20', '200', '2');

INSERT INTO LIKES(user_id, film_id)
VALUES (1, 1),
       (2, 1),
       (2, 2);

INSERT INTO GENRE (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');