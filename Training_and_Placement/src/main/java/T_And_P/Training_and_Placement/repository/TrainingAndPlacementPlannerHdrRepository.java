package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.PlannerHdrBean;
import T_And_P.Training_and_Placement.bean.StatusCountBean;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingAndPlacementPlannerHdrRepository extends JpaRepository<TrainingAndPlacementPlannerHdr, Long> {

    @Query(value = "SELECT "
            + "tph.id AS id, "
            + "tph.planner_name AS plannerName, "
            + "tph.planner_description AS plannerDesc, "
            + "tph.planner_type AS plannerType, "
            + "tph.mode AS mode, "
            + "tph.planner_schedule_type AS plannerScheduleType, "
            + "tph.status AS status, "
            + "tph.start_time AS startTime, "
            + "tph.end_time AS endTime, "
            + "tph.registration_start_date AS registrationStartDate, "
            + "tph.registration_end_date AS registrationEndDate, "
            + "tph.max_student_count AS maxStudents, "
            + "tph.venue AS venue, "
            + "tph.website AS website, "
            + "tph.meeting_link AS meetingLink, "
            + "tph.remarks AS remarks, "
            + "tph.attachment_path AS attachmentPath, "
            + "tph.published_by AS publishedBy, "
            + "tph.published_at AS publishedAt, "
            + "cm.id AS companyId, "
            + "cm.company_name AS companyName, "
            + "cm.company_code AS companyCode "
            + "FROM training_and_placement_planner_hdr tph "
            + "INNER JOIN company_master cm ON cm.id = tph.company_id "
            + "WHERE tph.status = 'ACTIVE' "
            + "AND (tph.registration_start_date IS NULL OR NOW() >= tph.registration_start_date) "
            + "AND (tph.registration_end_date IS NULL OR NOW() <= tph.registration_end_date) "
            + "ORDER BY tph.start_time ASC", nativeQuery = true)
    List<PlannerHdrBean> getActivePlanners();

    @Query(value = "SELECT "
            + "ph.id AS id, "
            + "ph.planner_name AS plannerName, "
            + "ph.planner_description AS plannerDesc, "
            + "ph.planner_type AS plannerType, "
            + "ph.mode AS mode, "
            + "ph.planner_schedule_type AS plannerScheduleType, "
            + "ph.status AS status, "
            + "ph.max_student_count AS maxStudents, "
            + "ph.start_time AS startTime, "
            + "ph.end_time AS endTime, "
            + "ph.registration_start_date AS registrationStartDate, "
            + "ph.registration_end_date AS registrationEndDate, "
            + "ph.venue AS venue, "
            + "ph.website AS website, "
            + "ph.meeting_link AS meetingLink, "
            + "ph.remarks AS remarks, "
            + "ph.attachment_path AS attachmentPath, "
            + "ph.published_by AS publishedBy, "
            + "ph.published_at AS publishedAt, "
            + "cm.id AS companyId, "
            + "cm.company_name AS companyName, "
            + "cm.company_code AS companyCode "
            + "FROM training_and_placement_planner_hdr ph "
            + "INNER JOIN company_master cm ON cm.id = ph.company_id "
            + "WHERE ph.id = :id", nativeQuery = true)
    Optional<PlannerHdrBean> getPlannerById(@Param("id") Long id);

    @Query(value = "SELECT "
            + "ph.id AS id, "
            + "ph.planner_name AS plannerName, "
            + "ph.planner_description AS plannerDesc, "
            + "ph.planner_type AS plannerType, "
            + "ph.mode AS mode, "
            + "ph.planner_schedule_type AS plannerScheduleType, "
            + "ph.status AS status, "
            + "ph.max_student_count AS maxStudents, "
            + "ph.start_time AS startTime, "
            + "ph.end_time AS endTime, "
            + "ph.registration_start_date AS registrationStartDate, "
            + "ph.registration_end_date AS registrationEndDate, "
            + "ph.venue AS venue, "
            + "ph.website AS website, "
            + "ph.meeting_link AS meetingLink, "
            + "ph.remarks AS remarks, "
            + "ph.attachment_path AS attachmentPath, "
            + "ph.published_by AS publishedBy, "
            + "ph.published_at AS publishedAt, "
            + "cm.id AS companyId, "
            + "cm.company_name AS companyName, "
            + "cm.company_code AS companyCode "
            + "FROM training_and_placement_planner_hdr ph "
            + "INNER JOIN company_master cm ON cm.id = ph.company_id "
            + "WHERE ph.id IN (:ids)", nativeQuery = true)
    List<PlannerHdrBean> getPlannersByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT "
            + "tph.id AS id, "
            + "tph.planner_name AS plannerName, "
            + "tph.planner_description AS plannerDesc, "
            + "tph.planner_type AS plannerType, "
            + "tph.mode AS mode, "
            + "tph.planner_schedule_type AS plannerScheduleType, "
            + "tph.status AS status, "
            + "tph.max_student_count AS maxStudents, "
            + "tph.start_time AS startTime, "
            + "tph.end_time AS endTime, "
            + "tph.registration_start_date AS registrationStartDate, "
            + "tph.registration_end_date AS registrationEndDate, "
            + "tph.venue AS venue, "
            + "tph.website AS website, "
            + "tph.meeting_link AS meetingLink, "
            + "tph.remarks AS remarks, "
            + "tph.attachment_path AS attachmentPath, "
            + "tph.published_by AS publishedBy, "
            + "tph.published_at AS publishedAt, "
            + "cm.id AS companyId, "
            + "cm.company_name AS companyName, "
            + "cm.company_code AS companyCode "
            + "FROM training_and_placement_planner_hdr tph "
            + "INNER JOIN company_master cm ON cm.id = tph.company_id "
            + "ORDER BY tph.id DESC", nativeQuery = true)
    List<PlannerHdrBean> getAllPlanners();

    @Query(value = "select count(*) from training_and_placement_planner_hdr", nativeQuery = true)
    Long countAllPlanners();

    @Query(value = "select status as status, count(*) as totalCount "
            + "from training_and_placement_planner_hdr "
            + "group by status", nativeQuery = true)
    List<StatusCountBean> countPlannersByStatus();
}
