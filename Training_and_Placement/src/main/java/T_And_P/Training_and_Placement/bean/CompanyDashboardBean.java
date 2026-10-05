package T_And_P.Training_and_Placement.bean;

public interface CompanyDashboardBean {

    Long getCompanyId();

    String getCompanyName();

    Long getPlannerCount();

    Long getAppliedCount();

    Long getShortlistedCount();

    Long getInterviewScheduledCount();

    Long getSelectedCount();

    Long getRejectedCount();

    Long getCancelledCount();

    Long getOfferAcceptedCount();
}