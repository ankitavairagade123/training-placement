//package T_And_P.Training_and_Placement.entity;
//
//import T_And_P.Training_and_Placement.constant.Status;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Data
//@Table(name = "certification_master")
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class CertificationMaster {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "certification_id")
//    private Long certificationId;
//
//    @Column(name = "certification_name")
//    private String certificationName;
//
//    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//}