package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.bean.ApplicationHdrBean;
import T_And_P.Training_and_Placement.bean.CompanyDashboardBean;
import T_And_P.Training_and_Placement.bean.PlannerDashboardBean;
import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.bean.StatusCountBean;
import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.CompanyDashboardDTO;
import T_And_P.Training_and_Placement.dto.DashboardRecentApplicationDTO;
import T_And_P.Training_and_Placement.dto.DashboardSummaryDTO;
import T_And_P.Training_and_Placement.dto.DashboardUpcomingPlannerDTO;
import T_And_P.Training_and_Placement.dto.PlannerDashboardDTO;
import T_And_P.Training_and_Placement.dto.StudentDashboardDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.CompanyRepository;
import T_And_P.Training_and_Placement.repository.PlacementApplicationHdrRepository;
import T_And_P.Training_and_Placement.repository.StudentRepository;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds TPO and student dashboard numbers from native count queries.
 */
@Slf4j
@Service
public class DashboardService {

    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final TrainingAndPlacementPlannerHdrRepository plannerRepository;
    private final PlacementApplicationHdrRepository applicationRepository;
    private final MessageUtil messageUtil;

    public DashboardService(CompanyRepository companyRepository,
                            StudentRepository studentRepository,
                            TrainingAndPlacementPlannerHdrRepository plannerRepository,
                            PlacementApplicationHdrRepository applicationRepository,
                            MessageUtil messageUtil) {
        log.info("DashboardService initialized");
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.plannerRepository = plannerRepository;
        this.applicationRepository = applicationRepository;
        this.messageUtil = messageUtil;
    }

