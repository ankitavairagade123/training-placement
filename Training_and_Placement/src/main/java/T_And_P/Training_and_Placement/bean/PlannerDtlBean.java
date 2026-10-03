package T_And_P.Training_and_Placement.bean;

public interface PlannerDtlBean {

    Long getId();

    Long getEligibilityId();

    String getEligibilityType();

    String getCriteriaValue();

    String getCriteriaRule();

    String getStatus();

    Boolean getMandatory();
}
