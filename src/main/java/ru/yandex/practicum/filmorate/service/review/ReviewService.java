package ru.yandex.practicum.filmorate.service.review;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.mappers.ReviewMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.feed.FeedService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;

    public ReviewService(@Qualifier("reviewRepository") ReviewStorage reviewStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public List<ReviewDto> getFilmReviews(long filmId, long count) {
        if (count <= 0) {
            throw new ParameterNotValidException("Count", "Must be greater than 0");
        }

        if (filmId == 0) {
            log.info("Showing {} reviews for all films", count);
            return reviewStorage.getAllReviews(count)
                    .stream()
                    .map(ReviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        }

        Film film = filmStorage.getFilmById(filmId);
        log.info("Showing {} reviews for film: {}", count, film);

        return reviewStorage.getFilmReviews(filmId, count)
                .stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto getReviewById(long reviewId) {
        Optional<Review> optionalReview = reviewStorage.getReviewById(reviewId);
        return optionalReview.map(review -> {
                    ReviewDto reviewDto = ReviewMapper.mapToReviewDto(review);
                    log.info("Found review: {}", reviewDto);
                    return reviewDto;
                })
                .orElseThrow(() -> new NotFoundException("Review with ID " + reviewId + " not found"));
    }

    public ReviewDto addReview(@Valid NewReviewRequest request) {
        /*
        Ручная обработка из-за специфичных postman тестов
         */
        if (request.getUserId() == null) {
            log.error("Parameter UserId cannot be empty");
            throw new ParameterNotValidException("UserId", "Cannot be empty");
        }

        if (request.getFilmId() == null) {
            log.error("Parameter FilmId cannot be empty");
            throw new ParameterNotValidException("FilmId", "Cannot be empty");
        }

        if (request.getUserId() < 0) {
            log.error("User id must be greater than 0");
            throw new NotFoundException("User id must be greater than 0");
        }

        if (request.getFilmId() < 0) {
            log.error("Film id must be greater than 0");
            throw new NotFoundException("Film id must be greater than 0");
        }

        Review review = ReviewMapper.mapToReview(request);
        ReviewDto reviewDto = ReviewMapper.mapToReviewDto(reviewStorage.addReview(review));
        feedService.logEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        log.info("Review successfully added");
        return reviewDto;
    }

    public ReviewDto updateReview(@Valid UpdateReviewRequest request) {
        Review updatedReview = reviewStorage.getReviewById(request.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(review, request))
                .orElseThrow(() -> new NotFoundException("Review with ID " + request.getReviewId() + " not found"));
        reviewStorage.updateReview(updatedReview);
        feedService.logEvent(updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE, updatedReview.getReviewId());
        return ReviewMapper.mapToReviewDto(updatedReview);
    }

    public void deleteReview(long reviewId) {
        ReviewDto reviewDto = getReviewById(reviewId);
        log.info("Deleting review: {}", reviewDto);

        boolean isDeleted = reviewStorage.deleteReview(reviewId);
        if (isDeleted) {
            feedService.logEvent(reviewDto.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId);
            log.info("Review deleted successfully");
        } else {
            throw new InternalServerException("Review was not deleted due to internal server error");
        }
    }

    public void likeReview(long reviewId, long userId) {
        checkReviewAndUser(reviewId, userId);
        log.info("User with userId = {} wants to like review with reviewId = : {}", userId, reviewId);

        boolean isSuccessful = reviewStorage.likeReview(reviewId, userId);

        if (isSuccessful) {
            log.info("User liked review successfully");
        } else {
            throw new InternalServerException("Failed to like review due to internal server error");
        }
    }

    public void removeLike(long reviewId, long userId) {
        checkReviewAndUser(reviewId, userId);
        log.info("User with userId = {} wants to remove like from review with reviewId = {}", userId, reviewId);

        boolean isSuccessful = reviewStorage.removeLike(reviewId, userId);

        if (isSuccessful) {
            log.info("Like removed successfully");
        } else {
            throw new InternalServerException("Failed to remove like due to internal server error");
        }
    }

    public void dislikeReview(long reviewId, long userId) {
        checkReviewAndUser(reviewId, userId);
        log.info("User with userId = {} wants to dislike review with reviewId = {}", userId, reviewId);

        boolean isSuccessful = reviewStorage.dislikeReview(reviewId, userId);

        if (isSuccessful) {
            log.info("User disliked review successfully");
        } else {
            throw new InternalServerException("Failed to dislike review due to internal server error");
        }
    }

    public void removeDislike(long reviewId, long userId) {
        checkReviewAndUser(reviewId, userId);
        log.info("User with userId = {} wants to remove dislike from review with reviewId = {}", userId, reviewId);

        boolean isSuccessful = reviewStorage.removeDislike(reviewId, userId);

        if (isSuccessful) {
            log.info("Dislike removed successfully");
        } else {
            throw new InternalServerException("Failed to remove dislike due to internal server error");
        }
    }

    private void checkReviewAndUser(long reviewId, long userId) {
        getReviewById(reviewId);
        userStorage.getUserById(userId);
    }
}
