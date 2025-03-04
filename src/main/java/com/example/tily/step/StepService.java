package com.example.tily.step;

import com.example.tily._core.errors.exception.ExceptionCode;
import com.example.tily._core.errors.exception.CustomException;
import com.example.tily.roadmap.Roadmap;
import com.example.tily.roadmap.RoadmapRepository;
import com.example.tily.roadmap.relation.UserRoadmap;
import com.example.tily.roadmap.relation.UserRoadmapRepository;
import com.example.tily.step.reference.Reference;
import com.example.tily.step.reference.ReferenceRepository;
import com.example.tily.step.relation.UserStepRepository;
import com.example.tily.til.Til;
import com.example.tily.til.TilRepository;
import com.example.tily.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class StepService {
    private final RoadmapRepository roadmapRepository;
    private final StepRepository stepRepository;
    private final ReferenceRepository referenceRepository;
    private final TilRepository tilRepository;
    private final UserRoadmapRepository userRoadmapRepository;

    @Transactional
    public StepResponse.CreateIndividualStepDTO createIndividualStep(Long id, StepRequest.CreateIndividualStepDTO requestDTO){
        Roadmap roadmap = roadmapRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.ROADMAP_NOT_FOUND));
        String title = requestDTO.title();

        Step step = Step.builder().roadmap(roadmap).title(title).build();
        stepRepository.save(step);

        return new StepResponse.CreateIndividualStepDTO(step);
    }

    @Transactional
    public StepResponse.FindReferenceDTO findReference(Long stepId){
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_NOT_FOUND));

        List<Reference> referenceList = referenceRepository.findByStepId(stepId);

        List<StepResponse.FindReferenceDTO.YoutubeDTO> youtubeDTOs = new ArrayList<>();
        List<StepResponse.FindReferenceDTO.WebDTO> webDTOS = new ArrayList<>();

        for(Reference reference : referenceList){
            String category = reference.getCategory();
            Long id = reference.getId();
            String link = reference.getLink();

            if(category.equals("youtube") ) {
                StepResponse.FindReferenceDTO.YoutubeDTO youtubeDTO = new StepResponse.FindReferenceDTO.YoutubeDTO(id, link);
                youtubeDTOs.add(youtubeDTO);
            }
            else if(category.equals("reference") ) {
                StepResponse.FindReferenceDTO.WebDTO webDTO = new StepResponse.FindReferenceDTO.WebDTO(id, link);
                webDTOS.add(webDTO);
            }
        }

        return new StepResponse.FindReferenceDTO(step, youtubeDTOs, webDTOS);
    }

    // 특정 로드맵의 step 목록 전체 조회
    @Transactional
    public StepResponse.FindAllStepDTO findAllStep (Long roadmapId, User user) {
        List<Step> steps = stepRepository.findByRoadmapId(roadmapId);

        Map<Step, Til> maps = new HashMap<>();
        for (Step step : steps) {
            Til til = tilRepository.findByStepIdAndUserId(step.getId(), user.getId());
            maps.put(step, til);
        }

        // 해당 페이지로 들어온 사람의 역할 (master, manager, member, none)
        UserRoadmap userRoadmap = userRoadmapRepository.findByRoadmapIdAndUserId(roadmapId, user.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.STEP_FORBIDDEN));

        List<StepResponse.FindAllStepDTO.StepDTO> stepDTOs = steps
                .stream()
                .map(step -> new StepResponse.FindAllStepDTO.StepDTO(step, maps.get(step)))
                .collect(Collectors.toList());

        return new StepResponse.FindAllStepDTO(stepDTOs, userRoadmap.getProgress(), userRoadmap.getRole().getValue());
    }
}
