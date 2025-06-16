package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.entity.Feedback;
import com.udea.fe.entity.Submission;
import com.udea.fe.entity.User;
import com.udea.fe.exception.FeedbackNotFoundException;
import com.udea.fe.repository.FeedbackRepository;
import com.udea.fe.repository.SubmissionRepository;
import com.udea.fe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final SubmissionRepository submissionRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final NotificationService notificationService;

  public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
    Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);

    Submission submission = submissionRepository
      .findById(feedbackDTO.getSubmissionId())
      .orElseThrow(() ->
        new FeedbackNotFoundException("Entrega no encontrada con id: " + feedbackDTO.getSubmissionId())
      );
    feedback.setSubmission(submission);

    User createdBy = userRepository
      .findById(feedbackDTO.getCreatedById())
      .orElseThrow(() -> new FeedbackNotFoundException("Usuario no encontrado"));
    feedback.setCreatedBy(createdBy);

    if (feedbackDTO.getParentFeedbackId() != null) {
      Feedback parentFeedback = feedbackRepository
        .findById(feedbackDTO.getParentFeedbackId())
        .orElseThrow(() ->
          new FeedbackNotFoundException("Retroalimentación padre no encontrada con id: " + feedbackDTO.getParentFeedbackId())
        );
      feedback.setParentFeedback(parentFeedback);
    }

    feedback.setCreatedAt(LocalDateTime.now());

    Feedback savedFeedback = feedbackRepository.save(feedback);

    NotificationDTO notification = new NotificationDTO();
    notification.setUserId(submission.getUser().getUserId());
    notification.setMessage("Has recibido una nueva retroalimentación.");
    notification.setType("FEEDBACK");

    notificationService.createNotification(notification);

    return modelMapper.map(savedFeedback, FeedbackDTO.class);
  }

  public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
    return feedbackRepository
      .findById(id)
      .map(feedback -> {
        feedback.setComment(feedbackDTO.getComment());
        feedback.setRating(feedbackDTO.getRating());
        return modelMapper.map(feedbackRepository.save(feedback), FeedbackDTO.class);
      })
      .orElseThrow(() ->
        new FeedbackNotFoundException("Retroalimentación no encontrada con id: " + id)
      );
  }

  public FeedbackDTO getFeedbackById(Long id) {
    return feedbackRepository
      .findById(id)
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .orElseThrow(() ->
        new FeedbackNotFoundException("Retroalimentación no encontrada con id: " + id)
      );
  }

  public List<FeedbackDTO> getAllFeedbacks() {
    return feedbackRepository
      .findAll()
      .stream()
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .toList(); // toList() reemplaza collect(Collectors.toList())
  }

  public void deleteFeedback(Long id) {
    if (!feedbackRepository.existsById(id)) {
      throw new FeedbackNotFoundException("Retroalimentación no encontrada con id: " + id);
    }
    feedbackRepository.deleteById(id);
  }

  public List<FeedbackDTO> getFeedbacksBySubmissionId(Long submissionId) {
    return feedbackRepository
      .findAll()
      .stream()
      .filter(feedback ->
        feedback.getSubmission() != null &&
        feedback.getSubmission().getSubmissionId().equals(submissionId)
      )
      .map(feedback -> modelMapper.map(feedback, FeedbackDTO.class))
      .toList(); // también reemplazado aquí
  }
}
