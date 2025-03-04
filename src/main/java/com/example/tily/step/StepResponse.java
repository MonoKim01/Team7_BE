package com.example.tily.step;

import com.example.tily.til.Til;
import java.util.List;


public class StepResponse {
    public record CreateIndividualStepDTO(Long id) {
        public CreateIndividualStepDTO(Step step) {
            this(step.getId());
        }
    }

    public record FindReferenceDTO(Long id, String description, List<YoutubeDTO> youtubes, List<WebDTO> references) {
        public FindReferenceDTO(Step step, List<YoutubeDTO> youtubeLinks, List<WebDTO> referenceLinks) {
            this(step.getId(), step.getDescription(), youtubeLinks, referenceLinks);
        }

        public record YoutubeDTO(Long id, String link) {}

        public record WebDTO(Long id, String link) {}
    }

    public record FindAllStepDTO(List<StepDTO> steps, int progress, String role) {
        public record StepDTO(Long id, String title, Boolean isCompleted, Long tillId) {
            public StepDTO(Step step, Til til) {
                this(step.getId(), step.getTitle(), til == null ? false : true, til.getId());
            }
        }
    }
}