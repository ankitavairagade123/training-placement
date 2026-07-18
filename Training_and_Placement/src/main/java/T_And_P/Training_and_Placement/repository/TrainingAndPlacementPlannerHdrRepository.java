package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.PlannerDtlBean;
import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
            tph.max_student_count AS maxStudents,
            tph.venue AS venue,
            tph.website AS website,
        FROM training_and_placement_planner_hdr tph
        WHERE tph.status = 'ACTIVE'
          AND (
                (
                    tph.planner_schedule_type = 'RANGE'
                    AND NOW() BETWEEN tph.start_time AND tph.end_time
                )
                OR
                (
                    tph.planner_schedule_type <> 'RANGE'
                    AND tph.start_time >= CURDATE()
                    AND tph.start_time < DATE_ADD(CURDATE(), INTERVAL 1 DAY)
                )
              )
        ORDER BY tph.start_time ASC
        """,
            nativeQuery = true)
    List<PlannerHdrBean> getActivePlanners();


    @Query(value = """
        SELECT
            ph.id id,
            ph.planner_name plannerName,
            ph.planner_description plannerDesc,
            ph.planner_type plannerType,
            ph.mode mode,
            ph.planner_schedule_type plannerScheduleType,
            ph.status status,
            ph.max_student_count maxStudents,
            ph.start_time startTime,
            ph.end_time endTime,
            ph.venue AS venue,
            ph.website AS website,
            cm.id AS companyId,
            cm.companyName companyName
        FROM training_and_placement_planner_hdr ph
        INNER JOIN company_master cm
            ON cm.id = ph.company_id
        WHERE ph.id = :id
        """,
            nativeQuery = true)
    Optional<PlannerHdrBean> getPlannerById(
            @Param("id") Long id);


    @Query(value = """
        SELECT
            pd.id id,
            pd.criteria_rule criteriaRule,
            pd.status status
        FROM training_and_placement_planner_dtl pd
        WHERE pd.planner_hdr_id = :plannerId
        """,
            nativeQuery = true)
    List<PlannerDtlBean> getPlannerDetails(
            @Param("plannerId") Long plannerId);

    /**
     * Fetch all planners with company details.
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
                tph.max_student_count AS maxStudents,
                tph.start_time AS startTime,
                tph.end_time AS endTime,
                tph.venue AS venue,
                tph.website AS website,
                cm.id AS companyId,
                cm.companyName AS companyName
            FROM training_and_placement_planner_hdr tph
            INNER JOIN company_master cm
                    ON cm.id = tph.company_id
            ORDER BY tph.id DESC
            """,
            nativeQuery = true)
    List<PlannerHdrBean> getAllPlanners();

}