    /**
     * TPO dashboard: KPI cards, application funnel, company-wise stats, recent applications, upcoming planners.
     */
    public DashboardSummaryDTO getTpoDashboard() {
        log.info("getTpoDashboard() started");
        try {
            Map<String, Long> plannerCounts = toCountMap(plannerRepository.countPlannersByStatus());
            Map<String, Long> applicationCounts = toCountMap(applicationRepository.countApplicationsByStatus());

            DashboardSummaryDTO response = DashboardSummaryDTO.builder()
                    .totalCompanies(toLong(companyRepository.countAllCompanies()))
                    .totalStudents(toLong(studentRepository.countAllStudents()))
                    .totalPlanners(toLong(plannerRepository.countAllPlanners()))
                    .draftPlanners(countOf(plannerCounts, Status.DRAFT.name()))
                    .activePlanners(countOf(plannerCounts, Status.ACTIVE.name()))
                    .rejectedPlanners(countOf(plannerCounts, Status.REJECTED.name()))
                    .inactivePlanners(countOf(plannerCounts, Status.INACTIVE.name()))
                    .totalApplications(toLong(applicationRepository.countAllApplications()))
                    .appliedCount(countOf(applicationCounts, ApplicationStatus.APPLIED.name()))
                    .shortlistedCount(countOf(applicationCounts, ApplicationStatus.SHORTLISTED.name()))
                    .interviewScheduledCount(countOf(applicationCounts, ApplicationStatus.INTERVIEW_SCHEDULED.name()))
                    .selectedCount(countOf(applicationCounts, ApplicationStatus.SELECTED.name()))
                    .rejectedCount(countOf(applicationCounts, ApplicationStatus.REJECTED.name()))
                    .cancelledCount(countOf(applicationCounts, ApplicationStatus.CANCELLED.name()))
                    .offerAcceptedCount(countOf(applicationCounts, ApplicationStatus.OFFER_ACCEPTED.name()))
                    .companyWise(toCompanyRows(applicationRepository.getCompanyWiseDashboard()))
                    .recentApplications(toRecentRows(applicationRepository.getRecentApplications()))
                    .upcomingPlanners(toUpcomingRows(plannerRepository.getActivePlanners()))
                    .build();
            log.info("getTpoDashboard() completed, applications={}", response.getTotalApplications());
            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.info("getTpoDashboard() failed", e);
            throw new PlacementApplicationException(messageUtil.badRequest("dashboard.cannot.load"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Planner-wise dashboard used on Faculty Verification.
     */
    public PlannerDashboardDTO getPlannerDashboard(Long plannerId) {
        log.info("getPlannerDashboard() started for plannerId={}", plannerId);
        try {
            PlannerDashboardBean bean = applicationRepository.getPlannerDashboard(plannerId)
                    .orElseThrow(() -> new PlacementApplicationException(
                            messageUtil.badRequest("dashboard.planner.not.found"), HttpStatus.BAD_REQUEST));
            PlannerDashboardDTO response = PlannerDashboardDTO.builder()
                    .plannerId(bean.getPlannerId())
                    .plannerName(bean.getPlannerName())
                    .companyName(bean.getCompanyName())
                    .plannerStatus(bean.getPlannerStatus())
                    .plannerType(bean.getPlannerType())
                    .totalApplications(toLong(bean.getTotalApplications()))
                    .appliedCount(toLong(bean.getAppliedCount()))
                    .shortlistedCount(toLong(bean.getShortlistedCount()))
                    .interviewScheduledCount(toLong(bean.getInterviewScheduledCount()))
                    .selectedCount(toLong(bean.getSelectedCount()))
                    .rejectedCount(toLong(bean.getRejectedCount()))
                    .cancelledCount(toLong(bean.getCancelledCount()))
                    .offerAcceptedCount(toLong(bean.getOfferAcceptedCount()))
                    .build();
            log.info("getPlannerDashboard() completed for plannerId={}", plannerId);
            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.info("getPlannerDashboard() failed for plannerId={}", plannerId, e);
            throw new PlacementApplicationException(messageUtil.badRequest("dashboard.cannot.load"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Student portal dashboard: my application funnel and open planners.
     */
    public StudentDashboardDTO getStudentDashboard(Long studentId) {
        log.info("getStudentDashboard() started for studentId={}", studentId);
        try {
            StudentBean student = studentRepository.getStudentById(studentId)
                    .orElseThrow(() -> new PlacementApplicationException(
                            messageUtil.badRequest("dashboard.student.not.found"), HttpStatus.BAD_REQUEST));
            Map<String, Long> applicationCounts = toCountMap(applicationRepository.countApplicationsByStudentId(studentId));
            List<ApplicationHdrBean> myApplications = applicationRepository.getApplicationsByStudentId(studentId);

            StudentDashboardDTO response = StudentDashboardDTO.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getStudentName())
                    .email(student.getEmail())
                    .totalApplications(myApplications == null ? 0L : myApplications.size())
                    .appliedCount(countOf(applicationCounts, ApplicationStatus.APPLIED.name()))
                    .shortlistedCount(countOf(applicationCounts, ApplicationStatus.SHORTLISTED.name()))
                    .interviewScheduledCount(countOf(applicationCounts, ApplicationStatus.INTERVIEW_SCHEDULED.name()))
                    .selectedCount(countOf(applicationCounts, ApplicationStatus.SELECTED.name()))
                    .rejectedCount(countOf(applicationCounts, ApplicationStatus.REJECTED.name()))
                    .cancelledCount(countOf(applicationCounts, ApplicationStatus.CANCELLED.name()))
                    .offerAcceptedCount(countOf(applicationCounts, ApplicationStatus.OFFER_ACCEPTED.name()))
                    .myApplications(toRecentRows(myApplications))
                    .upcomingPlanners(toUpcomingRows(plannerRepository.getActivePlanners()))
                    .build();
            log.info("getStudentDashboard() completed for studentId={}, applications={}",
                    studentId, response.getTotalApplications());
            return response;
        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.info("getStudentDashboard() failed for studentId={}", studentId, e);
            throw new PlacementApplicationException(messageUtil.badRequest("dashboard.cannot.load"), HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, Long> toCountMap(List<StatusCountBean> rows) {
        Map<String, Long> counts = new HashMap<String, Long>();
        if (rows == null) {
            return counts;
        }
        for (StatusCountBean row : rows) {
            if (row == null || row.getStatus() == null) {
                continue;
            }
            counts.put(row.getStatus(), toLong(row.getTotalCount()));
        }
        return counts;
    }

    private List<CompanyDashboardDTO> toCompanyRows(List<CompanyDashboardBean> rows) {
        List<CompanyDashboardDTO> result = new ArrayList<CompanyDashboardDTO>();
        if (rows == null) {
            return result;
        }
        for (CompanyDashboardBean row : rows) {
            if (row == null) {
                continue;
            }
            result.add(CompanyDashboardDTO.builder()
                    .companyId(row.getCompanyId())
                    .companyName(row.getCompanyName())
                    .plannerCount(toLong(row.getPlannerCount()))
                    .appliedCount(toLong(row.getAppliedCount()))
                    .shortlistedCount(toLong(row.getShortlistedCount()))
                    .interviewScheduledCount(toLong(row.getInterviewScheduledCount()))
                    .selectedCount(toLong(row.getSelectedCount()))
                    .rejectedCount(toLong(row.getRejectedCount()))
                    .cancelledCount(toLong(row.getCancelledCount()))
                    .offerAcceptedCount(toLong(row.getOfferAcceptedCount()))
                    .build());
        }
        return result;
    }

    private List<DashboardRecentApplicationDTO> toRecentRows(List<ApplicationHdrBean> rows) {
        List<DashboardRecentApplicationDTO> result = new ArrayList<DashboardRecentApplicationDTO>();
        if (rows == null || rows.isEmpty()) {
            return result;
        }

        List<Long> studentIds = new ArrayList<Long>();
        List<Long> plannerIds = new ArrayList<Long>();
        for (ApplicationHdrBean row : rows) {
            if (row == null) {
                continue;
            }
            if (row.getStudentId() != null && !studentIds.contains(row.getStudentId())) {
                studentIds.add(row.getStudentId());
            }
            if (row.getPlannerId() != null && !plannerIds.contains(row.getPlannerId())) {
                plannerIds.add(row.getPlannerId());
            }
        }

        Map<Long, StudentBean> studentsById = new HashMap<Long, StudentBean>();
        if (!studentIds.isEmpty()) {
            List<StudentBean> students = studentRepository.getStudentsByIds(studentIds);
            if (students != null) {
                for (StudentBean student : students) {
                    if (student != null && student.getStudentId() != null) {
                        studentsById.put(student.getStudentId(), student);
                    }
                }
            }
        }

        Map<Long, PlannerHdrBean> plannersById = new HashMap<Long, PlannerHdrBean>();
        if (!plannerIds.isEmpty()) {
            List<PlannerHdrBean> planners = plannerRepository.getPlannersByIds(plannerIds);
            if (planners != null) {
                for (PlannerHdrBean planner : planners) {
                    if (planner != null && planner.getId() != null) {
                        plannersById.put(planner.getId(), planner);
                    }
                }
            }
        }

        for (ApplicationHdrBean row : rows) {
            if (row == null) {
                continue;
            }
            StudentBean student = studentsById.get(row.getStudentId());
            PlannerHdrBean planner = plannersById.get(row.getPlannerId());
            result.add(DashboardRecentApplicationDTO.builder()
                    .applicationId(row.getId())
                    .studentId(row.getStudentId())
                    .studentName(student == null ? null : student.getStudentName())
                    .plannerName(planner == null ? null : planner.getPlannerName())
                    .companyName(planner == null ? null : planner.getCompanyName())
                    .applicationStatus(row.getApplicationStatus())
                    .appliedDate(row.getAppliedDate())
                    .build());
        }
        return result;
    }

    private List<DashboardUpcomingPlannerDTO> toUpcomingRows(List<PlannerHdrBean> rows) {
        List<DashboardUpcomingPlannerDTO> result = new ArrayList<DashboardUpcomingPlannerDTO>();
        if (rows == null) {
            return result;
        }
        for (PlannerHdrBean row : rows) {
            if (row == null) {
                continue;
            }
            result.add(DashboardUpcomingPlannerDTO.builder()
                    .plannerId(row.getId())
                    .plannerName(row.getPlannerName())
                    .companyName(row.getCompanyName())
                    .plannerType(row.getPlannerType())
                    .mode(row.getMode())
                    .registrationStartDate(row.getRegistrationStartDate())
                    .registrationEndDate(row.getRegistrationEndDate())
                    .build());
        }
        return result;
    }

    private long countOf(Map<String, Long> counts, String status) {
        Long value = counts.get(status);
        return value == null ? 0L : value.longValue();
    }

    private long toLong(Number value) {
        return value == null ? 0L : value.longValue();
    }
}
