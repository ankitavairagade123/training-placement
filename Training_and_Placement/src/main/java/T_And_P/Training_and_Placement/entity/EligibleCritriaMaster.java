package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eligible_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleCritriaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "eligible_name")
    private String eligibleName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

}
