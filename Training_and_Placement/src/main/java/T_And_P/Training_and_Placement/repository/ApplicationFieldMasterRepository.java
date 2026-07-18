package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.ApplicationFieldProjection;
import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.entity.ApplicationFieldMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationFieldMasterRepository
        extends JpaRepository<ApplicationFieldMaster, Long> {

    Optional<ApplicationFieldMaster> findByFieldNameIgnoreCase(String fieldName);

    List<ApplicationFieldMaster> findByStatus(Status status);

    Optional<ApplicationFieldMaster> findByFieldId(Long fieldId);

    @Query("""
           select
           a.fieldId as fieldId,
           a.fieldName as fieldName,
           a.fieldType as fieldType,
           a.status as status
           from ApplicationFieldMaster a
           where a.fieldId = :fieldId
           """)
    Optional<ApplicationFieldProjection> getApplicationFieldByFieldId(Long fieldId);

    @Query("""
           select
           a.fieldId as fieldId,
           a.fieldName as fieldName,
           a.fieldType as fieldType,
           a.status as status
           from ApplicationFieldMaster a
           """)
    List<ApplicationFieldProjection> getAllApplicationFields();

    @Query("""
           select a
           from ApplicationFieldMaster a
           where lower(a.fieldName)=lower(:fieldName)
           and a.fieldId<>:fieldId
           """)
    Optional<ApplicationFieldMaster> findDuplicateForUpdate(
            String fieldName,
            Long fieldId);
}