//package T_And_P.Training_and_Placement.service;
//
//import T_And_P.Training_and_Placement.constant.Status;
//import T_And_P.Training_and_Placement.dto.CertificationRequestDTO;
//import T_And_P.Training_and_Placement.dto.CertificationResponseDTO;
//import T_And_P.Training_and_Placement.entity.CertificationMaster;
//import T_And_P.Training_and_Placement.exception.CertificationException;
//import T_And_P.Training_and_Placement.repository.CertificationRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.security.cert.CertificateException;
//import java.util.Objects;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CertificationServiceImpl implements CertificationService {
//
//    private final CertificationRepository repository;
//
//    @Override
//    public CertificationResponseDTO save(CertificationRequestDTO requestDTO) {
//
//        log.info("certification saved request received");
//
//        validateRequest(requestDTO);
//
//        if (Objects.nonNull(requestDTO.getCertificationId())) {
//
//            log.info("update request received for certification with id : {}",
//                    requestDTO.getCertificationId());
//
//            repository.findById(requestDTO.getCertificationId())
//                    .orElseThrow(() -> new CertificationException(
//                            "Certification not found",
//                            HttpStatus.BAD_REQUEST));
//
//            repository.findDuplicateForUpdate(requestDTO.getCertificationName().trim(),
//                            requestDTO.getCertificationId())
//                    .ifPresent(data -> {
//                        throw new CertificateException(
//                                "Certification already exist",
//                                HttpStatus.BAD_REQUEST);
//                    });
//        } else {
//
//            log.info("request for create certification received");
//
//            repository.findByCertificationNameIgnoreCase(requestDTO.getCertificationName().trim())
//                    .ifPresent(data -> {
//                        throw new CertificateException(
//                                "Certification already exist",
//                                HttpStatus.BAD_REQUEST);
//                    });
//        }
//
//            CertificationMaster entity = CertificationMaster.builder()
//                    .certificationId(requestDTO.getCertificationId())
//                    .certificationName(requestDTO.getCertificationName())
//                    .status(requestDTO.getStatus())
//                    .build();
//
//            log.info("saving the certification fields to the database");
//
//            CertificationMaster savedEntity = repository.save(entity);
//
//            log.info("saving certification with id : {}", requestDTO.getCertificationId());
//
//            return CertificationResponseDTO.builder()
//                    .certificationId(savedEntity.getCertificationId())
//                    .certificationName(savedEntity.getCertificationName())
//                    .status(savedEntity.getStatus())
//                    .build();
//
//        }
//
//
//    private void validateRequest(CertificationRequestDTO requestDTO) {
//    }
//}
