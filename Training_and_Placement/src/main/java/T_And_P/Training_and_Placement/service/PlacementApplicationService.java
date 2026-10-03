package T_And_P.Training_and_Placement.service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.ApplicationDtlBean;
import T_And_P.Training_and_Placement.bean.ApplicationHdrBean;
import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.bean.PlannerQuestionBean;
import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.EligibilityCheckResponseDTO;
import T_And_P.Training_and_Placement.dto.OfferLetterRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationDtlRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationDtlResponseDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrRequestDTO;
import T_And_P.Training_and_Placement.dto.PlacementApplicationHdrResponseDTO;
import T_And_P.Training_and_Placement.dto.UpdateApplicationStatusRequestDTO;
import T_And_P.Training_and_Placement.entity.PlacementApplicationDtl;
import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
import T_And_P.Training_and_Placement.event.ApplicationSubmittedEvent;
import T_And_P.Training_and_Placement.event.PlannerEventPublisher;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.PlacementApplicationDtlRepository;
import T_And_P.Training_and_Placement.repository.PlacementApplicationHdrRepository;
import T_And_P.Training_and_Placement.repository.PlannerDtlRepository;
import T_And_P.Training_and_Placement.repository.PlannerQuestionRepository;
import T_And_P.Training_and_Placement.repository.StudentRepository;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Business logic for student apply, eligibility check,
 * faculty status update and offer-letter upload.
 */
@Slf4j
@Service
public class PlacementApplicationService {

    private final PlacementApplicationHdrRepository placementApplicationRepository;
    private final PlacementApplicationDtlRepository placementApplicationDtlRepository;
    private final StudentRepository studentRepository;
    private final TrainingAndPlacementPlannerHdrRepository plannerRepository;
    private final PlannerDtlRepository plannerDtlRepository;
    private final PlannerQuestionRepository plannerQuestionRepository;
    private final EligibilityValidationService eligibilityValidationService;
    private final PlannerEventPublisher plannerEventPublisher;
    private final MessageUtil messageUtil;

    /**
     * Wires repositories and helper services used by the apply flow.
     */
    public PlacementApplicationService(
            PlacementApplicationHdrRepository placementApplicationRepository,
            PlacementApplicationDtlRepository placementApplicationDtlRepository,
            StudentRepository studentRepository,
            TrainingAndPlacementPlannerHdrRepository plannerRepository,
            PlannerDtlRepository plannerDtlRepository,
            PlannerQuestionRepository plannerQuestionRepository,
            EligibilityValidationService eligibilityValidationService,
            PlannerEventPublisher plannerEventPublisher,
            MessageUtil messageUtil) {

        log.info("PlacementApplicationService initialized");

        this.placementApplicationRepository = placementApplicationRepository;
        this.placementApplicationDtlRepository = placementApplicationDtlRepository;
        this.studentRepository = studentRepository;
        this.plannerRepository = plannerRepository;
        this.plannerDtlRepository = plannerDtlRepository;
        this.plannerQuestionRepository = plannerQuestionRepository;
        this.eligibilityValidationService = eligibilityValidationService;
        this.plannerEventPublisher = plannerEventPublisher;
        this.messageUtil = messageUtil;
    }

