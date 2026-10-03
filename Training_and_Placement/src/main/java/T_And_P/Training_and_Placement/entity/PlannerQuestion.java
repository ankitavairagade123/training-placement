package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.constant.FieldType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planner_question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id", nullable = false)
    private TrainingAndPlacementPlannerHdr plannerHdr;

    @Column(name = "question", nullable = false)
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    @Column(name = "mandatory")
    @Builder.Default
    private Boolean mandatory = false;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    @ToString.Exclude
    private List<QuestionOption> options = new ArrayList<>();
}