package com.udea.fe.service;

import com.udea.fe.DTO.FeedbackResponseDTO;
import com.udea.fe.entity.Feedback;
import com.udea.fe.entity.FeedbackResponse;
import com.udea.fe.entity.User;
import com.udea.fe.exception.FeedbackResponseNotFoundException;
import com.udea.fe.repository.FeedbackRepository;
import com.udea.fe.repository.FeedbackResponseRepository;
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
public class FeedbackResponseService {

    private final FeedbackResponseRepository feedbackResponseRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public FeedbackResponseDTO createFeedbackResponse(FeedbackResponseDTO dto) {
        FeedbackResponse response = modelMapper.map(dto, FeedbackResponse.class);

        Feedback feedback = feedbackRepository.findById(dto.getFeedbackId())
                .orElseThrow(() -> new FeedbackResponseNotFoundException("Feedback no encontrado con id: " + dto.getFeedbackId()));
        response.setFeedback(feedback);

        User createdBy = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new FeedbackResponseNotFoundException("Usuario no encontrado con id: " + dto.getCreatedById()));
        response.setCreatedBy(createdBy);

        response.setResponseDate(LocalDateTime.now());

        FeedbackResponse saved = feedbackResponseRepository.save(response);
        return modelMapper.map(saved, FeedbackResponseDTO.class);
    }

    public FeedbackResponseDTO getFeedbackResponseById(Long id) {
        FeedbackResponse response = feedbackResponseRepository.findById(id)
                .orElseThrow(() -> new FeedbackResponseNotFoundException("Respuesta no encontrada con id: " + id));
        return modelMapper.map(response, FeedbackResponseDTO.class);
    }

    public List<FeedbackResponseDTO> getAllFeedbackResponses() {
        return feedbackResponseRepository.findAll().stream()
                .map(response -> modelMapper.map(response, FeedbackResponseDTO.class))
                .toList(); // Reemplazo de .collect(Collectors.toList())
    }

    public FeedbackResponseDTO updateFeedbackResponse(Long id, FeedbackResponseDTO dto) {
        return feedbackResponseRepository.findById(id)
                .map(response -> {
                    response.setComment(dto.getComment());
                    FeedbackResponse updated = feedbackResponseRepository.save(response);
                    return modelMapper.map(updated, FeedbackResponseDTO.class);
                })
                .orElseThrow(() -> new FeedbackResponseNotFoundException("Respuesta no encontrada con id: " + id));
    }

    public void deleteFeedbackResponse(Long id) {
        if (!feedbackResponseRepository.existsById(id)) {
            throw new FeedbackResponseNotFoundException("Respuesta no encontrada con id: " + id);
        }
        feedbackResponseRepository.deleteById(id);
    }
}
