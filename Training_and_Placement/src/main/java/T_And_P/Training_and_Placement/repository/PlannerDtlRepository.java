package T_And_P.Training_and_Placement.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import T_And_P.Training_and_Placement.bean.PlannerDtlBean;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerDtl;

@Repository
public interface PlannerDtlRepository
        extends JpaRepository<TrainingAndPlacementPlannerDtl, Long> {

    @Query(value = "SELECT "
            + "planner_dtl_id AS id, "
            + "planner_hdr_id AS plannerHdrId, "
            + "eligibility_id AS eligibilityId, "
            + "criteria_value AS criteriaValue, "
            + "criteria_rule AS criteriaRule, "
            + "status AS status, "
            + "mandatory AS mandatory "
            + "FROM training_and_placement_planner_dtl "
            + "WHERE planner_hdr_id = :plannerId "
            + "ORDER BY planner_dtl_id",
            nativeQuery = true)
    List<PlannerDtlBean> getPlannerDetails(
            @Param("plannerId") Long plannerId);

    @Query(value = "SELECT "
            + "planner_dtl_id AS id, "
            + "planner_hdr_id AS plannerHdrId, "
            + "eligibility_id AS eligibilityId, "
            + "criteria_value AS criteriaValue, "
            + "criteria_rule AS criteriaRule, "
            + "status AS status, "
            + "mandatory AS mandatory "
            + "FROM training_and_placement_planner_dtl "
            + "WHERE planner_hdr_id IN (:plannerIds) "
            + "ORDER BY planner_hdr_id, planner_dtl_id",
            nativeQuery = true)
    List<PlannerDtlBean> getPlannerDetailsByPlannerIds(
            @Param("plannerIds") List<Long> plannerIds);
}
