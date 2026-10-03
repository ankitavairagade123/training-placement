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
            + "pd.planner_dtl_id AS id, "
            + "em.id AS eligibilityId, "
            + "em.eligibility_type AS eligibilityType, "
            + "pd.criteria_value AS criteriaValue, "
            + "pd.criteria_rule AS criteriaRule, "
            + "pd.status AS status, "
            + "pd.mandatory AS mandatory "
            + "FROM training_and_placement_planner_dtl pd "
            + "INNER JOIN eligibility_master em "
            + "ON em.id = pd.eligibility_id "
            + "WHERE pd.planner_hdr_id = :plannerId "
            + "ORDER BY pd.planner_dtl_id",
            nativeQuery = true)
    List<PlannerDtlBean> getPlannerDetails(
            @Param("plannerId") Long plannerId);
}
