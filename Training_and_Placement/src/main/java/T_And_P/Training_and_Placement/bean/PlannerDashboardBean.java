package T_And_P.Training_and_Placement.bean;

public interface PlannerDashboardBean {

    Long getPlannerId();

    String getPlannerName();

    String getCompanyName();

    String getPlannerStatus();

    String getPlannerType();

    Long getTotalApplications();

    Long getAppliedCount();

    Long getShortlistedCount();

    Long getInterviewScheduledCount();

    Long getSelectedCount();

    Long getRejectedCount();

    Long getCancelledCount();

    Long getOfferAcceptedCount();
}