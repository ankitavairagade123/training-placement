package T_And_P.Training_and_Placement.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.EligibilityBean;
import T_And_P.Training_and_Placement.bean.PlannerDtlBean;
import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.constant.CriteriaRule;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.EligibilityCheckResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.EligibilityMasterRepository;
import T_And_P.Training_and_Placement.repository.PlannerDtlRepository;
import T_And_P.Training_and_Placement.repository.StudentRepository;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Compares a student's academic profile against the eligibility rules
 * configured on a planner.
 * Failed rules are returned as human-readable reasons so the UI can
 * show why Apply is blocked.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EligibilityValidationService {

    private static final Set<String> RESUME_TYPES =
            Collections.unmodifiableSet(
                    new HashSet<>(Arrays.asList(
                            "RESUME",
                            "RESUMEREQUIRED",
                            "RESUME REQUIRED"
                    ))
            );

    private final StudentRepository studentRepository;
    private final TrainingAndPlacementPlannerHdrRepository plannerHdrRepository;
    private final PlannerDtlRepository plannerDtlRepository;
    private final EligibilityMasterRepository eligibilityMasterRepository;
    private final MessageUtil messageUtil;

    /**
     * Loads student + planner and returns whether the student is eligible.
     */
    public EligibilityCheckResponseDTO checkEligibility(
            Long studentId,
            Long plannerId,
            String resumePath) {

        log.info(
                "checkEligibility() started for studentId={}, plannerId={}",
                studentId,
                plannerId
        );

        try {
            StudentBean student = studentRepository
                    .getStudentById(studentId)
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest("student.not.found"),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            plannerHdrRepository
                    .getPlannerById(plannerId)
                    .orElseThrow(() ->
                            new PlacementApplicationException(
                                    messageUtil.badRequest("planner.not.found"),
                                    HttpStatus.BAD_REQUEST
                            )
                    );

            List<String> reasons = validate(
                    student,
                    plannerDtlRepository.getPlannerDetails(plannerId),
                    resumePath
            );

            log.info(
                    "checkEligibility() completed for studentId={}, plannerId={}, eligible={}, reasonCount={}",
                    studentId,
                    plannerId,
                    reasons.isEmpty(),
                    reasons.size()
            );

            return EligibilityCheckResponseDTO.builder()
                    .studentId(studentId)
                    .plannerId(plannerId)
                    .eligible(reasons.isEmpty())
                    .reasons(reasons == null
                            ? new ArrayList<>()
                            : reasons)
                    .build();

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "checkEligibility() failed for studentId={}, plannerId={}",
                    studentId,
                    plannerId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Walks through ACTIVE planner eligibility rows and collects failed conditions.
     */
    public List<String> validate(
            StudentBean student,
            List<PlannerDtlBean> details,
            String resumePath) {

        log.info(
                "validate() started for studentId={}",
                student == null ? null : student.getStudentId()
        );

        List<String> reasons = new ArrayList<>();

        if (student == null || details == null || details.isEmpty()) {
            log.info(
                    "validate() completed with no eligibility rows configured"
            );
            return reasons;
        }

        Map<Long, EligibilityBean> eligibilityById = mapEligibilityById(details);

        for (PlannerDtlBean detail : details) {

            if (detail == null) {
                continue;
            }

            Status status = MapperUtil.parseEnum(
                    Status.class,
                    detail.getStatus(),
                    Status.ACTIVE
            );

            if (status != Status.ACTIVE) {
                log.debug(
                        "validate() skipping inactive eligibility row id={}",
                        detail.getId()
                );
                continue;
            }

            EligibilityBean eligibility = eligibilityById.get(detail.getEligibilityId());
            String eligibilityType = eligibility == null
                    ? detail.getEligibilityType()
                    : eligibility.getEligibilityType();
            String type = normalize(eligibilityType);
            String configuredValue = detail.getCriteriaValue();

            CriteriaRule rule = MapperUtil.parseEnum(
                    CriteriaRule.class,
                    detail.getCriteriaRule()
            );

            log.info(
                    "validate() checking type={}, rule={}, configuredValue={}",
                    type,
                    rule,
                    configuredValue
            );

            if (RESUME_TYPES.contains(type)) {
                boolean required = isYes(configuredValue);

                if (required
                        && !StringUtils.hasText(resumePath)
                        && !StringUtils.hasText(student.getResumePath())) {

                    reasons.add(
                            messageUtil.get("eligibility.resume.required")
                    );
                }

                continue;
            }

            ComparableValue actual = resolveStudentValue(student, type);

            if (actual == null) {
                reasons.add(
                        messageUtil.get(
                                "eligibility.field.missing",
                                eligibilityType
                        )
                );
                continue;
            }

            if (!matches(
                    actual,
                    configuredValue,
                    rule,
                    type
            )) {
                reasons.add(
                        buildReason(
                                eligibilityType,
                                rule,
                                configuredValue,
                                actual.display
                        )
                );
            }
        }

        log.info(
                "validate() completed for studentId={}, failedRuleCount={}",
                student.getStudentId(),
                reasons.size()
        );

        return reasons;
    }

    /**
     * Loads eligibility master rows once for all planner detail ids.
     */
    private Map<Long, EligibilityBean> mapEligibilityById(List<PlannerDtlBean> details) {
        Map<Long, EligibilityBean> eligibilityById = new HashMap<Long, EligibilityBean>();
        List<Long> eligibilityIds = new ArrayList<Long>();
        for (PlannerDtlBean detail : details) {
            if (detail != null && detail.getEligibilityId() != null
                    && !eligibilityIds.contains(detail.getEligibilityId())) {
                eligibilityIds.add(detail.getEligibilityId());
            }
        }
        if (eligibilityIds.isEmpty()) {
            return eligibilityById;
        }
        List<EligibilityBean> eligibilityRows = eligibilityMasterRepository.getEligibilityByIds(eligibilityIds);
        if (eligibilityRows != null) {
            for (EligibilityBean eligibility : eligibilityRows) {
                if (eligibility != null && eligibility.getId() != null) {
                    eligibilityById.put(eligibility.getId(), eligibility);
                }
            }
        }
        return eligibilityById;
    }

    /**
     * Maps an eligibility type name to the matching student academic field.
     */
    private ComparableValue resolveStudentValue(
            StudentBean student,
            String type) {

        log.debug(
                "resolveStudentValue() started for type={}",
                type
        );

        if (type.contains("SSC")) {
            return student.getSscPercentage() == null
                    ? null
                    : ComparableValue.number(
                    student.getSscPercentage()
            );
        }

        if (type.contains("HSC")) {
            return student.getHscPercentage() == null
                    ? null
                    : ComparableValue.number(
                    student.getHscPercentage()
            );
        }

        if (type.contains("CGPA") || type.contains("UG")) {
            return student.getUgCgpa() == null
                    ? null
                    : ComparableValue.number(
                    student.getUgCgpa()
            );
        }

        if (type.contains("ATTEND")) {
            return student.getAttendance() == null
                    ? null
                    : ComparableValue.number(
                    student.getAttendance()
            );
        }

        if (type.contains("BACKLOG")) {
            return student.getActiveBacklogs() == null
                    ? null
                    : ComparableValue.number(
                    student.getActiveBacklogs().doubleValue()
            );
        }

        if (type.contains("BRANCH")) {
            return !StringUtils.hasText(student.getBranch())
                    ? null
                    : ComparableValue.text(
                    student.getBranch()
            );
        }

        if (type.contains("SEMESTER")) {
            return student.getSemester() == null
                    ? null
                    : ComparableValue.number(
                    student.getSemester().doubleValue()
            );
        }

        if (type.contains("PASSING") || type.contains("YEAR")) {
            return student.getPassingYear() == null
                    ? null
                    : ComparableValue.number(
                    student.getPassingYear().doubleValue()
            );
        }

        log.info(
                "resolveStudentValue() found no matching student field for type={}",
                type
        );

        return null;
    }

    /**
     * Applies Greater / Less / Equals / IN comparison between
     * student value and configured value.
     */
    private boolean matches(
            ComparableValue actual,
            String configuredValue,
            CriteriaRule rule,
            String type) {

        log.debug(
                "matches() started for type={}, rule={}, configuredValue={}",
                type,
                rule,
                configuredValue
        );

        if (!StringUtils.hasText(configuredValue)) {
            return true;
        }

        CriteriaRule effectiveRule =
                rule == null ? defaultRule(type) : rule;

        if (effectiveRule == CriteriaRule.IN
                || type.contains("BRANCH")
                || (type.contains("SEMESTER")
                && configuredValue.contains(","))) {

            return Arrays.stream(configuredValue.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .anyMatch(value ->
                            value.equalsIgnoreCase(actual.display)
                    );
        }

        if (effectiveRule == CriteriaRule.EQUALS || !actual.numeric) {
            return actual.display.equalsIgnoreCase(
                    configuredValue.trim()
            );
        }

        Double expected = parseNumber(configuredValue);

        if (expected == null) {
            return false;
        }

        if (effectiveRule == CriteriaRule.GREATER_THAN) {
            return actual.number > expected;
        }

        if (effectiveRule == CriteriaRule.GREATER_THAN_EQUALS_TO) {
            return actual.number >= expected;
        }

        if (effectiveRule == CriteriaRule.LESS_THAN) {
            return actual.number < expected;
        }

        if (effectiveRule == CriteriaRule.LESS_THAN_EQUALS_TO) {
            return actual.number <= expected;
        }

        return actual.number >= expected;
    }

    /**
     * Picks a sensible default operator when planner row has no condition.
     * Example: backlogs default to <=, percentages default to >=.
     */
    private CriteriaRule defaultRule(String type) {

        log.debug(
                "defaultRule() started for type={}",
                type
        );

        if (type.contains("BACKLOG")) {
            return CriteriaRule.LESS_THAN_EQUALS_TO;
        }

        if (type.contains("BRANCH") || type.contains("SEMESTER")) {
            return CriteriaRule.IN;
        }

        if (type.contains("PASSING") || type.contains("YEAR")) {
            return CriteriaRule.EQUALS;
        }

        return CriteriaRule.GREATER_THAN_EQUALS_TO;
    }

    /**
     * Builds a message like
     * "SSC greater than equals to 60 (student value: 55)".
     */
    private String buildReason(
            String eligibilityType,
            CriteriaRule rule,
            String configuredValue,
            String actual) {

        log.debug(
                "buildReason() started for eligibilityType={}",
                eligibilityType
        );

        String condition = rule == null
                ? messageUtil.get("eligibility.condition.default")
                : rule.name()
                .replace('_', ' ')
                .toLowerCase(Locale.ROOT);

        return messageUtil.get(
                "eligibility.rule.failed",
                eligibilityType,
                condition,
                configuredValue,
                actual
        );
    }

    /**
     * Parses numbers from values such as 60, 60% or '60'.
     */
    private Double parseNumber(String value) {

        log.debug(
                "parseNumber() started for value={}",
                value
        );

        try {
            String cleaned = value
                    .replace("%", "")
                    .replace("'", "")
                    .trim();

            return Double.valueOf(cleaned);

        } catch (Exception e) {
            log.info(
                    "parseNumber() failed for value={}",
                    value
            );

            return null;
        }
    }

    /**
     * Treats YES / TRUE / 1 / blank as "required" for resume-type rules.
     */
    private boolean isYes(String value) {

        log.debug(
                "isYes() started for value={}",
                value
        );

        return !StringUtils.hasText(value)
                || value.equalsIgnoreCase("YES")
                || value.equalsIgnoreCase("TRUE")
                || value.equals("1");
    }

    /**
     * Normalizes eligibility type names so "SSC Percentage"
     * and "ssc_percentage" match the same rule.
     */
    private String normalize(String value) {

        log.debug("normalize() started");

        return value == null
                ? ""
                : value.replace("_", " ")
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    /**
     * Internal holder for either a numeric student value
     * or a text value.
     */
    private static final class ComparableValue {

        private final boolean numeric;
        private final Double number;
        private final String display;

        private ComparableValue(
                boolean numeric,
                Double number,
                String display) {

            this.numeric = numeric;
            this.number = number;
            this.display = display;
        }

        private static ComparableValue number(Double value) {
            return new ComparableValue(
                    true,
                    value,
                    String.valueOf(value)
            );
        }

        private static ComparableValue text(String value) {
            return new ComparableValue(
                    false,
                    null,
                    value
            );
        }
    }
}