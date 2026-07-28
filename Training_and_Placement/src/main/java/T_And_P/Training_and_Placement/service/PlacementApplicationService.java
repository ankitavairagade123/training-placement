package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import T_And_P.Training_and_Placement.dto.PlacementApplicationDtlRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrResponseDTO;
import T_And_P.Training_and_Placement.dto.UpdateApplicationStatusRequestDTO;
import T_And_P.Training_and_Placement.entity.PlacementApplicationDtl;
import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
import T_And_P.Training_and_Placement.entity.Student;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.PlacementApplicationDtlRepository;
import T_And_P.Training_and_Placement.repository.PlacementApplicationHdrRepository;
import T_And_P.Training_and_Placement.repository.StudentRepository;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlacementApplicationService {

    private final PlacementApplicationHdrRepository placementApplicationRepository;
    private final PlacementApplicationDtlRepository placementApplicationDtlRepository;
    private final StudentRepository studentRepository;
    private final TrainingAndPlacementPlannerHdrRepository plannerRepository;

    public PlacementApplicationService(
            PlacementApplicationHdrRepository placementApplicationRepository,
            PlacementApplicationDtlRepository placementApplicationDtlRepository,
            StudentRepository studentRepository,
            TrainingAndPlacementPlannerHdrRepository plannerRepository) {

        this.placementApplicationRepository = placementApplicationRepository;
        this.placementApplicationDtlRepository = placementApplicationDtlRepository;
        this.studentRepository = studentRepository;
        this.plannerRepository = plannerRepository;
    }

    @Transactional
    public PlacementApplicationHdrResponseDTO applyForDrive(
            PlacementApplicationHdrRequestDTO requestDTO) {

        log.info("Apply for drive request received");

        validateRequest(requestDTO);

        // Fetch Student
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new PlacementApplicationException(
                        "Student not found",
                        HttpStatus.BAD_REQUEST));

        // Fetch Planner
        TrainingAndPlacementPlannerHdr planner = plannerRepository
                .findById(requestDTO.getPlannerId())
                .orElseThrow(() -> new PlacementApplicationException(
                        "Placement drive not found",
                        HttpStatus.BAD_REQUEST));

        // Check duplicate application
        boolean alreadyApplied = placementApplicationRepository
                .existsByPlannerHdrIdAndStudentStudentId(
                        requestDTO.getPlannerId(),
                        requestDTO.getStudentId());

        if (alreadyApplied) {
            throw new PlacementApplicationException(
                    "You have already applied for this drive",
                    HttpStatus.BAD_REQUEST);
        }

        // Save Header
        PlacementApplicationHdr application = PlacementApplicationHdr.builder()
                .student(student)
                .plannerHdr(planner)
                .resumePath(requestDTO.getResumePath())
                .appliedDate(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.APPLIED)
                .build();

        PlacementApplicationHdr savedApplication =
                placementApplicationRepository.save(application);

        log.info("Application header saved successfully");

        // Save Details
        if (!CollectionUtils.isEmpty(requestDTO.getApplicationDetails())) {

            List<PlacementApplicationDtl> details = new ArrayList<>();

            for (PlacementApplicationDtlRequestDTO detailRequest :
                    requestDTO.getApplicationDetails()) {

                PlacementApplicationDtl detail = PlacementApplicationDtl.builder()
                        .applicationHdr(savedApplication)
                        .fieldName(detailRequest.getFieldName())
                        .fieldValue(detailRequest.getFieldValue())
                        .build();

                details.add(detail);
            }

            placementApplicationDtlRepository.saveAll(details);

            log.info("Application details saved successfully");
        }

        // Return Response
        return PlacementApplicationHdrResponseDTO.builder()
                .id(savedApplication.getApplicationId())
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .plannerId(planner.getId())
                .resumePath(savedApplication.getResumePath())
                .plannerName(planner.getPlannerName())
                .companyName(planner.getCompany().getCompany_name())
                .appliedDate(savedApplication.getAppliedDate())
                .applicationStatus(savedApplication.getApplicationStatus())
                .build();
    }

    private void validateRequest(PlacementApplicationHdrRequestDTO requestDTO) {

        if (requestDTO == null) {
            throw new PlacementApplicationException(
                    "Request is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getStudentId() == null) {
            throw new PlacementApplicationException(
                    "Student Id is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (requestDTO.getPlannerId() == null) {
            throw new PlacementApplicationException(
                    "Planner Id is required",
                    HttpStatus.BAD_REQUEST);
        }

        if (CollectionUtils.isEmpty(requestDTO.getApplicationDetails())) {
            throw new PlacementApplicationException(
                    "Application details are required",
                    HttpStatus.BAD_REQUEST);
        }

        for (PlacementApplicationDtlRequestDTO detail :
                requestDTO.getApplicationDetails()) {

            if (detail.getFieldName() == null ||
                    detail.getFieldName().trim().isEmpty()) {

                throw new PlacementApplicationException(
                        "Field name is required",
                        HttpStatus.BAD_REQUEST);
            }

            if (detail.getFieldValue() == null ||
                    detail.getFieldValue().trim().isEmpty()) {

                throw new PlacementApplicationException(
                        "Field value is required",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void deleteApplication(Long applicationId) {

        log.info("Delete application request received for id : {}", applicationId);

        PlacementApplicationHdr application = placementApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new PlacementApplicationException(
                        "Application not found",
                        HttpStatus.BAD_REQUEST));

        placementApplicationRepository.delete(application);

        log.info("Application deleted successfully");
    }

    public List<PlacementApplicationHdrResponseDTO> getByStudentId(Long studentId) {

        log.info("Fetching applications for student id : {}", studentId);

        List<PlacementApplicationHdr> applications =
                placementApplicationRepository.findByStudentStudentId(studentId);

        if (applications.isEmpty()) {
            throw new PlacementApplicationException(
                    "No applications found for student",
                    HttpStatus.NOT_FOUND);
        }

        return applications.stream()
                .map(application -> PlacementApplicationHdrResponseDTO.builder()
                        .id(application.getApplicationId())
                        .studentId(application.getStudent().getStudentId())
                        .studentName(application.getStudent().getStudentName())
                        .plannerId(application.getPlannerHdr().getId())
                        .plannerName(application.getPlannerHdr().getPlannerName())
                        .companyName(application.getPlannerHdr()
                                .getCompany()
                                .getCompany_name())
                        .appliedDate(application.getAppliedDate())
                        .applicationStatus(application.getApplicationStatus())
                        .build())
                .toList();
    }

    public List<PlacementApplicationHdrResponseDTO> getApplicationsByPlannerId(Long plannerId) {

        log.info("Fetching applications for planner id : {}", plannerId);

        List<PlacementApplicationHdr> applications =
                placementApplicationRepository.findByPlannerHdrId(plannerId);

        if (applications.isEmpty()) {
            throw new PlacementApplicationException(
                    "No applications found for planner",
                    HttpStatus.NOT_FOUND);
        }

        return applications.stream()
                .map(application -> PlacementApplicationHdrResponseDTO.builder()
                        .id(application.getApplicationId())
                        .studentId(application.getStudent().getStudentId())
                        .studentName(application.getStudent().getStudentName())
                        .plannerId(application.getPlannerHdr().getId())
                        .plannerName(application.getPlannerHdr().getPlannerName())
                        .companyName(application.getPlannerHdr()
                                .getCompany()
                                .getCompany_name())
                        .appliedDate(application.getAppliedDate())
                        .applicationStatus(application.getApplicationStatus())
                        .build())
                .toList();
    }

    @Transactional
    public PlacementApplicationHdrResponseDTO updateApplicationStatus(
            UpdateApplicationStatusRequestDTO requestDTO){

        validateUpdateStatusRequest(requestDTO);

        log.info("request received to find the application");

        PlacementApplicationHdr placementApplicationHdr = placementApplicationRepository.findById(requestDTO.getApplicationId())
                .orElseThrow(() -> new PlacementApplicationException(
                        "Application not found",
                        HttpStatus.BAD_REQUEST
                ));

        placementApplicationHdr.setApplicationStatus(requestDTO.getApplicationStatus());


        PlacementApplicationHdr savedApplication = placementApplicationRepository.save(placementApplicationHdr);

        return PlacementApplicationHdrResponseDTO.builder()
                .id(savedApplication.getApplicationId())
                .studentId(savedApplication.getStudent().getStudentId())
                .studentName(savedApplication.getStudent().getStudentName())
                .plannerId(savedApplication.getPlannerHdr().getId())
                .resumePath(savedApplication.getResumePath())
                .applicationStatus(savedApplication.getApplicationStatus())
                .appliedDate(savedApplication.getAppliedDate())
                .build();
    }
    private void validateUpdateStatusRequest(UpdateApplicationStatusRequestDTO requestDTO) {

        if(requestDTO.getApplicationId() == null) {
            throw new PlacementApplicationException(
                    "Application id is required",
                    HttpStatus.BAD_REQUEST
            );
        }

        if(requestDTO.getApplicationStatus() == null) {
            throw new PlacementApplicationException(
                    "Application status is required",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}