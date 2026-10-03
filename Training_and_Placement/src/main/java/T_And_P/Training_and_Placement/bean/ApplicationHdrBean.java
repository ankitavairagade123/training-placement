package T_And_P.Training_and_Placement.bean;

import java.time.LocalDateTime;

public interface ApplicationHdrBean {

    Long getId();

    Long getStudentId();

    String getStudentName();

    Long getPlannerId();

    String getPlannerName();

    String getCompanyName();

    String getResumePath();

    Boolean getTermsAccepted();

    String getOfferLetterPath();

    String getJoiningLetterPath();

    LocalDateTime getAppliedDate();

    String getApplicationStatus();
}
