//package T_And_P.Training_and_Placement.service;
//
//import T_And_P.Training_and_Placement.constant.ApplicationStatus;
//import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrRequestDTO;
//import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrResponseDTO;
//import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
//import T_And_P.Training_and_Placement.entity.Student;
//import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
//import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
//import T_And_P.Training_and_Placement.repository.PlacementApplicationHdrRepository;
//import T_And_P.Training_and_Placement.repository.PlacementApplicationRepository;
//import T_And_P.Training_and_Placement.repository.StudentRepository;
//import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class PlacementApplicationService {
//
//    private final PlacementApplicationHdrRepository placementApplicationRepository;
//    private final StudentRepository studentRepository;
//    private final TrainingAndPlacementPlannerHdrRepository plannerRepository;
//
//    private static final Logger log =
//            LoggerFactory.getLogger(PlacementApplicationService.class);
//
//    public PlacementApplicationService(PlacementApplicationHdrRepository placementApplicationRepository,
//                                       StudentRepository studentRepository,
//                                       TrainingAndPlacementPlannerHdrRepository plannerRepository) {
//        this.placementApplicationRepository = placementApplicationRepository;
//        this.studentRepository = studentRepository;
//        this.plannerRepository = plannerRepository;
//    }
//
//    public PlacementApplicationHdrResponseDTO applyForDrive(
//            PlacementApplicationHdrRequestDTO requestDTO){
//
//            log.info("Apply for drive request received");
//
//            validateRequest(requestDTO);
//
//            Student student = studentRepository.findById(requestDTO.getStudentId())
//                    .orElseThrow(() ->
//                            new PlacementApplicationException(
//                                    "Student not found",
//                                    HttpStatus.BAD_REQUEST));
//
//            TrainingAndPlacementPlannerHdr planner =
//                    plannerRepository.findById(requestDTO.getPlannerId())
//                            .orElseThrow(() ->
//                                    new PlacementApplicationException(
//                                            "Placement drive not found",
//                                            HttpStatus.BAD_REQUEST));
//
//            PlacementApplicationHdr application =
//                    PlacementApplicationHdr.builder()
//                            .student(student)
//                            .plannerHdr(planner)
//                            .resumePath(requestDTO.getResumePath())
//                            .appliedDate(LocalDateTime.now())
//                            .applicationStatus(ApplicationStatus.APPLIED)
//                            .build();
//
//            PlacementApplicationHdr savedApplication =
//                    placementApplicationRepository.save(application);
//
//            return PlacementApplicationHdrResponseDTO.builder()
//                    .id(savedApplication.getId())
//                    .studentId(student.getId())
//                    .studentName(student.getStudentName())
//                    .plannerId(planner.getId())
//                    .plannerName(planner.getPlannerName())
//                    .companyName(planner.getCompany().getCompany_name())
//                    .resumePath(savedApplication.getResumePath())
//                    .appliedDate(savedApplication.getAppliedDate())
//                    .applicationStatus(savedApplication.getApplicationStatus())
//                    .build();
//        }
//    private void validateRequest(PlacementApplicationHdrRequestDTO requestDTO) {
//
//        if (requestDTO.getStudentId() == null) {
//            throw new PlacementApplicationException(
//                    "Student Id is required",
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        if (requestDTO.getPlannerId() == null) {
//            throw new PlacementApplicationException(
//                    "Planner Id is required",
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        if (requestDTO.getResumePath() == null
//                || requestDTO.getResumePath().isBlank()) {
//
//            throw new PlacementApplicationException(
//                    "Resume is required",
//                    HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    public void deleteApplication(Long applicationId) {
//    }
//
//    public List<PlacementApplicationResponseDTO> getApplicationsByPlanner(Long plannerId) {
//    }
//
//    public List<PlacementApplicationResponseDTO> getApplicationsByStudent(Long studentId) {
//    }
//}
