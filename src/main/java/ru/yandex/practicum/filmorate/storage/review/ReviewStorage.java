package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    List<Review> getFilmReviews(long filmId, long count);

    List<Review> getAllReviews(long count);

    Optional<Review> getReviewById(long reviewId);

    Review addReview(Review newReview);

    Review updateReview(Review newReview);

    boolean deleteReview(long reviewId);

    boolean likeReview(long reviewId, long likeId);

    boolean removeLike(long reviewId, long likeId);

    boolean dislikeReview(long reviewId, long likeId);

    boolean removeDislike(long reviewId, long dislikeId);
}
