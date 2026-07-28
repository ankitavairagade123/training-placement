package T_And_P.Training_and_Placement.entity;

import T_And_P.Training_and_Placement.audit.AuditEntity;
import T_And_P.Training_and_Placement.constant.FieldType;
import T_And_P.Training_and_Placement.constant.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "application_field_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFieldMaster extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id")
    private Long fieldId;

    @Column(name = "field_name", nullable = false, unique = true)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}