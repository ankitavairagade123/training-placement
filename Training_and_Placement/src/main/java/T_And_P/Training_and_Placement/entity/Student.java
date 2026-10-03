package T_And_P.Training_and_Placement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "email")
    private String email;

    @Column(name = "ssc_percentage")
    private Double sscPercentage;

    @Column(name = "hsc_percentage")
    private Double hscPercentage;

    @Column(name = "ug_cgpa")
    private Double ugCgpa;

    @Column(name = "attendance")
    private Double attendance;

    @Column(name = "active_backlogs")
    private Integer activeBacklogs;

    @Column(name = "branch")
    private String branch;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "passing_year")
    private Integer passingYear;

    @Column(name = "resume_path")
    private String resumePath;
}

