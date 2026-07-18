//package T_And_P.Training_and_Placement.controller;
//
//import T_And_P.Training_and_Placement.dto.StudentRequestDTO;
//import T_And_P.Training_and_Placement.dto.StudentResponseDTO;
//import T_And_P.Training_and_Placement.service.StudentService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.Mapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequestMapping("/student-master")
//public class StudentController {
//
//    private final StudentService studentService;
//
//    public StudentController(StudentService studentService) {
//        this.studentService = studentService;
//    }
//
//    @PostMapping("/save")
//    public StudentResponseDTO save(StudentRequestDTO requestDTO){
//
//        log.info("save student request received");
//
//        StudentResponseDTO response = studentService.saveStudent(requestDTO);
//
//        log.info("saved company successfully");
//
//        ResponseEntity.ok(response);
//    }
//
//
//}