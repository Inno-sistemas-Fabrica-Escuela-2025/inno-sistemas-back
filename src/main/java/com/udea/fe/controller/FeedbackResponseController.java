package com.udea.fe.controller;

import com.udea.fe.DTO.FeedbackResponseDTO;
import com.udea.fe.service.FeedbackResponseService;
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
@RequestMapping("/api/feedback-response")
@AllArgsConstructor
@Tag(name = "FeedbackResponse", description = "Operaciones relacionadas con respuestas a feedbacks")
public class FeedbackResponseController {

    private final FeedbackResponseService feedbackResponseService;

    @Operation(
            summary = "Crear una nueva respuesta a feedback",
            description = "Permite crear una respuesta asociada a un feedback existente.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Respuesta creada exitosamente")
            }
    )
    @PostMapping("/create_feedbackResponse")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> createFeedbackResponse(
            @RequestBody FeedbackResponseDTO feedbackResponseDTO) {
        FeedbackResponseDTO created = feedbackResponseService.createFeedbackResponse(feedbackResponseDTO);
        EntityModel<FeedbackResponseDTO> resource = toModel(created);
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener respuesta por ID",
            description = "Devuelve una respuesta específica a feedback según su identificador."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> getFeedbackResponseById(
            @Parameter(description = "ID de la respuesta de feedback") @PathVariable Long id) {
        FeedbackResponseDTO dto = feedbackResponseService.getFeedbackResponseById(id);
        return ResponseEntity.ok(toModel(dto));
    }

    @Operation(
            summary = "Listar todas las respuestas a feedbacks",
            description = "Devuelve todas las respuestas almacenadas en el sistema."
    )
    @GetMapping("/all")
    public ResponseEntity<CollectionModel<EntityModel<FeedbackResponseDTO>>> getAllFeedbackResponses() {
        List<FeedbackResponseDTO> list = feedbackResponseService.getAllFeedbackResponses();
        List<EntityModel<FeedbackResponseDTO>> models = list.stream().map(this::toModel).toList();

        CollectionModel<EntityModel<FeedbackResponseDTO>> collection = CollectionModel.of(models);
        collection.add(linkTo(methodOn(FeedbackResponseController.class).getAllFeedbackResponses()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(
            summary = "Actualizar respuesta a feedback",
            description = "Actualiza los datos de una respuesta a feedback existente."
    )
    @PutMapping("/{id}/edit")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> update(
            @Parameter(description = "ID de la respuesta a actualizar") @PathVariable Long id,
            @RequestBody FeedbackResponseDTO dto) {
        FeedbackResponseDTO updated = feedbackResponseService.updateFeedbackResponse(id, dto);
        return ResponseEntity.ok(toModel(updated));
    }

    @Operation(
            summary = "Eliminar respuesta a feedback",
            description = "Elimina permanentemente una respuesta a feedback por su ID."
    )
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la respuesta a eliminar") @PathVariable Long id) {
        feedbackResponseService.deleteFeedbackResponse(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<FeedbackResponseDTO> toModel(FeedbackResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(FeedbackResponseController.class).getFeedbackResponseById(dto.getId())).withSelfRel(),
                linkTo(methodOn(FeedbackResponseController.class).getAllFeedbackResponses()).withRel("all"),
                linkTo(methodOn(FeedbackResponseController.class).update(dto.getId(), dto)).withRel("edit"),
                linkTo(methodOn(FeedbackResponseController.class).delete(dto.getId())).withRel("delete")
        );
    }
}
