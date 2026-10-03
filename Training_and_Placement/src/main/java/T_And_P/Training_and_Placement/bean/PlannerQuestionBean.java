package T_And_P.Training_and_Placement.bean;

public interface PlannerQuestionBean {

    Long getQuestionId();

    Long getPlannerId();

    String getQuestion();

    String getFieldType();

    Boolean getMandatory();
}
