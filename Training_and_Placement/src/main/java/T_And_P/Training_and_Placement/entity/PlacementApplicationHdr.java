package T_And_P.Training_and_Placement.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;

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
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private ApplicationStatus applicationStatus;

    @Column(name = "resume_path")
    private String resumePath;

    @Column(name = "terms_accepted")
    private Boolean termsAccepted;

    @Column(name = "offer_letter_path")
    private String offerLetterPath;

    @Column(name = "joining_letter_path")
    private String joiningLetterPath;

    @Column(name = "applied_date", nullable = false)
    private LocalDateTime appliedDate;

    @OneToMany(
            mappedBy = "applicationHdr",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    private List<PlacementApplicationDtl> applicationDetails = new ArrayList<>();
}