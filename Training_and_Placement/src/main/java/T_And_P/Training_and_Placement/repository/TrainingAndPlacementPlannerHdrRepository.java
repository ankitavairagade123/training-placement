package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainingAndPlacementPlannerHdrRepository extends JpaRepository<TrainingAndPlacementPlannerHdr,Long> {

    /**
     * Fetch all active planners
     * Current date should fall between planner start and end time
     */
    @Query(value = """
            SELECT
                tph.id AS id,
                tph.planner_name AS plannerName,
                tph.planner_description AS plannerDesc,
                tph.planner_type AS plannerType,
                tph.mode AS mode,
                tph.planner_schedule_type AS plannerScheduleType,
                tph.status AS status,
                tph.start_time AS startTime,
                tph.end_time AS endTime,
                tph.max_student_count AS maxStudents
            FROM training_and_placement_planner_hdr tph
            WHERE tph.status = 'ACTIVE'
              AND GETDATE() BETWEEN tph.start_time AND tph.end_time
            ORDER BY tph.start_time ASC
            """,
            nativeQuery = true)
    List<PlannerHdrBean> getActivePlanners();
}
