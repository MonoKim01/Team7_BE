package com.example.tily.roadmap;

import com.example.tily._core.security.CustomUserDetails;
import com.example.tily._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class RoadmapController {
    private final RoadmapService roadmapService;

    // 개인 로드맵(카테고리) 생성하기
    @PostMapping("/roadmaps/individual")
    public ResponseEntity<?> createIndividualRoadmap(@RequestBody @Valid RoadmapRequest.CreateIndividualRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.CreateRoadmapDTO responseDTO = roadmapService.createIndividualRoadmap(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 그룹 로드맵 생성하기
    @PostMapping("/roadmaps")
    public ResponseEntity<?> createGroupRoadmap(@RequestBody @Valid RoadmapRequest.CreateGroupRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.CreateRoadmapDTO responseDTO = roadmapService.createGroupRoadmap(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 틸리, 그룹 로드맵 정보 조회하기
    @GetMapping("/roadmaps/{id}")
    public ResponseEntity<?> findGroupRoadmap(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
         RoadmapResponse.FindGroupRoadmapDTO responseDTO = roadmapService.findGroupRoadmap(id, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 그룹 로드맵 정보 수정하기
    @PostMapping("/roadmaps/{id}")
    public ResponseEntity<?> updateGroupRoadmap(@PathVariable Long id, @RequestBody @Valid RoadmapRequest.UpdateGroupRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.updateGroupRoadmap(id, requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 내가 속한 로드맵 전체 목록 조회하기
    @GetMapping("/roadmaps/my")
    public ResponseEntity<?> findAllMyRoadmap(@AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.FindAllMyRoadmapDTO responseDTO = roadmapService.findAllMyRoadmaps(userDetails.getUser());
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵 조회하기
    @GetMapping("/roadmaps")
    public ResponseEntity<?> findRoadmapByQuery(@RequestParam(value="category", defaultValue = "tily") String category,
                                                @RequestParam(value="name", required = false) String name,
                                                @RequestParam(value="page", defaultValue = "0") int page,
                                                @RequestParam(value="size", defaultValue = "9") int size,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoadmapResponse.FindRoadmapByQueryDTO responseDTO  = roadmapService.findAll(category, name, page, size);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 특정 로드맵에 참여 신청하기
    @PostMapping("/roadmaps/{id}/apply")
    public ResponseEntity<?> applyRoadmap(@RequestBody @Valid RoadmapRequest.ApplyRoadmapDTO requestDTO, @PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.applyRoadmap(requestDTO, id, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 참가 코드로 로드맵 참여하기
    @PostMapping("/roadmaps/groups/participate")
    public ResponseEntity<?> participateRoadmap(@RequestBody @Valid RoadmapRequest.ParticipateRoadmapDTO requestDTO, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.ParticipateRoadmapDTO responseDTO = roadmapService.participateRoadmap(requestDTO, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵의 구성원 전체 조회하기
    @GetMapping("/roadmaps/groups/{id}/members")
    public ResponseEntity<?> findRoadmapMembers(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.FindRoadmapMembersDTO responseDTO = roadmapService.findRoadmapMembers(id, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵의 구성원 역할 바꾸기
    @PatchMapping("/roadmaps/groups/{groupsId}/members/{membersId}")
    public ResponseEntity<?> changeMemberRole(@RequestBody @Valid RoadmapRequest.ChangeMemberRoleDTO requestDTO, @PathVariable Long groupsId, @PathVariable Long membersId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.changeMemberRole(requestDTO, groupsId, membersId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵의 구성원 강퇴하기
    @DeleteMapping("/roadmaps/groups/{groupsId}/members/{membersId}")
    public ResponseEntity<?> dismissMember(@PathVariable Long groupsId, @PathVariable Long membersId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.dismissMember(groupsId, membersId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵에 신청한 사람들 목록 조회하기
    @GetMapping("/roadmaps/groups/{id}/members/apply")
    public ResponseEntity<?> findAppliedUsers(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        RoadmapResponse.FindAppliedUsersDTO responseDTO = roadmapService.findAppliedUsers(id, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }

    // 로드맵 참여 신청 승인
    @PostMapping("/roadmaps/groups/{groupsId}/members/{membersId}/accept")
    public ResponseEntity<?> acceptApplication(@PathVariable Long groupsId, @PathVariable Long membersId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.acceptApplication(groupsId, membersId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    // 로드맵 참여 신청 거절
    @DeleteMapping("/roadmaps/groups/{groupsId}/members/{membersId}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long groupsId, @PathVariable Long membersId, @AuthenticationPrincipal CustomUserDetails userDetails){
        roadmapService.rejectApplication(groupsId, membersId, userDetails.getUser());

        return ResponseEntity.ok().body(ApiUtils.success(null));
    }

    //  로드맵의 특정 step의 틸 목록 조회
    @GetMapping("/roadmaps/groups/{groupsId}/steps/{stepsId}/tils")
    public ResponseEntity<?> findTilOfStep(@PathVariable Long groupsId, @PathVariable Long stepsId,
                                           @RequestParam(value="isSubmit", defaultValue = "true") Boolean isSubmit,
                                           @RequestParam(value="isMember", defaultValue = "true") Boolean isMember,
                                           @RequestParam(value="name", required = false) String name){

        RoadmapResponse.FindTilOfStepDTO responseDTO = roadmapService.findTilOfStep(groupsId, stepsId, isSubmit, isMember, name);
        return ResponseEntity.ok().body(ApiUtils.success(responseDTO));
    }
}