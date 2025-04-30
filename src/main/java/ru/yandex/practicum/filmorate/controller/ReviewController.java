package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDto> getFilmReviews(@RequestParam(defaultValue = "0") long filmId,
                                          @RequestParam(defaultValue = "10")
                                          @Positive(message = "Count must be positive") long count) {
        log.info("Received GET {} reviews for film with ID = {}", count, filmId);
        return reviewService.getFilmReviews(filmId, count);
    }

    @GetMapping("{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto getReviewById(@PathVariable int reviewId) {
        log.info("Received GET review with ID = {}", reviewId);
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@RequestBody @Valid NewReviewRequest request) {
        log.info("Received POST new review: {}", request);
        return reviewService.addReview(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto updateReview(@RequestBody @Valid UpdateReviewRequest request) {
        log.info("Received UPDATE review: {}", request);
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable long reviewId) {
        log.info("Received DELETE review with ID = {}", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void likeReview(@PathVariable long reviewId,
                           @PathVariable long userId) {
        log.info("Received LIKE review with reviewId = {} by user with userId = {}", reviewId, userId);
        reviewService.likeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void dislikeReview(@PathVariable long reviewId,
                              @PathVariable long userId) {
        log.info("Received DISLIKE review with reviewId = {} by user with userId = {}", reviewId, userId);
        reviewService.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable long reviewId,
                           @PathVariable long userId) {
        log.info("Received REMOVE LIKE from review with reviewId = {} by user with userId = {}", reviewId, userId);
        reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable long reviewId,
                              @PathVariable long userId) {
        log.info("Received REMOVE DISLIKE from review with reviewId = {} by user with userId = {}", reviewId, userId);
        reviewService.removeDislike(reviewId, userId);
    }
}
