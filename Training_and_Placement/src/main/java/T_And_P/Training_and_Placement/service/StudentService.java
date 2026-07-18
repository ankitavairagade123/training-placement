//package T_And_P.Training_and_Placement.service;
//
//import T_And_P.Training_and_Placement.dto.StudentRequestDTO;
//import T_And_P.Training_and_Placement.dto.StudentResponseDTO;
//import T_And_P.Training_and_Placement.entity.CompanyMaster;
//import T_And_P.Training_and_Placement.entity.Student;
//import T_And_P.Training_and_Placement.exception.StudentException;
//import T_And_P.Training_and_Placement.repository.StudentRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.security.cert.CertificateException;
//import java.util.Objects;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class StudentService {
//
//    private final StudentRepository repository;
//
//    public StudentResponseDTO saveStudent(StudentRequestDTO requestDTO) {
//
//        log.info("save request received for student");
//
//        validateStudentRequest(requestDTO);
//
//        if(Objects.nonNull(requestDTO.getStudentId())){
//
//            log.info("request received for update with id :{}" , requestDTO.getStudentId());
//
//            repository.findById(requestDTO.getStudentId())
//                    .orElseThrow(() -> new StudentException(
//                            "Student not found",
//                            HttpStatus.BAD_REQUEST
//                    ));
//
//            repository.findDuplicateForUpdate(requestDTO.getStudentName().trim(),
//                            requestDTO.getStudentId())
//                    .ifPresent(data -> {
//                        throw new StudentException(
//                                "Student already exist",
//                                HttpStatus.BAD_REQUEST);
//                    });
//        }else {
//
//            log.info("create request received for student");
//
//            repository.findByStudentNameIgnoreCase(requestDTO.getStudentName().trim())
//                    .ifPresent(data -> {
//                        throw new StudentException(
//                                "Student already exist",
//                                HttpStatus.BAD_REQUEST
//                        );
//                    });
//        }
//
//            Student studentEntity = Student.builder()
//                    .studentId(requestDTO.getStudentId())
//                    .studentName(requestDTO.getStudentName().trim()).build();
//
//                    log.info("student is getting saved");
//
//                    Student savedStudent = repository.save(studentEntity);
//
//                    log.info("student saved successfully for id: {}" , savedStudent.getStudentId());
//
//                    return  StudentResponseDTO.builder()
//                            .studentId(savedStudent.getStudentId())
//                            .studentName(savedStudent.getStudentName()).build();
//    }
//
//    public StudentResponseDTO getById(StudentRequestDTO requestDTO){
//
//        Student student = repository.getById(studentId)
//    }
//
//    private void validateStudentRequest(StudentRequestDTO requestDTO) {
//
//        if(requestDTO == null){
//            throw new StudentException(
//                    "Student cannot be null",
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//
//        if(!StringUtils.hasText(requestDTO.getStudentName())){
//            throw new StudentException(
//                    "Student name is mandatory",
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//        if(requestDTO.getStudentName().trim().length() > 50){
//            throw new StudentException(
//                "Student name should not exceed 50 characters",
//                HttpStatus.BAD_REQUEST
//        );
//        }
//    }
//}