    /**
     * Student apply: validates published planner, registration window,
     * terms, eligibility and questions.
     */
    @Transactional
    public PlacementApplicationHdrResponseDTO applyForDrive(
            PlacementApplicationHdrRequestDTO requestDTO) {

        log.info(
                "applyForDrive() started for studentId={}, plannerId={}",
                requestDTO == null ? null : requestDTO.getStudentId(),
                requestDTO == null ? null : requestDTO.getPlannerId()
        );

        try {
            validateRequest(requestDTO);

            StudentBean student = studentRepository
                    .getStudentById(requestDTO.getStudentId())
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest(
                                            "application.student.not.found"
                                    ),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            PlannerHdrBean planner = plannerRepository
                    .getPlannerById(requestDTO.getPlannerId())
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest(
                                            "application.drive.not.found"
                                    ),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            if (!Status.ACTIVE.name().equals(planner.getStatus())) {
                log.info(
                        "applyForDrive() rejected because planner {} is not published",
                        requestDTO.getPlannerId()
                );

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.planner.not.published"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            LocalDateTime now = LocalDateTime.now();

            if (planner.getRegistrationStartDate() != null
                    && now.isBefore(planner.getRegistrationStartDate())) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.registration.not.started"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (planner.getRegistrationEndDate() != null
                    && now.isAfter(planner.getRegistrationEndDate())) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.registration.closed"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!Boolean.TRUE.equals(requestDTO.getTermsAccepted())) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.terms.required"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (placementApplicationRepository
                    .existsByPlannerHdrIdAndStudentStudentId(
                            requestDTO.getPlannerId(),
                            requestDTO.getStudentId()
                    )
                    .isPresent()) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.already.applied"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            String resumePath =
                    StringUtils.hasText(requestDTO.getResumePath())
                            ? MapperUtil.trimToNull(
                            requestDTO.getResumePath()
                    )
                            : student.getResumePath();

            List<String> reasons =
                    eligibilityValidationService.validate(
                            student,
                            plannerDtlRepository.getPlannerDetails(
                                    requestDTO.getPlannerId()
                            ),
                            resumePath
                    );

            if (!reasons.isEmpty()) {
                log.info(
                        "applyForDrive() rejected due to ineligibility: {}",
                        reasons
                );

                throw new PlacementApplicationException(
                        String.join("; ", reasons),
                        HttpStatus.BAD_REQUEST
                );
            }

            validateMandatoryQuestions(
                    plannerQuestionRepository.getQuestionsByPlannerId(
                            requestDTO.getPlannerId()
                    ),
                    requestDTO.getApplicationDetails()
            );

            PlacementApplicationHdr application =
                    PlacementApplicationHdr.builder()
                            .student(
                                    studentRepository.getReferenceById(
                                            requestDTO.getStudentId()
                                    )
                            )
                            .plannerHdr(
                                    plannerRepository.getReferenceById(
                                            requestDTO.getPlannerId()
                                    )
                            )
                            .resumePath(resumePath)
                            .termsAccepted(true)
                            .appliedDate(LocalDateTime.now())
                            .applicationStatus(ApplicationStatus.APPLIED)
                            .applicationDetails(
                                    new ArrayList<PlacementApplicationDtl>()
                            )
                            .build();

            PlacementApplicationHdr savedApplication =
                    placementApplicationRepository.save(application);

            List<PlacementApplicationDtl> details =
                    mapApplicationDetails(
                            requestDTO.getApplicationDetails(),
                            savedApplication
                    );

            if (!details.isEmpty()) {
                placementApplicationDtlRepository.saveAll(details);
            }

            plannerEventPublisher.publishApplicationSubmitted(
                    ApplicationSubmittedEvent.builder()
                            .email(student.getEmail())
                            .studentName(student.getStudentName())
                            .plannerName(planner.getPlannerName())
                            .build()
            );

            log.info(
                    "applyForDrive() completed for applicationId={}",
                    savedApplication.getApplicationId()
            );

            return toResponse(
                    getApplicationOrThrow(
                            savedApplication.getApplicationId()
                    )
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "applyForDrive() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("application.cannot.apply"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Pre-check used by the student portal before showing
     * the Apply button result.
     */
    public EligibilityCheckResponseDTO checkEligibility(
            Long studentId,
            Long plannerId) {

        log.info(
                "checkEligibility() started for studentId={}, plannerId={}",
                studentId,
                plannerId
        );

        EligibilityCheckResponseDTO response =
                eligibilityValidationService.checkEligibility(
                        studentId,
                        plannerId,
                        null
                );

        log.info(
                "checkEligibility() completed for studentId={}, plannerId={}, eligible={}",
                studentId,
                plannerId,
                response == null ? null : response.isEligible()
        );

        return response;
    }

    /**
     * Allowed only after SELECTED / OFFER_ACCEPTED.
     * Stores offer letter and/or joining letter path.
     */
    @Transactional
    public PlacementApplicationHdrResponseDTO uploadOfferLetter(
            Long applicationId,
            OfferLetterRequestDTO requestDTO) {

        log.info(
                "uploadOfferLetter() started for applicationId={}",
                applicationId
        );

        try {
            if (requestDTO == null) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.request.required"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            ApplicationHdrBean current =
                    getApplicationOrThrow(applicationId);

            ApplicationStatus currentStatus =
                    MapperUtil.parseEnum(
                            ApplicationStatus.class,
                            current.getApplicationStatus()
                    );

            if (currentStatus != ApplicationStatus.SELECTED
                    && currentStatus != ApplicationStatus.OFFER_ACCEPTED) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.offer.upload.not.allowed"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!StringUtils.hasText(
                    requestDTO.getOfferLetterPath()
            ) && !StringUtils.hasText(
                    requestDTO.getJoiningLetterPath()
            )) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.offer.required"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            PlacementApplicationHdr application =
                    placementApplicationRepository.getReferenceById(
                            applicationId
                    );

            if (StringUtils.hasText(
                    requestDTO.getOfferLetterPath()
            )) {
                application.setOfferLetterPath(
                        MapperUtil.trimToNull(
                                requestDTO.getOfferLetterPath()
                        )
                );
            }

            if (StringUtils.hasText(
                    requestDTO.getJoiningLetterPath()
            )) {
                application.setJoiningLetterPath(
                        MapperUtil.trimToNull(
                                requestDTO.getJoiningLetterPath()
                        )
                );
            }

            application.setApplicationStatus(
                    ApplicationStatus.OFFER_ACCEPTED
            );

            placementApplicationRepository.save(application);

            PlacementApplicationHdrResponseDTO response =
                    toResponse(
                            getApplicationOrThrow(applicationId)
                    );

            log.info(
                    "uploadOfferLetter() completed for applicationId={}",
                    applicationId
            );

            return response;

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "uploadOfferLetter() failed for applicationId={}",
                    applicationId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.cannot.update"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes one application by id.
     */
    public void deleteApplication(Long applicationId) {

        log.info(
                "deleteApplication() started for applicationId={}",
                applicationId
        );

        try {
            placementApplicationRepository
                    .existsApplicationById(applicationId)
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest(
                                            "application.not.found"
                                    ),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            placementApplicationRepository.deleteById(applicationId);

            log.info(
                    "deleteApplication() completed for applicationId={}",
                    applicationId
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "deleteApplication() failed for applicationId={}",
                    applicationId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.cannot.delete"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Returns all applications submitted by one student.
     */
    public List<PlacementApplicationHdrResponseDTO> getByStudentId(
            Long studentId) {

        log.info(
                "getByStudentId() started for studentId={}",
                studentId
        );

        List<ApplicationHdrBean> applications =
                placementApplicationRepository
                        .getApplicationsByStudentId(studentId);

        if (applications == null || applications.isEmpty()) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.none.for.student"
                    ),
                    HttpStatus.NOT_FOUND
            );
        }

        List<PlacementApplicationHdrResponseDTO> response =
                applications.stream()
                        .filter(Objects::nonNull)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info(
                "getByStudentId() completed for studentId={}, count={}",
                studentId,
                response.size()
        );

        return response;
    }

    /**
     * Returns all applications for one planner
     * (faculty verification list).
     */
    public List<PlacementApplicationHdrResponseDTO> getApplicationsByPlannerId(
            Long plannerId) {

        log.info(
                "getApplicationsByPlannerId() started for plannerId={}",
                plannerId
        );

        List<ApplicationHdrBean> applications =
                placementApplicationRepository
                        .getApplicationsByPlannerId(plannerId);

        if (applications == null || applications.isEmpty()) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.none.for.planner"
                    ),
                    HttpStatus.NOT_FOUND
            );
        }

        List<PlacementApplicationHdrResponseDTO> response =
                applications.stream()
                        .filter(Objects::nonNull)
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        log.info(
                "getApplicationsByPlannerId() completed for plannerId={}, count={}",
                plannerId,
                response.size()
        );

        return response;
    }

