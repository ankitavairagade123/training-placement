package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {

    @Query("""
       SELECT s
       FROM Student s
       WHERE LOWER(s.studentName)=LOWER(:studentName)
       AND s.studentId<>:studentId
       """)
    Optional<Student> findDuplicateForUpdate(
            @Param("studentName") String studentName,
            @Param("studentId") Long studentId);

    Optional<Student> findByStudentNameIgnoreCase(String trim);
}
