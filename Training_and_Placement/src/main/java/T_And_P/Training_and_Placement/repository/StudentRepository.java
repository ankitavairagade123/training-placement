package T_And_P.Training_and_Placement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "SELECT student_id AS studentId, "
            + "student_name AS studentName, "
            + "email AS email, "
            + "ssc_percentage AS sscPercentage, "
            + "hsc_percentage AS hscPercentage, "
            + "ug_cgpa AS ugCgpa, "
            + "attendance AS attendance, "
            + "active_backlogs AS activeBacklogs, "
            + "branch AS branch, "
            + "semester AS semester, "
            + "passing_year AS passingYear, "
            + "resume_path AS resumePath "
            + "FROM student "
            + "ORDER BY student_id DESC", nativeQuery = true)
    List<StudentBean> getAllStudents();

    @Query(value = "SELECT student_id AS studentId, "
            + "student_name AS studentName, "
            + "email AS email, "
            + "ssc_percentage AS sscPercentage, "
            + "hsc_percentage AS hscPercentage, "
            + "ug_cgpa AS ugCgpa, "
            + "attendance AS attendance, "
            + "active_backlogs AS activeBacklogs, "
            + "branch AS branch, "
            + "semester AS semester, "
            + "passing_year AS passingYear, "
            + "resume_path AS resumePath "
            + "FROM student "
            + "WHERE student_id = :studentId", nativeQuery = true)
    Optional<StudentBean> getStudentById(
            @Param("studentId") Long studentId);

    @Query(value = "SELECT student_id "
            + "FROM student "
            + "WHERE student_id = :studentId", nativeQuery = true)
    Optional<Long> existsStudentById(
            @Param("studentId") Long studentId);

    @Query(value = "SELECT student_id "
            + "FROM student "
            + "WHERE LOWER(student_name) = LOWER(:studentName) "
            + "AND student_id <> :studentId", nativeQuery = true)
    Optional<Long> findDuplicateForUpdate(
            @Param("studentName") String studentName,
            @Param("studentId") Long studentId);

    @Query(value = "SELECT student_id "
            + "FROM student "
            + "WHERE LOWER(student_name) = LOWER(:studentName)",
            nativeQuery = true)
    Optional<Long> findByStudentNameIgnoreCase(
            @Param("studentName") String studentName);
}
