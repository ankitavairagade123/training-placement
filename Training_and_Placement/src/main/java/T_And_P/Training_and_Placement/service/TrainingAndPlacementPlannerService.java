package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.*;
import T_And_P.Training_and_Placement.constant.*;
import T_And_P.Training_and_Placement.dto.*;
import T_And_P.Training_and_Placement.entity.*;
import T_And_P.Training_and_Placement.event.PlannerEventPublisher;
import T_And_P.Training_and_Placement.event.PlannerPublishedEvent;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.*;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for planner create, update, publish and reject.
 * Publish runs all document validations, sets ACTIVE, then emits PlannerPublishedEvent.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TrainingAndPlacementPlannerService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

    private final TrainingAndPlacementPlannerHdrRepository plannerHdrRepository;
    private final CompanyRepository companyRepository;
    private final PlannerDtlRepository plannerDtlRepository;
    private final EligibilityMasterRepository eligibilityMasterRepository;
    private final PlannerQuestionRepository plannerQuestionRepository;
    private final StudentRepository studentRepository;
    private final EligibilityValidationService eligibilityValidationService;
    private final PlannerEventPublisher plannerEventPublisher;
    private final MessageUtil messageUtil;

    /**
     * Returns published planners whose registration window is currently open.
     * Used by the student portal to show the Apply list.
     */
    public List<PlannerResponseDTO> getActivePlanners() {

        log.info("getActivePlanners() started");

        List<PlannerHdrBean> projections =
                plannerHdrRepository.getActivePlanners();

        if (CollectionUtils.isEmpty(projections)) {
            log.info("getActivePlanners() completed with empty list");
            return Collections.emptyList();
        }

        List<PlannerResponseDTO> response = projections.stream()
                .map(planner ->
                        convertToResponse(
                                planner,
                                plannerDtlRepository.getPlannerDetails(
                                        planner.getId()
                                ),
                                plannerQuestionRepository
                                        .getQuestionsByPlannerId(
                                                planner.getId()
                                        ),
                                plannerQuestionRepository
                                        .getOptionsByPlannerId(
                                                planner.getId()
                                        )
                        )
                )
                .collect(Collectors.toList());

        log.info(
                "getActivePlanners() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Creates a DRAFT planner or updates an existing non-published planner.
     * Also replaces eligibility rows and custom questions in the same transaction.
     */
    @Transactional
    public PlannerResponseDTO savePlanner(PlannerRequestDTO request) {

        log.info(
                "savePlanner() started for id={}, plannerName={}, companyId={}",
                request == null ? null : request.getId(),
                request == null ? null : request.getPlannerName(),
                request == null ? null : request.getCompanyId()
        );

        try {
            validateRequest(request);

            companyRepository
                    .getByIdCompany(request.getCompanyId())
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest(
                                            "company.not.found"
                                    ),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            CompanyMaster company =
                    companyRepository.getReferenceById(
                            request.getCompanyId()
                    );

            TrainingAndPlacementPlannerHdr planner;

            if (request.getId() != null) {

                log.info(
                        "savePlanner() update path for id={}",
                        request.getId()
                );

                PlannerHdrBean existing =
                        plannerHdrRepository.getPlannerById(
                                        request.getId()
                                )
                                .orElseThrow(() ->
                                        new PlacementApplicationException(
                                                messageUtil.badRequest(
                                                        "planner.not.found"
                                                ),
                                                HttpStatus.BAD_REQUEST
                                        )
                                );

                if (Status.ACTIVE.name().equals(existing.getStatus())) {
                    log.info(
                            "savePlanner() rejected because planner {} is already published",
                            request.getId()
                    );

                    throw new PlacementApplicationException(
                            messageUtil.badRequest(
                                    "planner.published.cannot.edit"
                            ),
                            HttpStatus.BAD_REQUEST
                    );
                }

                planner = plannerHdrRepository.getReferenceById(
                        request.getId()
                );

                if (planner.getTrainingAndPlacementPlannerDtls() != null) {
                    planner.getTrainingAndPlacementPlannerDtls().clear();
                }

                if (planner.getQuestions() != null) {
                    planner.getQuestions().clear();
                }

            } else {

                log.info(
                        "savePlanner() create path, status will be DRAFT"
                );

                planner = new TrainingAndPlacementPlannerHdr();
                planner.setStatus(Status.DRAFT);
            }

            planner.setPlannerName(
                    MapperUtil.trimToNull(
                            request.getPlannerName()
                    )
            );

            planner.setPlannerDesc(
                    MapperUtil.trimToNull(
                            request.getPlannerDesc()
                    )
            );

            planner.setPlannerType(request.getPlannerType());
            planner.setMode(request.getMode());
            planner.setPlannerScheduleType(
                    request.getPlannerScheduleType()
            );
            planner.setStartTime(request.getStartTime());
            planner.setEndTime(request.getEndTime());
            planner.setRegistrationStartDate(
                    request.getRegistrationStartDate()
            );
            planner.setRegistrationEndDate(
                    request.getRegistrationEndDate()
            );
            planner.setMaxStudents(request.getMaxStudents());

            planner.setVenue(
                    MapperUtil.trimToNull(request.getVenue())
            );

            planner.setWebsite(
                    MapperUtil.trimToNull(request.getWebsite())
            );

            planner.setMeetingLink(
                    MapperUtil.trimToNull(request.getMeetingLink())
            );

            planner.setRemarks(
                    MapperUtil.trimToNull(request.getRemarks())
            );

            planner.setAttachmentPath(
                    MapperUtil.trimToNull(request.getAttachmentPath())
            );

            planner.setCompany(company);

            if (StringUtils.hasText(request.getStatus())
                    && request.getId() != null) {
                planner.setStatus(
                        Status.valueOf(request.getStatus())
                );
            }

            if (planner.getTrainingAndPlacementPlannerDtls() == null) {
                planner.setTrainingAndPlacementPlannerDtls(
                        new ArrayList<>()
                );
            }

            if (!CollectionUtils.isEmpty(request.getPlannerDetails())) {
                planner.getTrainingAndPlacementPlannerDtls().addAll(
                        buildEligibilityDetails(
                                request,
                                planner
                        )
                );
            }

            if (planner.getQuestions() == null) {
                planner.setQuestions(new ArrayList<>());
            }

            if (!CollectionUtils.isEmpty(request.getQuestions())) {
                planner.getQuestions().addAll(
                        buildQuestions(
                                request.getQuestions(),
                                planner
                        )
                );
            }

            TrainingAndPlacementPlannerHdr saved =
                    plannerHdrRepository.save(planner);

            log.info(
                    "savePlanner() completed for plannerId={}, status={}",
                    saved.getId(),
                    saved.getStatus()
            );

            return getPlannerById(saved.getId());

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "savePlanner() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("planner.cannot.save"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Publishes a planner after validating company, eligibility, dates and mode-specific fields.
     * On success: status=ACTIVE, published_by/published_at set, event published, emails sent.
     */
    @Transactional
    public PlannerResponseDTO publishPlanner(
            Long plannerId,
            String publishedBy) {

        log.info(
                "publishPlanner() started for plannerId={}, publishedBy={}",
                plannerId,
                publishedBy
        );

        try {
            PlannerHdrBean plannerBean =
                    plannerHdrRepository.getPlannerById(plannerId)
                            .orElseThrow(() ->
                                    new PlacementApplicationException(
                                            messageUtil.badRequest(
                                                    "planner.not.found"
                                            ),
                                            HttpStatus.BAD_REQUEST
                                    )
                            );

            CompanyBean companyBean =
                    companyRepository
                            .getByIdCompanyDetails(
                                    plannerBean.getCompanyId()
                            )
                            .orElseThrow(() ->
                                    new PlacementApplicationException(
                                            messageUtil.badRequest(
                                                    "company.not.found"
                                            ),
                                            HttpStatus.BAD_REQUEST
                                    )
                            );

            List<PlannerDtlBean> eligibilityRows =
                    plannerDtlRepository.getPlannerDetails(plannerId);

            validateForPublish(
                    plannerBean,
                    companyBean,
                    eligibilityRows
            );

            TrainingAndPlacementPlannerHdr planner =
                    plannerHdrRepository.getReferenceById(plannerId);

            planner.setStatus(Status.ACTIVE);

            planner.setPublishedBy(
                    StringUtils.hasText(publishedBy)
                            ? publishedBy
                            : "TPO"
            );

            planner.setPublishedAt(LocalDateTime.now());

            TrainingAndPlacementPlannerHdr saved =
                    plannerHdrRepository.save(planner);

            log.info(
                    "publishPlanner() planner {} marked ACTIVE, finding eligible students",
                    saved.getId()
            );

            List<String> eligibleEmails =
                    studentRepository.getAllStudents()
                            .stream()
                            .filter(student -> student != null)
                            .filter(student ->
                                    eligibilityValidationService
                                            .validate(
                                                    student,
                                                    eligibilityRows,
                                                    student.getResumePath()
                                            )
                                            .isEmpty()
                            )
                            .map(StudentBean::getEmail)
                            .filter(StringUtils::hasText)
                            .collect(Collectors.toList());

            plannerEventPublisher.publish(
                    PlannerPublishedEvent.builder()
                            .plannerId(saved.getId())
                            .plannerName(saved.getPlannerName())
                            .plannerType(
                                    saved.getPlannerType() == null
                                            ? null
                                            : saved.getPlannerType().name()
                            )
                            .companyName(companyBean.getCompanyName())
                            .companyCode(companyBean.getCompanyCode())
                            .registrationStartDate(
                                    saved.getRegistrationStartDate()
                            )
                            .registrationEndDate(
                                    saved.getRegistrationEndDate()
                            )
                            .publishedAt(saved.getPublishedAt())
                            .publishedBy(saved.getPublishedBy())
                            .eligibleStudentEmails(eligibleEmails)
                            .build()
            );

            log.info(
                    "publishPlanner() completed for plannerId={}, eligibleEmailCount={}",
                    saved.getId(),
                    eligibleEmails.size()
            );

            return getPlannerById(saved.getId());

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "publishPlanner() failed for plannerId={}",
                    plannerId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("planner.cannot.publish"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Rejects a draft planner from the Publish Planner screen.
     */
    @Transactional
    public PlannerResponseDTO rejectPlanner(Long plannerId) {

        log.info(
                "rejectPlanner() started for plannerId={}",
                plannerId
        );

        try {
            PlannerHdrBean plannerBean =
                    plannerHdrRepository.getPlannerById(plannerId)
                            .orElseThrow(() ->
                                    new PlacementApplicationException(
                                            messageUtil.badRequest(
                                                    "planner.not.found"
                                            ),
                                            HttpStatus.BAD_REQUEST
                                    )
                            );

            if (Status.ACTIVE.name().equals(plannerBean.getStatus())) {
                log.info(
                        "rejectPlanner() rejected because planner {} is already published",
                        plannerId
                );

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "planner.published.cannot.reject"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            TrainingAndPlacementPlannerHdr planner =
                    plannerHdrRepository.getReferenceById(plannerId);

            planner.setStatus(Status.REJECTED);

            plannerHdrRepository.save(planner);

            log.info(
                    "rejectPlanner() completed for plannerId={}",
                    plannerId
            );

            return getPlannerById(plannerId);

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "rejectPlanner() failed for plannerId={}",
                    plannerId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("planner.cannot.reject"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Deletes a non-published planner.
     */
    @Transactional
    public void deletePlanner(Long id) {

        log.info(
                "deletePlanner() started for id={}",
                id
        );

        try {
            PlannerHdrBean planner =
                    plannerHdrRepository.getPlannerById(id)
                            .orElseThrow(() ->
                                    new PlacementApplicationException(
                                            messageUtil.badRequest(
                                                    "planner.not.found"
                                            ),
                                            HttpStatus.BAD_REQUEST
                                    )
                            );

            if (Status.ACTIVE.name().equals(planner.getStatus())) {
                log.info(
                        "deletePlanner() rejected because planner {} is published",
                        id
                );

                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "planner.published.cannot.delete"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            plannerHdrRepository.deleteById(id);

            log.info(
                    "deletePlanner() completed for id={}",
                    id
            );

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "deletePlanner() failed for id={} due to reference or DB error",
                    id,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("planner.cannot.delete"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Loads planner header, eligibility configuration and questions.
     */
    public PlannerResponseDTO getPlannerById(Long id) {

        log.info(
                "getPlannerById() started for id={}",
                id
        );

        PlannerHdrBean planner =
                plannerHdrRepository.getPlannerById(id)
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest(
                                                "planner.not.found"
                                        ),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

        PlannerResponseDTO response =
                convertToResponse(
                        planner,
                        plannerDtlRepository.getPlannerDetails(id),
                        plannerQuestionRepository
                                .getQuestionsByPlannerId(id),
                        plannerQuestionRepository
                                .getOptionsByPlannerId(id)
                );

        log.info(
                "getPlannerById() completed for id={}, status={}",
                id,
                response.getStatus()
        );

        return response;
    }

    /**
     * Returns every planner for the TPO/Faculty grid, newest first.
     */
    public List<PlannerResponseDTO> getAllPlanners() {

        log.info("getAllPlanners() started");

        List<PlannerHdrBean> planners =
                plannerHdrRepository.getAllPlanners();

        if (CollectionUtils.isEmpty(planners)) {
            log.info(
                    "getAllPlanners() completed with empty list"
            );
            return Collections.emptyList();
        }

        List<PlannerResponseDTO> response =
                planners.stream()
                        .map(planner ->
                                convertToResponse(
                                        planner,
                                        plannerDtlRepository.getPlannerDetails(
                                                planner.getId()
                                        ),
                                        plannerQuestionRepository
                                                .getQuestionsByPlannerId(
                                                        planner.getId()
                                                ),
                                        plannerQuestionRepository
                                                .getOptionsByPlannerId(
                                                        planner.getId()
                                                )
                                )
                        )
                        .collect(Collectors.toList());

        log.info(
                "getAllPlanners() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Builds planner eligibility rows. Only ACTIVE eligibility master types are allowed.
     */
    private List<TrainingAndPlacementPlannerDtl> buildEligibilityDetails(
            PlannerRequestDTO request,
            TrainingAndPlacementPlannerHdr planner) {

        log.info(
                "buildEligibilityDetails() started, rowCount={}",
                request.getPlannerDetails() == null
                        ? 0
                        : request.getPlannerDetails().size()
        );

        List<TrainingAndPlacementPlannerDtl> details =
                new ArrayList<>();

        for (PlannerDtlDTO dtl : request.getPlannerDetails()) {

            if (dtl == null) {
                continue;
            }

            if (dtl.getEligibilityId() == null) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "eligibility.type.required.on.planner"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            EligibilityBean eligibility =
                    eligibilityMasterRepository
                            .getEligibilityById(dtl.getEligibilityId())
                            .orElseThrow(() ->
                                    new PlacementApplicationException(
                                            messageUtil.badRequest(
                                                    "eligibility.not.found"
                                            ),
                                            HttpStatus.BAD_REQUEST
                                    )
                            );

            if (!Status.ACTIVE.name().equals(
                    eligibility.getStatus()
            )) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "eligibility.only.active"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            details.add(
                    TrainingAndPlacementPlannerDtl.builder()
                            .plannerHdr(planner)
                            .eligibilityMaster(
                                    eligibilityMasterRepository
                                            .getReferenceById(
                                                    dtl.getEligibilityId()
                                            )
                            )
                            .criteriaRule(dtl.getCriteriaRule())
                            .criteriaValue(
                                    MapperUtil.trimToNull(
                                            dtl.getCriteriaValue()
                                    )
                            )
                            .mandatory(
                                    Boolean.TRUE.equals(
                                            dtl.getMandatory()
                                    )
                            )
                            .status(
                                    dtl.getStatus() == null
                                            ? Status.ACTIVE
                                            : dtl.getStatus()
                            )
                            .build()
            );
        }

        log.info(
                "buildEligibilityDetails() completed, rowCount={}",
                details.size()
        );

        return details;
    }

    /**
     * Builds custom application questions and their radio/multi-select options.
     */
    private List<PlannerQuestion> buildQuestions(
            List<PlannerQuestionDTO> questionDTOs,
            TrainingAndPlacementPlannerHdr planner) {

        log.info(
                "buildQuestions() started, questionCount={}",
                questionDTOs == null
                        ? 0
                        : questionDTOs.size()
        );

        List<PlannerQuestion> questions =
                new ArrayList<>();

        for (PlannerQuestionDTO dto : questionDTOs) {

            if (dto == null
                    || MapperUtil.isBlank(dto.getQuestion())
                    || dto.getFieldType() == null) {
                continue;
            }

            PlannerQuestion question =
                    PlannerQuestion.builder()
                            .plannerHdr(planner)
                            .question(
                                    MapperUtil.trimToNull(
                                            dto.getQuestion()
                                    )
                            )
                            .fieldType(dto.getFieldType())
                            .mandatory(
                                    Boolean.TRUE.equals(
                                            dto.getMandatory()
                                    )
                            )
                            .options(
                                    new ArrayList<QuestionOption>()
                            )
                            .build();

            if (!CollectionUtils.isEmpty(dto.getOptions())) {

                for (QuestionOptionDTO optionDTO :
                        dto.getOptions()) {

                    if (optionDTO == null
                            || MapperUtil.isBlank(
                            optionDTO.getOptionText()
                    )) {
                        continue;
                    }

                    question.getOptions().add(
                            QuestionOption.builder()
                                    .question(question)
                                    .optionText(
                                            MapperUtil.trimToNull(
                                                    optionDTO.getOptionText()
                                            )
                                    )
                                    .displayOrder(
                                            optionDTO.getDisplayOrder()
                                    )
                                    .build()
                    );
                }
            }

            questions.add(question);
        }

        log.info(
                "buildQuestions() completed, questionCount={}",
                questions.size()
        );

        return questions;
    }

    /**
     * Validates planner create/update request:
     * mandatory fields, date range, venue/meeting link.
     */
    private void validateRequest(
            PlannerRequestDTO request) {

        log.info("validateRequest() started");

        if (request == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.request.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(request.getPlannerName())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.name.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getCompanyId() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.company.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getPlannerType() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.type.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getMode() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.mode.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getPlannerScheduleType() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.schedule.type.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getStartTime() == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.start.time.required"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getPlannerScheduleType()
                == PlannerScheduleType.RANGE) {

            if (request.getEndTime() == null) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "planner.end.time.required.range"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }

            if (!request.getEndTime().isAfter(
                    request.getStartTime()
            )) {
                throw new PlacementApplicationException(
                        messageUtil.badRequest(
                                "planner.end.time.after.start"
                        ),
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        if (request.getPlannerScheduleType()
                == PlannerScheduleType.FIXED) {
            request.setEndTime(null);
        }

        if (request.getMode() == Mode.ONLINE
                && !StringUtils.hasText(
                firstNonBlank(
                        request.getMeetingLink(),
                        request.getWebsite()
                )
        )) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.meeting.link.required.online"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getMode() == Mode.OFFLINE
                && !StringUtils.hasText(
                request.getVenue()
        )) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.venue.required.offline"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (request.getRegistrationStartDate() != null
                && request.getRegistrationEndDate() != null
                && !request.getRegistrationEndDate()
                .isAfter(
                        request.getRegistrationStartDate()
                )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.registration.end.after.start"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validateRequest() completed");
    }

    /**
     * Document publish checks:
     * company complete, planner complete, eligibility present, dates valid.
     */
    private void validateForPublish(
            PlannerHdrBean planner,
            CompanyBean company,
            List<PlannerDtlBean> details) {

        log.info(
                "validateForPublish() started for plannerId={}",
                planner.getId()
        );

        if (company == null
                || !StringUtils.hasText(
                company.getCompanyName()
        )
                || !StringUtils.hasText(
                company.getCompanyCode()
        )
                || !StringUtils.hasText(
                company.getCompanyType()
        )
                || !StringUtils.hasText(
                company.getIndustryType()
        )
                || !StringUtils.hasText(
                company.getWebsite()
        )
                || !StringUtils.hasText(
                company.getEmail()
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "company.details.incomplete"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (StringUtils.hasText(company.getStatus())
                && !Status.ACTIVE.name().equals(
                company.getStatus()
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "company.must.be.active"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(
                planner.getPlannerName()
        )
                || !StringUtils.hasText(
                planner.getPlannerType()
        )
                || !StringUtils.hasText(
                planner.getMode()
        )
                || planner.getStartTime() == null) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.details.incomplete"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (CollectionUtils.isEmpty(details)) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.eligibility.not.configured"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (planner.getRegistrationStartDate() == null
                || planner.getRegistrationEndDate() == null) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.registration.dates.invalid"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!planner.getRegistrationEndDate().isAfter(
                planner.getRegistrationStartDate()
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.registration.dates.invalid"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (PlannerScheduleType.RANGE.name().equals(
                planner.getPlannerScheduleType()
        )
                && (
                planner.getEndTime() == null
                        || !planner.getEndTime().isAfter(
                        planner.getStartTime()
                )
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.event.dates.invalid"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (Mode.OFFLINE.name().equals(
                planner.getMode()
        )
                && !StringUtils.hasText(
                planner.getVenue()
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.venue.required.publish"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (Mode.ONLINE.name().equals(
                planner.getMode()
        )
                && !StringUtils.hasText(
                firstNonBlank(
                        planner.getMeetingLink(),
                        planner.getWebsite()
                )
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.meeting.link.required.publish"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (Status.ACTIVE.name().equals(
                planner.getStatus()
        )) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "planner.already.published"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info(
                "validateForPublish() completed for plannerId={}",
                planner.getId()
        );
    }

    /**
     * Converts native-query planner projection plus child rows into the API response.
     */
    private PlannerResponseDTO convertToResponse(
            PlannerHdrBean planner,
            List<PlannerDtlBean> details,
            List<PlannerQuestionBean> questions,
            List<QuestionOptionBean> options) {

        if (planner == null) {
            return null;
        }

        log.debug(
                "convertToResponse() started for plannerId={}",
                planner.getId()
        );

        LocalDateTime startTime = planner.getStartTime();
        LocalDateTime endTime = planner.getEndTime();

        List<PlannerDtlDTO> plannerDetails =
                new ArrayList<>();

        if (!CollectionUtils.isEmpty(details)) {

            for (PlannerDtlBean dtl : details) {

                if (dtl == null) {
                    continue;
                }

                plannerDetails.add(
                        PlannerDtlDTO.builder()
                                .id(dtl.getId())
                                .eligibilityId(
                                        dtl.getEligibilityId()
                                )
                                .eligibilityType(
                                        dtl.getEligibilityType()
                                )
                                .criteriaValue(
                                        dtl.getCriteriaValue()
                                )
                                .criteriaRule(
                                        parseCriteria(
                                                dtl.getCriteriaRule()
                                        )
                                )
                                .status(
                                        parseStatus(
                                                dtl.getStatus()
                                        )
                                )
                                .mandatory(
                                        dtl.getMandatory()
                                )
                                .build()
                );
            }
        }

        List<PlannerQuestionDTO> questionDTOs =
                toQuestionDTOs(
                        questions,
                        options
                );

        return PlannerResponseDTO.builder()
                .id(planner.getId())
                .plannerName(planner.getPlannerName())
                .plannerDesc(planner.getPlannerDesc())
                .plannerType(
                        MapperUtil.parseEnum(
                                PlannerType.class,
                                planner.getPlannerType()
                        )
                )
                .mode(
                        MapperUtil.parseEnum(
                                Mode.class,
                                planner.getMode()
                        )
                )
                .plannerScheduleType(
                        MapperUtil.parseEnum(
                                PlannerScheduleType.class,
                                planner.getPlannerScheduleType()
                        )
                )
                .status(
                        MapperUtil.parseEnum(
                                Status.class,
                                planner.getStatus()
                        )
                )
                .maxStudents(
                        MapperUtil.toInteger(
                                planner.getMaxStudents()
                        )
                )
                .companyId(planner.getCompanyId())
                .companyName(planner.getCompanyName())
                .companyCode(planner.getCompanyCode())
                .startDate(
                        startTime != null
                                ? startTime.format(
                                DATE_FORMATTER
                        )
                                : ""
                )
                .startTimeDisplay(
                        startTime != null
                                ? startTime.format(
                                TIME_FORMATTER
                        )
                                : ""
                )
                .endDate(
                        endTime != null
                                ? endTime.format(
                                DATE_FORMATTER
                        )
                                : ""
                )
                .endTimeDisplay(
                        endTime != null
                                ? endTime.format(
                                TIME_FORMATTER
                        )
                                : ""
                )
                .startDateTime(startTime)
                .endDateTime(endTime)
                .registrationStartDate(
                        planner.getRegistrationStartDate()
                )
                .registrationEndDate(
                        planner.getRegistrationEndDate()
                )
                .venue(planner.getVenue())
                .website(planner.getWebsite())
                .meetingLink(planner.getMeetingLink())
                .remarks(planner.getRemarks())
                .attachmentPath(planner.getAttachmentPath())
                .publishedBy(planner.getPublishedBy())
                .publishedAt(planner.getPublishedAt())
                .plannerDetails(plannerDetails)
                .questions(questionDTOs)
                .build();
    }

    /**
     * Maps a planner question entity and its options to DTO.
     */
    private List<PlannerQuestionDTO> toQuestionDTOs(
            List<PlannerQuestionBean> questions,
            List<QuestionOptionBean> options) {

        List<PlannerQuestionDTO> questionDTOs =
                new ArrayList<>();

        if (CollectionUtils.isEmpty(questions)) {
            return questionDTOs;
        }

        for (PlannerQuestionBean question : questions) {

            if (question == null) {
                continue;
            }

            List<QuestionOptionDTO> optionDTOs =
                    new ArrayList<>();

            if (!CollectionUtils.isEmpty(options)) {

                for (QuestionOptionBean option : options) {

                    if (option == null
                            || option.getQuestionId() == null
                            || !option.getQuestionId().equals(
                            question.getQuestionId()
                    )) {
                        continue;
                    }

                    optionDTOs.add(
                            QuestionOptionDTO.builder()
                                    .optionId(
                                            option.getOptionId()
                                    )
                                    .optionText(
                                            option.getOptionText()
                                    )
                                    .displayOrder(
                                            option.getDisplayOrder()
                                    )
                                    .build()
                    );
                }
            }

            questionDTOs.add(
                    PlannerQuestionDTO.builder()
                            .questionId(
                                    question.getQuestionId()
                            )
                            .question(
                                    question.getQuestion()
                            )
                            .fieldType(
                                    MapperUtil.parseEnum(
                                            FieldType.class,
                                            question.getFieldType()
                                    )
                            )
                            .mandatory(
                                    question.getMandatory()
                            )
                            .options(optionDTOs)
                            .build()
            );
        }

        return questionDTOs;
    }

    /**
     * Converts a criteria_rule DB value to enum.
     */
    private CriteriaRule parseCriteria(String value) {
        log.debug(
                "parseCriteria() started for value={}",
                value
        );

        return MapperUtil.parseEnum(
                CriteriaRule.class,
                value
        );
    }

    /**
     * Converts a status DB value to enum, defaulting to ACTIVE.
     */
    private Status parseStatus(String value) {
        log.debug(
                "parseStatus() started for value={}",
                value
        );

        return MapperUtil.parseEnum(
                Status.class,
                value,
                Status.ACTIVE
        );
    }

    /**
     * Returns the first non-blank string.
     * Used so meetingLink can fall back to website.
     */
    private String firstNonBlank(
            String first,
            String second) {

        log.debug("firstNonBlank() started");

        if (StringUtils.hasText(first)) {
            return first;
        }

        return second;
    }
}