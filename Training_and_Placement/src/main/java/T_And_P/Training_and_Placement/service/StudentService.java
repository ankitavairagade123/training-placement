package T_And_P.Training_and_Placement.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.bean.StudentBean;
import T_And_P.Training_and_Placement.dto.StudentRequestDTO;
import T_And_P.Training_and_Placement.dto.StudentResponseDTO;
import T_And_P.Training_and_Placement.entity.Student;
import T_And_P.Training_and_Placement.exception.PlacementApplicationException;
import T_And_P.Training_and_Placement.repository.StudentRepository;
import T_And_P.Training_and_Placement.util.MapperUtil;
import T_And_P.Training_and_Placement.util.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Business logic for student master.
 * Academic fields saved here are later compared against planner eligibility rules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final StudentRepository repository;
    private final MessageUtil messageUtil;

    /**
     * Creates or updates a student. Duplicate student names are not allowed.
     */
    public StudentResponseDTO saveStudent(StudentRequestDTO requestDTO) {

        log.info(
                "saveStudent() started for studentId={}, studentName={}",
                requestDTO == null ? null : requestDTO.getStudentId(),
                requestDTO == null ? null : requestDTO.getStudentName()
        );

        try {
            validateStudentRequest(requestDTO);

            if (Objects.nonNull(requestDTO.getStudentId())) {

                log.info(
                        "saveStudent() update path for studentId={}",
                        requestDTO.getStudentId()
                );

                repository.existsStudentById(requestDTO.getStudentId())
                        .orElseThrow(() ->
                                new PlacementApplicationException(
                                        messageUtil.badRequest("student.not.found"),
                                        HttpStatus.BAD_REQUEST
                                )
                        );

                repository.findDuplicateForUpdate(
                                requestDTO.getStudentName().trim(),
                                requestDTO.getStudentId()
                        )
                        .ifPresent(data -> {
                            log.info(
                                    "saveStudent() duplicate name found during update"
                            );

                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("student.already.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });

            } else {

                log.info(
                        "saveStudent() create path for studentName={}",
                        requestDTO.getStudentName()
                );

                repository.findByStudentNameIgnoreCase(
                                requestDTO.getStudentName().trim()
                        )
                        .ifPresent(data -> {
                            log.info(
                                    "saveStudent() duplicate name found during create"
                            );

                            throw new PlacementApplicationException(
                                    messageUtil.badRequest("student.already.exists"),
                                    HttpStatus.BAD_REQUEST
                            );
                        });
            }

            Student studentEntity = Student.builder()
                    .studentId(requestDTO.getStudentId())
                    .studentName(
                            MapperUtil.trimToNull(
                                    requestDTO.getStudentName()
                            )
                    )
                    .email(
                            MapperUtil.trimToNull(
                                    requestDTO.getEmail()
                            )
                    )
                    .sscPercentage(requestDTO.getSscPercentage())
                    .hscPercentage(requestDTO.getHscPercentage())
                    .ugCgpa(requestDTO.getUgCgpa())
                    .attendance(requestDTO.getAttendance())
                    .activeBacklogs(requestDTO.getActiveBacklogs())
                    .branch(
                            MapperUtil.trimToNull(
                                    requestDTO.getBranch()
                            )
                    )
                    .semester(requestDTO.getSemester())
                    .passingYear(requestDTO.getPassingYear())
                    .resumePath(
                            MapperUtil.trimToNull(
                                    requestDTO.getResumePath()
                            )
                    )
                    .build();

            Student savedStudent = repository.save(studentEntity);

            log.info(
                    "saveStudent() completed for studentId={}",
                    savedStudent.getStudentId()
            );

            return getById(savedStudent.getStudentId());

        } catch (PlacementApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "saveStudent() failed due to unexpected error",
                    e
            );

            throw new PlacementApplicationException(
                    messageUtil.badRequest("student.cannot.save"),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Loads one student by id.
     */
    public StudentResponseDTO getById(Long studentId) {

        log.info(
                "getById() started for studentId={}",
                studentId
        );

        StudentBean student = repository.getStudentById(studentId)
                .orElseThrow(() ->
                        new PlacementApplicationException(
                                messageUtil.badRequest("student.not.found"),
                                HttpStatus.BAD_REQUEST
                        )
                );

        log.info(
                "getById() completed for studentId={}",
                studentId
        );

        return toResponse(student);
    }

    /**
     * Returns all students for master listing.
     */
    public List<StudentResponseDTO> getAllStudents() {

        log.info("getAllStudents() started");

        List<StudentBean> students = repository.getAllStudents();

        if (CollectionUtils.isEmpty(students)) {
            log.info(
                    "getAllStudents() completed with empty list"
            );
            return Collections.emptyList();
        }

        List<StudentResponseDTO> response = students.stream()
                .filter(Objects::nonNull)
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info(
                "getAllStudents() completed, count={}",
                response.size()
        );

        return response;
    }

    /**
     * Validates student name and optional email format.
     */
    private void validateStudentRequest(
            StudentRequestDTO requestDTO) {

        log.info("validateStudentRequest() started");

        if (requestDTO == null) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("student.null"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!StringUtils.hasText(requestDTO.getStudentName())) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("student.name.required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (requestDTO.getStudentName().trim().length() > 50) {
            throw new PlacementApplicationException(
                    messageUtil.badRequest("student.name.max.length"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (StringUtils.hasText(requestDTO.getEmail())
                && !EMAIL_PATTERN.matcher(
                requestDTO.getEmail().trim()
        ).matches()) {

            throw new PlacementApplicationException(
                    messageUtil.badRequest(
                            "validation.email.invalid"
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("validateStudentRequest() completed");
    }

    /**
     * Maps student entity to API response, including academic eligibility fields.
     */
    private StudentResponseDTO toResponse(StudentBean student) {

        if (student == null) {
            return null;
        }

        log.debug(
                "toResponse() started for studentId={}",
                student.getStudentId()
        );

        return StudentResponseDTO.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .email(student.getEmail())
                .sscPercentage(student.getSscPercentage())
                .hscPercentage(student.getHscPercentage())
                .ugCgpa(student.getUgCgpa())
                .attendance(student.getAttendance())
                .activeBacklogs(student.getActiveBacklogs())
                .branch(student.getBranch())
                .semester(student.getSemester())
                .passingYear(student.getPassingYear())
                .resumePath(student.getResumePath())
                .build();
    }
}