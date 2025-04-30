package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

/*
Не стал оформлять ReviewDbStorage, так как в остальной программе такие классы лишь делигируют вызовы классу-репозиторию.
В моем понимании ReviewDbStorage по заданию предыдущего спринта и есть ReviewRepository и т.п.
 */

@Qualifier("reviewRepository")
@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    private static final String GET_FILM_REVIEWS =
            "SELECT r.review_id, " +
                    "r.content, " +
                    "r.is_positive, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "(SELECT COALESCE(SUM(CASE WHEN rl.is_like THEN 1 ELSE -1 END), 0) " +
                    "FROM review_likes rl " +
                    "WHERE rl.review_id = r.review_id) AS useful " +
                    "FROM reviews r " +
                    "WHERE r.film_id = ? " +
                    "ORDER BY useful DESC, r.review_id ASC " +
                    "LIMIT ?";
    private static final String GET_REVIEW_BY_REVIEW_ID =
            "SELECT r.review_id, " +
                    "r.content, " +
                    "r.is_positive, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "(SELECT COALESCE(SUM(CASE WHEN rl.is_like THEN 1 ELSE -1 END), 0) " +
                    "FROM review_likes rl " +
                    "WHERE rl.review_id = r.review_id) AS useful " +
                    "FROM reviews r " +
                    "WHERE r.review_id = ?";
    private static final String GET_ALL_REVIEWS =
            "SELECT r.review_id, " +
                    "r.content, " +
                    "r.is_positive, " +
                    "r.user_id, " +
                    "r.film_id, " +
                    "(SELECT COALESCE(SUM(CASE WHEN rl.is_like THEN 1 ELSE -1 END), 0) " +
                    "FROM review_likes rl " +
                    "WHERE rl.review_id = r.review_id) AS useful " +
                    "FROM reviews r " +
                    "ORDER BY useful DESC, r.review_id ASC " +
                    "LIMIT ?";

    private static final String POST_REVIEW = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
            "VALUES (?, ?, ?, ?)";

    private static final String PUT_REVIEW = "UPDATE reviews " +
            "SET content = ?, is_positive = ? WHERE review_id = ?";

    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE review_id = ?";

    private static final String RATE_REVIEW = "MERGE INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)";
    private static final String REMOVE_RATE = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";

    private static final String CHECK_RATING = "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> getFilmReviews(long filmId, long count) {
        return findMany(GET_FILM_REVIEWS, filmId, count);
    }

    @Override
    public List<Review> getAllReviews(long count) {
        return findMany(GET_ALL_REVIEWS, count);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        return findOne(GET_REVIEW_BY_REVIEW_ID, reviewId);
    }

    @Override
    public Review addReview(Review newReview) {
        long id = insert(
                POST_REVIEW,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getUserId(),
                newReview.getFilmId()
        );
        newReview.setReviewId(id);

        return newReview;
    }

    @Override
    public boolean deleteReview(long reviewId) {
        return delete(DELETE_REVIEW, reviewId);
    }

    @Override
    public boolean likeReview(long reviewId, long userId) {
        int rowsAdded = jdbc.update(RATE_REVIEW, reviewId, userId, true);
        return rowsAdded > 0;
    }

    @Override
    public boolean removeLike(long reviewId, long userId) {
        int rowsRemoved = jdbc.update(REMOVE_RATE, reviewId, userId);
        return rowsRemoved > 0;
    }

    @Override
    public boolean dislikeReview(long reviewId, long userId) {
        int rowsAdded = jdbc.update(RATE_REVIEW, reviewId, userId, false);
        return rowsAdded > 0;
    }

    @Override
    public boolean removeDislike(long reviewId, long userId) {
        int rowsRemoved = jdbc.update(REMOVE_RATE, reviewId, userId);
        return rowsRemoved > 0;
    }

    @Override
    public Review updateReview(Review newReview) {
        update(PUT_REVIEW,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getReviewId());
        return newReview;
    }
}
