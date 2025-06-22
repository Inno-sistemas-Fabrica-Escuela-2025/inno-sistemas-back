package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackDTO;
import com.udea.fe.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/feedback")
@AllArgsConstructor
@Tag(name = "Feedback", description = "Gestión de feedbacks asociados a submissions")
public class FeedbackController {

  private final FeedbackService feedbackService;

  @Operation(summary = "Crear un nuevo feedback", responses = {
          @ApiResponse(responseCode = "201", description = "Feedback creado exitosamente")
  })
  @PostMapping("/create_feedback")
  public ResponseEntity<EntityModel<FeedbackDTO>> createFeedback(
          @RequestBody FeedbackDTO feedbackDTO) {
    FeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);
    EntityModel<FeedbackDTO> resource = toModel(createdFeedback);
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  @Operation(summary = "Actualizar un feedback existente")
  @PutMapping("/{id}/edit")
  public ResponseEntity<EntityModel<FeedbackDTO>> updateFeedback(
          @Parameter(description = "ID del feedback a actualizar") @PathVariable Long id,
          @RequestBody FeedbackDTO feedbackDTO
  ) {
    FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
    return ResponseEntity.ok(toModel(updatedFeedback));
  }

  @Operation(summary = "Obtener un feedback por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<FeedbackDTO>> getFeedbackById(
          @Parameter(description = "ID del feedback") @PathVariable Long id) {
    FeedbackDTO feedback = feedbackService.getFeedbackById(id);
    return ResponseEntity.ok(toModel(feedback));
  }

  @Operation(summary = "Obtener todos los feedbacks")
  @GetMapping("/all")
  public ResponseEntity<CollectionModel<EntityModel<FeedbackDTO>>> getAllFeedbacks() {
    List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
    List<EntityModel<FeedbackDTO>> feedbackModels = feedbacks.stream()
            .map(this::toModel)
            .toList();

    CollectionModel<EntityModel<FeedbackDTO>> collectionModel = CollectionModel.of(feedbackModels);
    collectionModel.add(linkTo(methodOn(FeedbackController.class).getAllFeedbacks()).withSelfRel());

    return ResponseEntity.ok(collectionModel);
  }

  @Operation(summary = "Eliminar un feedback por su ID")
  @DeleteMapping("/{id}/delete")
  public ResponseEntity<Void> deleteFeedback(
          @Parameter(description = "ID del feedback a eliminar") @PathVariable Long id) {
    feedbackService.deleteFeedback(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Obtener feedbacks por ID de submission")
  @GetMapping("/submission/{submissionId}")
  public ResponseEntity<CollectionModel<EntityModel<FeedbackDTO>>> getFeedbacksBySubmission(
          @Parameter(description = "ID de la submission") @PathVariable Long submissionId) {
    List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksBySubmissionId(submissionId);

    List<EntityModel<FeedbackDTO>> feedbackModels = feedbacks.stream()
            .map(this::toModel)
            .toList();

    CollectionModel<EntityModel<FeedbackDTO>> collectionModel = CollectionModel.of(feedbackModels);
    collectionModel.add(linkTo(methodOn(FeedbackController.class).getFeedbacksBySubmission(submissionId)).withSelfRel());

    return ResponseEntity.ok(collectionModel);
  }

  private EntityModel<FeedbackDTO> toModel(FeedbackDTO feedback) {
    return EntityModel.of(feedback,
            linkTo(methodOn(FeedbackController.class).getFeedbackById(feedback.getFeedbackId())).withSelfRel(),
            linkTo(methodOn(FeedbackController.class).getAllFeedbacks()).withRel("all-feedbacks"),
            linkTo(methodOn(FeedbackController.class).getFeedbacksBySubmission(feedback.getSubmissionId())).withRel("feedbacks-by-submission"),
            linkTo(methodOn(FeedbackController.class).updateFeedback(feedback.getFeedbackId(), feedback)).withRel("edit"),
            linkTo(methodOn(FeedbackController.class).deleteFeedback(feedback.getFeedbackId())).withRel("delete")
    );
  }
}
