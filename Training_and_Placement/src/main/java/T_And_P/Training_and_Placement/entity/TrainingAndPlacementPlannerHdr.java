package T_And_P.Training_and_Placement.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import T_And_P.Training_and_Placement.audit.AuditEntity;
import T_And_P.Training_and_Placement.constant.Mode;
import T_And_P.Training_and_Placement.constant.PlannerScheduleType;
import T_And_P.Training_and_Placement.constant.PlannerType;
import T_And_P.Training_and_Placement.constant.Status;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "training_and_placement_planner_hdr")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAndPlacementPlannerHdr extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "planner_name")
    private String plannerName;

    @Column(name = "planner_description")
    private String plannerDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "planner_type")
    private PlannerType plannerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private Mode mode;

    @Enumerated(EnumType.STRING)
    @Column(name = "planner_schedule_type")
    private PlannerScheduleType plannerScheduleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "registration_start_date")
    private LocalDateTime registrationStartDate;

    @Column(name = "registration_end_date")
    private LocalDateTime registrationEndDate;

    @Column(name = "max_student_count")
    private Integer maxStudents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyMaster company;

    @OneToMany(
            mappedBy = "plannerHdr",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    @ToString.Exclude
    private List<TrainingAndPlacementPlannerDtl> trainingAndPlacementPlannerDtls =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "plannerHdr",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    @ToString.Exclude
    private List<PlannerQuestion> questions = new ArrayList<>();

    @OneToMany(
            mappedBy = "plannerHdr",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    @ToString.Exclude
    private List<PlacementApplicationHdr> placementApplications =
            new ArrayList<>();

    @Column(name = "venue")
    private String venue;

    @Column(name = "website")
    private String website;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Column(name = "published_by")
    private String publishedBy;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}