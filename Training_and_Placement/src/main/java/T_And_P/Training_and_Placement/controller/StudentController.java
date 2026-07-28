package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.StudentRequestDTO;
import T_And_P.Training_and_Placement.dto.StudentResponseDTO;
import T_And_P.Training_and_Placement.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/student-master")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/save")
    public ResponseEntity<StudentResponseDTO> save(@RequestBody StudentRequestDTO requestDTO){

        log.info("save student request received");

        StudentResponseDTO response = studentService.saveStudent( requestDTO);

        log.info("saved students successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getByIdStudent(@PathVariable("id") Long studentId){

        log.info("Get student request received for id : {}", studentId);

        StudentResponseDTO student = studentService.getById(studentId);

        return ResponseEntity.ok(student);


    }

    @GetMapping("/getAll")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents(@RequestBody StudentRequestDTO requestDTO){

        log.info("getting all students request received");

        List<StudentResponseDTO> students = studentService.getAllStudents();

        log.info("getting all students");

        return ResponseEntity.ok(students);
    }


}