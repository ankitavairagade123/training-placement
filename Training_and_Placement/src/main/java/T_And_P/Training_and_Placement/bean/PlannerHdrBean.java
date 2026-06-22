package T_And_P.Training_and_Placement.bean;

import java.time.LocalDateTime;

public interface PlannerHdrBean {

    Long getId();

    String getPlannerName();

    String getPlannerDesc();

    String getPlannerType();

    String getMode();

    String getPlannerScheduleType();

    String getStatus();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    Long getMaxStudents();
}
