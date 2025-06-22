package com.udea.fe.controller;

import com.udea.fe.DTO.NotificationDTO;
import com.udea.fe.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@Tag(name = "Notificaciones", description = "Operaciones sobre notificaciones de los usuarios")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Obtener una notificación por su ID",
            description = "Devuelve los detalles de una notificación específica.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<NotificationDTO>> getById(
            @Parameter(description = "ID de la notificación") @PathVariable Long id) {
        NotificationDTO dto = notificationService.getById(id);
        return ResponseEntity.ok(toModel(dto));
    }

    @Operation(
            summary = "Obtener notificaciones del usuario autenticado",
            description = "Devuelve todas las notificaciones asociadas al usuario que realiza la petición."
    )
    @GetMapping("/user/")
    public ResponseEntity<CollectionModel<EntityModel<NotificationDTO>>> getByUserId(Principal principal) {
        String userEmail = principal.getName();
        List<NotificationDTO> list = notificationService.getByUser(userEmail);
        List<EntityModel<NotificationDTO>> models = list.stream().map(this::toModel).toList();

        CollectionModel<EntityModel<NotificationDTO>> collection = CollectionModel.of(models);
        collection.add(linkTo(methodOn(NotificationController.class).getByUserId(principal)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(
            summary = "Eliminar una notificación",
            description = "Elimina una notificación específica por su ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Notificación eliminada"),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
            }
    )
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la notificación a eliminar") @PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Marcar una notificación como leída",
            description = "Actualiza el estado de una notificación a leída.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
                    @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
            }
    )
    @PatchMapping("/{id}/read")
    public ResponseEntity<EntityModel<NotificationDTO>> markAsRead(
            @Parameter(description = "ID de la notificación a marcar como leída") @PathVariable Long id) {
        NotificationDTO dto = notificationService.markAsRead(id);
        return ResponseEntity.ok(toModel(dto));
    }

    private EntityModel<NotificationDTO> toModel(NotificationDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(NotificationController.class).getById(dto.getId())).withSelfRel(),
                linkTo(methodOn(NotificationController.class).delete(dto.getId())).withRel("delete"),
                linkTo(methodOn(NotificationController.class).markAsRead(dto.getId())).withRel("mark-as-read")
        );
    }
}
