package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(value = "select student_id as studentId, "
            + "student_name as studentName, "
            + "email as email, "
            + "ssc_percentage as sscPercentage, "
            + "hsc_percentage as hscPercentage, "
            + "ug_cgpa as ugCgpa, "
            + "attendance as attendance, "
            + "active_backlogs as activeBacklogs, "
            + "branch as branch, "
            + "semester as semester, "
            + "passing_year as passingYear, "
            + "resume_path as resumePath "
            + "from student "
            + "order by student_id desc", nativeQuery = true)
    List<StudentBean> getAllStudents();

    @Query(value = "select student_id as studentId, "
            + "student_name as studentName, "
            + "email as email, "
            + "ssc_percentage as sscPercentage, "
            + "hsc_percentage as hscPercentage, "
            + "ug_cgpa as ugCgpa, "
            + "attendance as attendance, "
            + "active_backlogs as activeBacklogs, "
            + "branch as branch, "
            + "semester as semester, "
            + "passing_year as passingYear, "
            + "resume_path as resumePath "
            + "from student "
            + "where student_id = :studentId", nativeQuery = true)
    Optional<StudentBean> getStudentById(@Param("studentId") Long studentId);

    @Query(value = "select student_id from student where student_id = :studentId", nativeQuery = true)
    Optional<Long> existsStudentById(@Param("studentId") Long studentId);

    @Query(value = "select student_id from student "
            + "where lower(student_name) = lower(:studentName) "
            + "and student_id <> :studentId", nativeQuery = true)
    Optional<Long> findDuplicateForUpdate(
            @Param("studentName") String studentName,
            @Param("studentId") Long studentId);

    @Query(value = "select student_id from student "
            + "where lower(student_name) = lower(:studentName)", nativeQuery = true)
    Optional<Long> findByStudentNameIgnoreCase(@Param("studentName") String studentName);

    @Query(value = "select count(*) from student", nativeQuery = true)
    Long countAllStudents();
}
