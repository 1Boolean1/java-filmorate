delete
from FILMS;
delete
from LIKES;
delete
from GENRE;
delete
from RATING;
delete
from USERS;
delete
from FILM_GENRE;
delete
from FRIENDSHIP;

insert into RATING(rating_id, rating_name)
values (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

insert into USERS (email, login, name, birthday)
values ('test@user.com', 'testlogin', 'Test User Name', '1991-11-11');

insert into USERS ( email, login, name, birthday)
values ('test1@user.com', 'test2login', 'Another User', '1992-11-11');

insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
values ('film1', 'its film1', '2010-10-10', '100', '1'),
       ('film2', 'its film2', '2020-12-20', '200', '2');

insert into LIKES(user_id, film_id)
values (1, 1),
       (2, 1),
       (2, 2);

insert into GENRE (name)
values ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');