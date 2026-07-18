//package T_And_P.Training_and_Placement.service;
//
//import T_And_P.Training_and_Placement.constant.Status;
//import T_And_P.Training_and_Placement.dto.CertificationRequestDTO;
//import T_And_P.Training_and_Placement.dto.CertificationResponseDTO;
//import T_And_P.Training_and_Placement.entity.CertificationMaster;
//import T_And_P.Training_and_Placement.repository.CertificationRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface CertificationService {
//
//    List<CertificationResponseDTO> getAll();
//
//    Optional<CertificationResponseDTO> getByCertificationId(Long certificationId );
//
//    Optional<CertificationResponseDTO> getByCertificationName(String certificationName);
//
//    List<CertificationResponseDTO> getActiveCertifications(Status status);
//
//    CertificationResponseDTO save(CertificationRequestDTO requestDTO);
//
//    void delete(Long certificationId);
//
//
//}
