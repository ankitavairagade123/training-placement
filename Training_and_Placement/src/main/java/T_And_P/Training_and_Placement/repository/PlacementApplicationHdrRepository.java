package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.ApplicationHdrBean;
import T_And_P.Training_and_Placement.bean.CompanyDashboardBean;
import T_And_P.Training_and_Placement.bean.PlannerDashboardBean;
import T_And_P.Training_and_Placement.bean.StatusCountBean;
import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlacementApplicationHdrRepository
        extends JpaRepository<PlacementApplicationHdr, Long> {

    @Query(value = "select application_id as id, "
            + "student_id as studentId, "
            + "planner_id as plannerId, "
            + "resume_path as resumePath, "
            + "terms_accepted as termsAccepted, "
            + "offer_letter_path as offerLetterPath, "
            + "joining_letter_path as joiningLetterPath, "
            + "applied_date as appliedDate, "
            + "application_status as applicationStatus "
            + "from placement_application_hdr "
            + "where application_id = :applicationId", nativeQuery = true)
    Optional<ApplicationHdrBean> getApplicationById(@Param("applicationId") Long applicationId);

    @Query(value = "select application_id as id, "
            + "student_id as studentId, "
            + "planner_id as plannerId, "
            + "resume_path as resumePath, "
            + "terms_accepted as termsAccepted, "
            + "offer_letter_path as offerLetterPath, "
            + "joining_letter_path as joiningLetterPath, "
            + "applied_date as appliedDate, "
            + "application_status as applicationStatus "
            + "from placement_application_hdr "
            + "where student_id = :studentId "
            + "order by application_id desc", nativeQuery = true)
    List<ApplicationHdrBean> getApplicationsByStudentId(@Param("studentId") Long studentId);

    @Query(value = "select application_id as id, "
            + "student_id as studentId, "
            + "planner_id as plannerId, "
            + "resume_path as resumePath, "
            + "terms_accepted as termsAccepted, "
            + "offer_letter_path as offerLetterPath, "
            + "joining_letter_path as joiningLetterPath, "
            + "applied_date as appliedDate, "
            + "application_status as applicationStatus "
            + "from placement_application_hdr "
            + "where planner_id = :plannerId "
            + "order by application_id desc", nativeQuery = true)
    List<ApplicationHdrBean> getApplicationsByPlannerId(@Param("plannerId") Long plannerId);

    @Query(value = "select application_id from placement_application_hdr where application_id = :applicationId",
            nativeQuery = true)
    Optional<Long> existsApplicationById(@Param("applicationId") Long applicationId);

    @Query(value = "select application_id from placement_application_hdr "
            + "where planner_id = :plannerId and student_id = :studentId", nativeQuery = true)
    Optional<Long> existsByPlannerHdrIdAndStudentStudentId(
            @Param("plannerId") Long plannerId,
            @Param("studentId") Long studentId);

    @Query(value = "select count(*) from placement_application_hdr", nativeQuery = true)
    Long countAllApplications();

    @Query(value = "select application_status as status, count(*) as totalCount "
            + "from placement_application_hdr "
            + "group by application_status", nativeQuery = true)
    List<StatusCountBean> countApplicationsByStatus();

    @Query(value = "select application_status as status, count(*) as totalCount "
            + "from placement_application_hdr "
            + "where student_id = :studentId "
            + "group by application_status", nativeQuery = true)
    List<StatusCountBean> countApplicationsByStudentId(@Param("studentId") Long studentId);

    @Query(value = "select application_id as id, "
            + "student_id as studentId, "
            + "planner_id as plannerId, "
            + "resume_path as resumePath, "
            + "terms_accepted as termsAccepted, "
            + "offer_letter_path as offerLetterPath, "
            + "joining_letter_path as joiningLetterPath, "
            + "applied_date as appliedDate, "
            + "application_status as applicationStatus "
            + "from placement_application_hdr "
            + "order by application_id desc "
            + "limit 10", nativeQuery = true)
    List<ApplicationHdrBean> getRecentApplications();

    @Query(value = "select cm.id as companyId, "
            + "cm.company_name as companyName, "
            + "count(distinct tph.id) as plannerCount, "
            + "count(pah.application_id) as appliedCount, "
            + "cast(sum(case when pah.application_status = 'SHORTLISTED' then 1 else 0 end) as signed) as shortlistedCount, "
            + "cast(sum(case when pah.application_status = 'INTERVIEW_SCHEDULED' then 1 else 0 end) as signed) as interviewScheduledCount, "
            + "cast(sum(case when pah.application_status = 'SELECTED' then 1 else 0 end) as signed) as selectedCount, "
            + "cast(sum(case when pah.application_status = 'REJECTED' then 1 else 0 end) as signed) as rejectedCount, "
            + "cast(sum(case when pah.application_status = 'CANCELLED' then 1 else 0 end) as signed) as cancelledCount, "
            + "cast(sum(case when pah.application_status = 'OFFER_ACCEPTED' then 1 else 0 end) as signed) as offerAcceptedCount "
            + "from company_master cm "
            + "left join training_and_placement_planner_hdr tph on tph.company_id = cm.id "
            + "left join placement_application_hdr pah on pah.planner_id = tph.id "
            + "group by cm.id, cm.company_name "
            + "order by count(pah.application_id) desc", nativeQuery = true)
    List<CompanyDashboardBean> getCompanyWiseDashboard();

    @Query(value = "select tph.id as plannerId, "
            + "tph.planner_name as plannerName, "
            + "cm.company_name as companyName, "
            + "tph.status as plannerStatus, "
            + "tph.planner_type as plannerType, "
            + "count(pah.application_id) as totalApplications, "
            + "cast(sum(case when pah.application_status = 'APPLIED' then 1 else 0 end) as signed) as appliedCount, "
            + "cast(sum(case when pah.application_status = 'SHORTLISTED' then 1 else 0 end) as signed) as shortlistedCount, "
            + "cast(sum(case when pah.application_status = 'INTERVIEW_SCHEDULED' then 1 else 0 end) as signed) as interviewScheduledCount, "
            + "cast(sum(case when pah.application_status = 'SELECTED' then 1 else 0 end) as signed) as selectedCount, "
            + "cast(sum(case when pah.application_status = 'REJECTED' then 1 else 0 end) as signed) as rejectedCount, "
            + "cast(sum(case when pah.application_status = 'CANCELLED' then 1 else 0 end) as signed) as cancelledCount, "
            + "cast(sum(case when pah.application_status = 'OFFER_ACCEPTED' then 1 else 0 end) as signed) as offerAcceptedCount "
            + "from training_and_placement_planner_hdr tph "
            + "inner join company_master cm on cm.id = tph.company_id "
            + "left join placement_application_hdr pah on pah.planner_id = tph.id "
            + "where tph.id = :plannerId "
            + "group by tph.id, tph.planner_name, cm.company_name, tph.status, tph.planner_type", nativeQuery = true)
    Optional<PlannerDashboardBean> getPlannerDashboard(@Param("plannerId") Long plannerId);
}
