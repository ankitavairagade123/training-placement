package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "training_and_placement_planner_hdr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAndPlacementPlannerHdr {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "planner_name")
    private String planner_name;

    @Column(name = "planner_description")
    private String plannerDesc;

    @Column(name = "planner_type")
    @Enumerated(EnumType.STRING)
    private PlannerType plannerType;

    @Column(name = "mode")
    @Enumerated(EnumType.STRING)
    private Mode mode;

    @Column(name = "planner_schedule_type")
    @Enumerated(EnumType.STRING)
    private PlannerScheduleType plannerScheduleType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "max_student_count")
    private Integer maxStudents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyMaster company;

    @OneToMany(mappedBy = "plannerHdr", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrainingAndPlacementPlannerDtl> trainingAndPlacementPlannerDtls;


}
