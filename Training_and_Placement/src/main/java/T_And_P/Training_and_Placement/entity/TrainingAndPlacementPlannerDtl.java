package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.constant.CriteriaRule;
import T_And_P.Training_and_Placement.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "training_and_placement_planner_dtl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingAndPlacementPlannerDtl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_hdr_id", nullable = false)
    private TrainingAndPlacementPlannerHdr plannerHdr;

    @Column(name = "criteria_rule")
    @Enumerated(EnumType.STRING)
    private CriteriaRule criteriaRule;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

}
