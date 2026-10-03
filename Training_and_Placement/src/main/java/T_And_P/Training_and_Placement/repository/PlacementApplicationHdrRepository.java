package T_And_P.Training_and_Placement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.ApplicationHdrBean;
import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;

public interface PlacementApplicationHdrRepository
        extends JpaRepository<PlacementApplicationHdr, Long> {

    @Query(value = "SELECT pah.application_id AS id, "
            + "s.student_id AS studentId, "
            + "s.student_name AS studentName, "
            + "tph.id AS plannerId, "
            + "tph.planner_name AS plannerName, "
            + "cm.company_name AS companyName, "
            + "pah.resume_path AS resumePath, "
            + "pah.terms_accepted AS termsAccepted, "
            + "pah.offer_letter_path AS offerLetterPath, "
            + "pah.joining_letter_path AS joiningLetterPath, "
            + "pah.applied_date AS appliedDate, "
            + "pah.application_status AS applicationStatus "
            + "FROM placement_application_hdr pah "
            + "INNER JOIN student s "
            + "ON s.student_id = pah.student_id "
            + "INNER JOIN training_and_placement_planner_hdr tph "
            + "ON tph.id = pah.planner_id "
            + "INNER JOIN company_master cm "
            + "ON cm.id = tph.company_id "
            + "WHERE pah.application_id = :applicationId",
            nativeQuery = true)
    Optional<ApplicationHdrBean> getApplicationById(
            @Param("applicationId") Long applicationId);

    @Query(value = "SELECT pah.application_id AS id, "
            + "s.student_id AS studentId, "
            + "s.student_name AS studentName, "
            + "tph.id AS plannerId, "
            + "tph.planner_name AS plannerName, "
            + "cm.company_name AS companyName, "
            + "pah.resume_path AS resumePath, "
            + "pah.terms_accepted AS termsAccepted, "
            + "pah.offer_letter_path AS offerLetterPath, "
            + "pah.joining_letter_path AS joiningLetterPath, "
            + "pah.applied_date AS appliedDate, "
            + "pah.application_status AS applicationStatus "
            + "FROM placement_application_hdr pah "
            + "INNER JOIN student s "
            + "ON s.student_id = pah.student_id "
            + "INNER JOIN training_and_placement_planner_hdr tph "
            + "ON tph.id = pah.planner_id "
            + "INNER JOIN company_master cm "
            + "ON cm.id = tph.company_id "
            + "WHERE pah.student_id = :studentId "
            + "ORDER BY pah.application_id DESC",
            nativeQuery = true)
    List<ApplicationHdrBean> getApplicationsByStudentId(
            @Param("studentId") Long studentId);

    @Query(value = "SELECT pah.application_id AS id, "
            + "s.student_id AS studentId, "
            + "s.student_name AS studentName, "
            + "tph.id AS plannerId, "
            + "tph.planner_name AS plannerName, "
            + "cm.company_name AS companyName, "
            + "pah.resume_path AS resumePath, "
            + "pah.terms_accepted AS termsAccepted, "
            + "pah.offer_letter_path AS offerLetterPath, "
            + "pah.joining_letter_path AS joiningLetterPath, "
            + "pah.applied_date AS appliedDate, "
            + "pah.application_status AS applicationStatus "
            + "FROM placement_application_hdr pah "
            + "INNER JOIN student s "
            + "ON s.student_id = pah.student_id "
            + "INNER JOIN training_and_placement_planner_hdr tph "
            + "ON tph.id = pah.planner_id "
            + "INNER JOIN company_master cm "
            + "ON cm.id = tph.company_id "
            + "WHERE pah.planner_id = :plannerId "
            + "ORDER BY pah.application_id DESC",
            nativeQuery = true)
    List<ApplicationHdrBean> getApplicationsByPlannerId(
            @Param("plannerId") Long plannerId);

    @Query(value = "SELECT application_id "
            + "FROM placement_application_hdr "
            + "WHERE application_id = :applicationId",
            nativeQuery = true)
    Optional<Long> existsApplicationById(
            @Param("applicationId") Long applicationId);

    @Query(value = "SELECT application_id "
            + "FROM placement_application_hdr "
            + "WHERE planner_id = :plannerId "
            + "AND student_id = :studentId",
            nativeQuery = true)
    Optional<Long> existsByPlannerHdrIdAndStudentStudentId(
            @Param("plannerId") Long plannerId,
            @Param("studentId") Long studentId);
}