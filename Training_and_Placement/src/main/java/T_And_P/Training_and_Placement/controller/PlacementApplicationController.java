package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrResponseDTO;
import T_And_P.Training_and_Placement.service.PlacementApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placement-application")
@RequiredArgsConstructor
public class PlacementApplicationController {

    private final PlacementApplicationService placementApplicationService;

    @PostMapping("/apply")
    public PlacementApplicationHdrResponseDTO applyForDrive(
            @RequestBody PlacementApplicationHdrRequestDTO requestDTO) {

        return placementApplicationService.applyForDrive(requestDTO);
    }

    @GetMapping("/student/{studentId}")
    public List<PlacementApplicationHdrResponseDTO> getApplicationsByStudentId(
            @PathVariable Long studentId) {

        return placementApplicationService.getByStudentId(studentId);
    }

    @GetMapping("/planner/{plannerId}")
    public List<PlacementApplicationHdrResponseDTO> getApplicationsByPlanner(
            @PathVariable Long plannerId) {

        return placementApplicationService.getApplicationsByPlannerId(plannerId);
    }

    @DeleteMapping("/{applicationId}")
    public String deleteApplication(@PathVariable Long applicationId) {

        placementApplicationService.deleteApplication(applicationId);
        return "Application deleted successfully";
    }
}