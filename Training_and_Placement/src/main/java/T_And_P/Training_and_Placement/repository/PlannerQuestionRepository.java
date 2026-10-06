package T_And_P.Training_and_Placement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.PlannerQuestionBean;
import T_And_P.Training_and_Placement.bean.QuestionOptionBean;
import T_And_P.Training_and_Placement.entity.PlannerQuestion;

public interface PlannerQuestionRepository
        extends JpaRepository<PlannerQuestion, Long> {

    @Query(value = "SELECT question_id AS questionId, "
            + "planner_id AS plannerId, "
            + "question AS question, "
            + "field_type AS fieldType, "
            + "mandatory AS mandatory "
            + "FROM planner_question "
            + "WHERE planner_id = :plannerId "
            + "ORDER BY question_id", nativeQuery = true)
    List<PlannerQuestionBean> getQuestionsByPlannerId(
            @Param("plannerId") Long plannerId);

    @Query(value = "SELECT qo.option_id AS optionId, "
            + "qo.question_id AS questionId, "
            + "qo.option_text AS optionText, "
            + "qo.display_order AS displayOrder "
            + "FROM question_option qo "
            + "INNER JOIN planner_question pq "
            + "ON pq.question_id = qo.question_id "
            + "WHERE pq.planner_id = :plannerId "
            + "ORDER BY qo.question_id, qo.display_order, qo.option_id",
            nativeQuery = true)
    List<QuestionOptionBean> getOptionsByPlannerId(
            @Param("plannerId") Long plannerId);

    @Query(value = "SELECT question_id AS questionId, "
            + "planner_id AS plannerId, "
            + "question AS question, "
            + "field_type AS fieldType, "
            + "mandatory AS mandatory "
            + "FROM planner_question "
            + "WHERE planner_id IN (:plannerIds) "
            + "ORDER BY planner_id, question_id", nativeQuery = true)
    List<PlannerQuestionBean> getQuestionsByPlannerIds(
            @Param("plannerIds") List<Long> plannerIds);

    @Query(value = "SELECT qo.option_id AS optionId, "
            + "qo.question_id AS questionId, "
            + "qo.option_text AS optionText, "
            + "qo.display_order AS displayOrder "
            + "FROM question_option qo "
            + "INNER JOIN planner_question pq "
            + "ON pq.question_id = qo.question_id "
            + "WHERE pq.planner_id IN (:plannerIds) "
            + "ORDER BY qo.question_id, qo.display_order, qo.option_id",
            nativeQuery = true)
    List<QuestionOptionBean> getOptionsByPlannerIds(
            @Param("plannerIds") List<Long> plannerIds);
}