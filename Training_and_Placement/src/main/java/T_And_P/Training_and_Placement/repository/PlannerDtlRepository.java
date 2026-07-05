package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.PlannerDtlBean;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannerDtlRepository extends JpaRepository<TrainingAndPlacementPlannerDtl, Long> {

    @Query(value = """
            SELECT
                pd.id AS id,
                pd.criteria_rule AS criteriaRule,
                pd.status AS status
            FROM training_and_placement_planner_dtl pd
            WHERE pd.planner_hdr_id = :plannerId
            ORDER BY pd.id
            """,
            nativeQuery = true)
    List<PlannerDtlBean> getPlannerDetails(
            @Param("plannerId") Long plannerId);
}