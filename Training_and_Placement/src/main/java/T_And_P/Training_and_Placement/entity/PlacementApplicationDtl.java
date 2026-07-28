package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "placement_application_dtl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementApplicationDtl extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private PlacementApplicationHdr applicationHdr;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_value")
    private String fieldValue;
}
