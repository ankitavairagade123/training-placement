package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import T_And_P.Training_and_Placement.entity.PlacementApplicationDtl;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "placement_application_hdr",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "planner_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationHdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id", nullable = false)
    private TrainingAndPlacementPlannerHdr plannerHdr;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    @Column(name = "applied_date", nullable = false)
    private LocalDateTime appliedDate;

    @OneToMany(
            mappedBy = "applicationHdr",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PlacementApplicationDtl> applicationDetails;


}