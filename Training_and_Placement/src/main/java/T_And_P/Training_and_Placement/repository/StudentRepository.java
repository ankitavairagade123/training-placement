//package T_And_P.Training_and_Placement.repository;
//
//import T_And_P.Training_and_Placement.entity.Student;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface StudentRepository extends JpaRepository<Student,Long> {
//
//    Optional<Student> findDuplicateForUpdate( String studentName,Long studentId);
//
//    Optional<Object> findByStudentNameIgnoreCase(String trim);
//}
