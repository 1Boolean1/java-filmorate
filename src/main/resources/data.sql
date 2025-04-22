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
delete
from FILM_DIRECTORS;
delete
from DIRECTORS;

insert into Users (email, login, name, birthday)
values ('test@user.com', 'testlogin', 'Test User Name', '1991-11-11');

insert into Users (email, login, name, birthday)
values ('test1@user.com', 'test2login', 'Another User', '1992-11-11');

insert into Rating(rating_id, rating_name)
values (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');


insert into Genre (id, name)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');