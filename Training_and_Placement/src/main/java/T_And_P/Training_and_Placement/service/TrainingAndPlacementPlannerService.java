package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.PlannerDtlBean;
import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.constant.*;
import T_And_P.Training_and_Placement.dto.PlannerDtlDTO;
import T_And_P.Training_and_Placement.dto.PlannerRequestDTO;
import T_And_P.Training_and_Placement.dto.PlannerResponseDTO;
import T_And_P.Training_and_Placement.entity.CompanyMaster;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerDtl;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.exception.CompanyException;
import T_And_P.Training_and_Placement.repository.CompanyRepository;
import T_And_P.Training_and_Placement.repository.PlannerDtlRepository;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TrainingAndPlacementPlannerService {

    private final TrainingAndPlacementPlannerHdrRepository plannerHdrRepository;
    private final CompanyRepository companyRepository;
    private final PlannerDtlRepository plannerDtlRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

    public List<PlannerResponseDTO> getActivePlanners() {

        log.info("Fetching active planners");

        List<PlannerHdrBean> projections = plannerHdrRepository.getActivePlanners();

        if (CollectionUtils.isEmpty(projections)) {

            log.info("No active planners found");

            return Collections.emptyList();
        }

        log.info("Total active planners found : {}", projections.size());

        return projections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert Projection to DTO
     */
    private PlannerResponseDTO convertToResponse(PlannerHdrBean projection) {


        LocalDateTime startTime = projection.getStartTime();
        LocalDateTime endTime = projection.getEndTime();

        return PlannerResponseDTO.builder()
                .id(projection.getId())
                .plannerName(projection.getPlannerName())
                .plannerDesc(projection.getPlannerDesc())
                .plannerType(PlannerType.valueOf(projection.getPlannerType()))
                .mode(Mode.valueOf(projection.getMode()))
                .plannerScheduleType(PlannerScheduleType.valueOf(projection.getPlannerScheduleType()))
                .status(Status.valueOf(projection.getStatus()))
                .maxStudents(projection.getMaxStudents())
                .startDate(startTime != null ? startTime.format(DATE_FORMATTER) : null)
                .startTimeDisplay(startTime != null ? startTime.format(TIME_FORMATTER) : null)
                .endDate(endTime != null ? endTime.format(DATE_FORMATTER) : null)
                .endTimeDisplay(endTime != null ? endTime.format(TIME_FORMATTER) : null)
                .build();
    }


    @Transactional
    public PlannerResponseDTO savePlanner(PlannerRequestDTO request) {

        log.info("Save planner request received");

        validateRequest(request);

        CompanyMaster company = companyRepository.findById(
                                request.getCompanyId()).orElseThrow(() -> new CompanyException("Company not found",
                                        HttpStatus.BAD_REQUEST));

        TrainingAndPlacementPlannerHdr planner;

        if (request.getId() != null) {
            planner = plannerHdrRepository.findById(request.getId()).orElseThrow(() ->
                            new CompanyException("Planner not found", HttpStatus.BAD_REQUEST));
            planner.getTrainingAndPlacementPlannerDtls()
                    .clear();
        } else {

            planner = new TrainingAndPlacementPlannerHdr();
        }

        planner.setPlanner_name(request.getPlannerName());
        planner.setPlannerDesc(request.getPlannerDesc());
        planner.setPlannerType(request.getPlannerType());
        planner.setMode(request.getMode());
        planner.setPlannerScheduleType(request.getPlannerScheduleType());
        planner.setStatus(Status.valueOf(request.getStatus()));
        planner.setStartTime(request.getStartTime());
        planner.setEndTime(request.getEndTime());
        planner.setMaxStudents(request.getMaxStudents());
        planner.setCompany(company);

        if (!CollectionUtils.isEmpty(
                request.getPlannerDetails())) {

            List<TrainingAndPlacementPlannerDtl> details = request.getPlannerDetails()
                            .stream().map(dtl -> {
                                TrainingAndPlacementPlannerDtl entity =
                                        new TrainingAndPlacementPlannerDtl();
                                entity.setCriteriaRule(dtl.getCriteriaRule());
                                entity.setStatus(dtl.getStatus());
                                entity.setPlannerHdr(planner);
                                return entity;

                            }).toList();

            planner.setTrainingAndPlacementPlannerDtls(
                    details);
        }

        TrainingAndPlacementPlannerHdr saved = plannerHdrRepository.save(planner);

        return PlannerResponseDTO.builder()
                .id(saved.getId())
                .plannerName(saved.getPlanner_name())
                .build();
    }


    private void validateRequest(PlannerRequestDTO request) {

        if (!StringUtils.hasText(request.getPlannerName())) {
            throw new CompanyException("Planner Name is mandatory", HttpStatus.BAD_REQUEST);
        }

        if (request.getCompanyId() == null) {
            throw new CompanyException("Company is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getPlannerType() == null) {
            throw new CompanyException("Planner Type is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getMode() == null) {
            throw new CompanyException("Mode is mandatory",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getPlannerScheduleType() == null) {
            throw new CompanyException("Planner Schedule Type is mandatory", HttpStatus.BAD_REQUEST);
        }

        if (request.getStartTime() == null) {
            throw new CompanyException("Start Time is mandatory", HttpStatus.BAD_REQUEST);
        }

        if (request.getPlannerScheduleType()
                == PlannerScheduleType.RANGE) {

            if (request.getEndTime() == null) {
                throw new CompanyException("End Time is mandatory for RANGE", HttpStatus.BAD_REQUEST);
            }

            if (!request.getEndTime()
                    .isAfter(request.getStartTime())) {

                throw new CompanyException(
                        "End Time must be greater than Start Time",
                        HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getPlannerScheduleType()
                == PlannerScheduleType.FIXED) {

            request.setEndTime(null);
        }
    }

    @Transactional
    public void deletePlanner(Long id) {

        log.info("Delete planner request for id {}", id);

        TrainingAndPlacementPlannerHdr planner = plannerHdrRepository.findById(id)
                        .orElseThrow(() -> new CompanyException("Planner not found",
                                        HttpStatus.BAD_REQUEST));
        plannerHdrRepository.delete(planner);
        log.info("Planner deleted successfully");
    }


    public PlannerResponseDTO getPlannerById(Long id) {

        log.info("Fetching planner details for id : {}", id);

        PlannerHdrBean planner = plannerHdrRepository.getPlannerById(id)
                        .orElseThrow(() -> new CompanyException("Planner not found",
                                HttpStatus.BAD_REQUEST));

        List<PlannerDtlBean> details = plannerDtlRepository.getPlannerDetails(id);

        return convertToResponse(planner, details);
    }


    public List<PlannerResponseDTO> getAllPlanners() {

        log.info("Fetching all planners");

        List<PlannerHdrBean> planners =
                plannerHdrRepository.getAllPlanners();

        if (CollectionUtils.isEmpty(planners)) {
            log.info("No planner records found");
            return Collections.emptyList();
        }

        return planners.stream().map(planner -> {
                    List<PlannerDtlBean> details =
                            plannerDtlRepository.getPlannerDetails(planner.getId());

                    return convertToResponse(
                            planner,
                            details);
        }).collect(Collectors.toList());
    }


    private PlannerResponseDTO convertToResponse(
            PlannerHdrBean planner,
            List<PlannerDtlBean> details) {

        LocalDateTime startTime = planner.getStartTime();
        LocalDateTime endTime = planner.getEndTime();

        List<PlannerDtlDTO> plannerDetails =
                CollectionUtils.isEmpty(details)
                        ? Collections.emptyList()
                        : details.stream()
                        .map(dtl -> PlannerDtlDTO.builder()
                                .id(dtl.getId())
                                .criteriaRule(CriteriaRule.valueOf(dtl.getCriteriaRule()))
                                .status(Status.valueOf(dtl.getStatus()))
                                .build())
                        .collect(Collectors.toList());

        return PlannerResponseDTO.builder()
                .id(planner.getId())
                .plannerName(planner.getPlannerName())
                .plannerDesc(planner.getPlannerDesc())
                .plannerType(PlannerType.valueOf(planner.getPlannerType()))
                .mode(Mode.valueOf(planner.getMode()))
                .plannerScheduleType(PlannerScheduleType.valueOf(planner.getPlannerScheduleType()))
                .status(Status.valueOf(planner.getStatus()))
                .maxStudents(planner.getMaxStudents())
                .companyId(planner.getCompanyId())
                .companyName(planner.getCompanyName())
                .startDate(startTime != null ? startTime.format(DATE_FORMATTER) : "")
                .startTimeDisplay(startTime != null ? startTime.format(TIME_FORMATTER) : "")
                .endDate(endTime != null ? endTime.format(DATE_FORMATTER) : "")
                .endTimeDisplay(endTime != null ? endTime.format(TIME_FORMATTER) : "")
                .plannerDetails(plannerDetails)
                .build();
    }
}
