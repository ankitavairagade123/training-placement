package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.DashboardSummaryDTO;
import T_And_P.Training_and_Placement.dto.PlannerDashboardDTO;
import T_And_P.Training_and_Placement.dto.StudentDashboardDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.DashboardService;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard APIs for TPO, planner-wise faculty view, and student portal.
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final MessageUtil messageUtil;

    /**
     * TPO / Faculty home dashboard with KPI cards and funnel counts.
     */
    @GetMapping
    public DashboardSummaryDTO getTpoDashboard() {
        log.info("getTpoDashboard() started");

        try {
            DashboardSummaryDTO response = dashboardService.getTpoDashboard();
            log.info("getTpoDashboard() completed");
            return response;

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.error("getTpoDashboard() failed", e);
            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Planner-wise application funnel for faculty verification.
     */
    @GetMapping("/planner/{plannerId}")
    public PlannerDashboardDTO getPlannerDashboard(
            @PathVariable Long plannerId) {

        log.info(
                "getPlannerDashboard() started for plannerId={}",
                plannerId
        );

        try {
            PlannerDashboardDTO response =
                    dashboardService.getPlannerDashboard(plannerId);

            log.info(
                    "getPlannerDashboard() completed for plannerId={}",
                    plannerId
            );

            return response;

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.error(
                    "getPlannerDashboard() failed for plannerId={}",
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
     * Student portal dashboard.
     */
    @GetMapping("/student/{studentId}")
    public StudentDashboardDTO getStudentDashboard(
            @PathVariable Long studentId) {

        log.info(
                "getStudentDashboard() started for studentId={}",
                studentId
        );

        try {
            StudentDashboardDTO response =
                    dashboardService.getStudentDashboard(studentId);

            log.info(
                    "getStudentDashboard() completed for studentId={}",
                    studentId
            );

            return response;

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.error(
                    "getStudentDashboard() failed for studentId={}",
                    studentId,
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}