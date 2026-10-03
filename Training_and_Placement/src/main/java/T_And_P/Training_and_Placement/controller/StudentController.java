package T_And_P.Training_and_Placement.controller;

import T_And_P.Training_and_Placement.dto.StudentRequestDTO;
import T_And_P.Training_and_Placement.dto.StudentResponseDTO;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.service.StudentService;
import T_And_P.Training_and_Placement.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST APIs for student master.
 * Academic fields stored here are used later for planner eligibility validation.
 */
@Slf4j
@RestController
@RequestMapping("/student-master")
public class StudentController {

    private final StudentService studentService;
    private final MessageUtil messageUtil;

    public StudentController(StudentService studentService, MessageUtil messageUtil) {
        log.info("StudentController initialized");
        this.studentService = studentService;
        this.messageUtil = messageUtil;
    }

    /**
     * Creates or updates a student profile including academic eligibility fields.
     */
    @PostMapping("/save")
    public ResponseEntity<StudentResponseDTO> save(@RequestBody StudentRequestDTO requestDTO) {
        log.info(
                "save() started for studentId={}, studentName={}",
                requestDTO == null ? null : requestDTO.getStudentId(),
                requestDTO == null ? null : requestDTO.getStudentName()
        );

        try {
            StudentResponseDTO response = studentService.saveStudent(requestDTO);

            log.info("save() completed for studentId={}", response.getStudentId());

            return ResponseEntity.ok(response);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("save() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Loads one student by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getByIdStudent(
            @PathVariable("id") Long studentId
    ) {
        log.info("getByIdStudent() started for studentId={}", studentId);

        try {
            StudentResponseDTO student = studentService.getById(studentId);

            log.info("getByIdStudent() completed for studentId={}", studentId);

            return ResponseEntity.ok(student);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("getByIdStudent() failed for studentId={}", studentId, e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Returns all students.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        log.info("getAllStudents() started");

        try {
            List<StudentResponseDTO> students = studentService.getAllStudents();

            log.info("getAllStudents() completed, count={}", students.size());

            return ResponseEntity.ok(students);

        } catch (PlacementApplicationException e) {
            throw e;

        } catch (Exception e) {
            log.info("getAllStudents() failed", e);

            throw new PlacementApplicationException(
                    messageUtil.badRequest("error.unexpected"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}