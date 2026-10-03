package T_And_P.Training_and_Placement.entity;
import T_And_P.Training_and_Placement.audit.AuditEntity;
import T_And_P.Training_and_Placement.constant.CriteriaRule;
import T_And_P.Training_and_Placement.constant.Status;

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
import javax.persistence.Table;

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
public class TrainingAndPlacementPlannerDtl extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planner_dtl_id")
    private Long plannerDtlId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_hdr_id", nullable = false)
    private TrainingAndPlacementPlannerHdr plannerHdr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_id", nullable = false)
    private EligibilityMaster eligibilityMaster;

    @Column(name = "mandatory")
    @Builder.Default
    private Boolean mandatory = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_rule")
    private CriteriaRule criteriaRule;

    @Column(name = "criteria_value")
    private String criteriaValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