    /**
     * Faculty updates application status through the interview/result flow.
     */
    @Transactional
    public PlacementApplicationHdrResponseDTO updateApplicationStatus(
            UpdateApplicationStatusRequestDTO requestDTO) {

        log.info(
                "updateApplicationStatus() started for applicationId={}, status={}",
                requestDTO == null ? null : requestDTO.getApplicationId(),
                requestDTO == null ? null : requestDTO.getApplicationStatus()
        );

        try {
            validateUpdateStatusRequest(requestDTO);

            placementApplicationRepository
                    .existsApplicationById(
                            requestDTO.getApplicationId()
                    )
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest(
                                            "application.not.found"
                                    ),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            PlacementApplicationHdr placementApplicationHdr =
                    placementApplicationRepository.getReferenceById(
                            requestDTO.getApplicationId()
                    );

            placementApplicationHdr.setApplicationStatus(
                    requestDTO.getApplicationStatus()
            );

            placementApplicationRepository.save(
                    placementApplicationHdr
            );

            PlacementApplicationHdrResponseDTO response =
                    toResponse(
                            getApplicationOrThrow(
                                    requestDTO.getApplicationId()
                            )
                    );

            log.info(
                    "updateApplicationStatus() completed for applicationId={}, status={}",
                    response == null ? null : response.getId(),
                    response == null
                            ? null
                            : response.getApplicationStatus()
            );

            return response;

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "updateApplicationStatus() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.cannot.update"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Maps apply-form answers to application detail entities.
     */
    private List<PlacementApplicationDtl> mapApplicationDetails(
            List<PlacementApplicationDtlRequestDTO> detailRequests,
            PlacementApplicationHdr savedApplication) {

        List<PlacementApplicationDtl> details =
                new ArrayList<>();

        if (CollectionUtils.isEmpty(detailRequests)) {
            return details;
        }

        for (PlacementApplicationDtlRequestDTO detailRequest :
                detailRequests) {

            if (detailRequest == null
                    || MapperUtil.isBlank(
                    detailRequest.getFieldName()
            )) {
                continue;
            }

            details.add(
                    PlacementApplicationDtl.builder()
                            .id(detailRequest.getApplicationDetailId())
                            .applicationHdr(savedApplication)
                            .fieldName(
                                    MapperUtil.trimToNull(
                                            detailRequest.getFieldName()
                                    )
                            )
                            .fieldValue(
                                    MapperUtil.trimToNull(
                                            detailRequest.getFieldValue()
                                    )
                            )
                            .build()
            );
        }

        return details;
    }

    /**
     * Ensures every mandatory planner question has an answer
     * in the apply payload.
     */
    private void validateMandatoryQuestions(
            List<PlannerQuestionBean> questions,
            List<PlacementApplicationDtlRequestDTO> details) {

        log.info("validateMandatoryQuestions() started");

        if (CollectionUtils.isEmpty(questions)) {
            return;
        }

        Map<String, String> answers = new HashMap<>();

        if (!CollectionUtils.isEmpty(details)) {
            for (PlacementApplicationDtlRequestDTO detail : details) {

                if (detail == null
                        || !StringUtils.hasText(
                        detail.getFieldName()
                )) {
                    continue;
                }

                if (!answers.containsKey(
                        detail.getFieldName()
                )) {
                    answers.put(
                            detail.getFieldName(),
                            detail.getFieldValue() == null
                                    ? ""
                                    : detail.getFieldValue()
                    );
                }
            }
        }

        for (PlannerQuestionBean question : questions) {

            if (question == null) {
                continue;
            }

            if (Boolean.TRUE.equals(question.getMandatory())
                    && !StringUtils.hasText(
                    answers.get(question.getQuestion())
            )) {

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "application.answer.required",
                                question.getQuestion()
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        log.info(
                "validateMandatoryQuestions() completed"
        );
    }

    /**
     * Validates studentId and plannerId on apply request.
     */
    private void validateRequest(
            PlacementApplicationHdrRequestDTO requestDTO) {

        log.info("validateRequest() started");

        if (requestDTO == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.request.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getStudentId() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.student.id.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getPlannerId() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.planner.id.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validateRequest() completed");
    }

    /**
     * Validates application id and new status before faculty update.
     */
    private void validateUpdateStatusRequest(
            UpdateApplicationStatusRequestDTO requestDTO) {

        log.info(
                "validateUpdateStatusRequest() started"
        );

        if (requestDTO == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.request.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getApplicationId() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.id.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getApplicationStatus() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "application.status.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info(
                "validateUpdateStatusRequest() completed"
        );
    }

    /**
     * Maps application header and child answers to API response.
     */
    private ApplicationHdrBean getApplicationOrThrow(
            Long applicationId) {

        return placementApplicationRepository
                .getApplicationById(applicationId)
                .orElseThrow(() ->
                        new PlacementApplicationException(
                                messageUtil.badRequest(
                                        "application.not.found"
                                ),
                                HttpStatus.BAD_REQUEST
                        )
                );
    }

    /**
     * Maps application header projection and child answers
     * to API response.
     */
    private PlacementApplicationHdrResponseDTO toResponse(
            ApplicationHdrBean application) {

        if (application == null) {
            return null;
        }

        log.debug(
                "toResponse() started for applicationId={}",
                application.getId()
        );

        return PlacementApplicationHdrResponseDTO.builder()
                .id(application.getId())
                .studentId(application.getStudentId())
                .studentName(application.getStudentName())
                .plannerId(application.getPlannerId())
                .plannerName(application.getPlannerName())
                .companyName(application.getCompanyName())
                .resumePath(application.getResumePath())
                .termsAccepted(application.getTermsAccepted())
                .offerLetterPath(application.getOfferLetterPath())
                .joiningLetterPath(
                        application.getJoiningLetterPath()
                )
                .appliedDate(application.getAppliedDate())
                .applicationStatus(
                        MapperUtil.parseEnum(
                                ApplicationStatus.class,
                                application.getApplicationStatus()
                        )
                )
                .applicationDetails(
                        toDetailResponses(
                                placementApplicationDtlRepository
                                        .getDetailsByApplicationId(
                                                application.getId()
                                        )
                        )
                )
                .build();
    }

    /**
     * Maps application detail projections to response DTOs.
     */
    private List<PlacementApplicationDtlResponseDTO> toDetailResponses(
            List<ApplicationDtlBean> details) {

        List<PlacementApplicationDtlResponseDTO> responses =
                new ArrayList<>();

        if (details == null || details.isEmpty()) {
            return responses;
        }

        for (ApplicationDtlBean detail : details) {

            if (detail == null) {
                continue;
            }

            responses.add(
                    PlacementApplicationDtlResponseDTO.builder()
                            .applicationDetailId(
                                    detail.getApplicationDetailId()
                            )
                            .fieldName(detail.getFieldName())
                            .fieldValue(detail.getFieldValue())
                            .build()
            );
        }

        return responses;
    }
}