package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.service.FeedbackService;
import java.util.List;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
public class FeedbackController {

  private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

  private final FeedbackService feedbackService;

  @PostMapping("/create_feedback")
  public ResponseEntity<FeedbackDTO> createFeedback(@RequestBody FeedbackDTO feedbackDTO) {
    FeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);
    return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
  }

  @PutMapping("/{id}/edit")
  public ResponseEntity<FeedbackDTO> updateFeedback(
    @PathVariable Long id,
    @RequestBody FeedbackDTO feedbackDTO
  ) {
    FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
    return ResponseEntity.ok(updatedFeedback);
  }

  @GetMapping("/{id}")
  public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
    FeedbackDTO feedback = feedbackService.getFeedbackById(id);
    return ResponseEntity.ok(feedback);
  }

  @GetMapping("/all")
  public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
    List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
    return ResponseEntity.ok(feedbacks);
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
    feedbackService.deleteFeedback(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/submission/{submissionId}")
  public ResponseEntity<List<FeedbackDTO>> getFeedbacksBySubmission(
    @PathVariable Long submissionId
  ) {
    logger.info("Llamada a getFeedbacksBySubmission con submissionId: {}", submissionId);
    try {
      List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksBySubmissionId(submissionId);
      return ResponseEntity.ok(feedbacks);
    } catch (Exception e) {
      logger.error("Error en getFeedbacksBySubmission: {}", e.getMessage(), e);
      return ResponseEntity.badRequest().build();
    }
  }
}
