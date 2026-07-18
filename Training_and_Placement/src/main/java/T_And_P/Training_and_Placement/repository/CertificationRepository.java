//package T_And_P.Training_and_Placement.repository;
//
//import T_And_P.Training_and_Placement.constant.Status;
//import T_And_P.Training_and_Placement.dto.CertificationResponseDTO;
//import T_And_P.Training_and_Placement.entity.CertificationMaster;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface CertificationRepository extends JpaRepository<CertificationMaster,Long> {
//
//    List<CertificationMaster> findByStatus(Status status);
//
//    Optional<CertificationMaster> findByCertificationNameIgnoreCase(String certificationName);
//
//    Optional<CertificationMaster> findDuplicateForUpdate(String certificationName,Long certificationId);
//}
