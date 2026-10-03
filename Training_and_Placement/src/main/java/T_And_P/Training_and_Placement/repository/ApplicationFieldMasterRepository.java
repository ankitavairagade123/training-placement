package T_And_P.Training_and_Placement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.ApplicationFieldProjection;
import T_And_P.Training_and_Placement.entity.ApplicationFieldMaster;

public interface ApplicationFieldMasterRepository
        extends JpaRepository<ApplicationFieldMaster, Long> {

    @Query(value = "SELECT field_id AS fieldId, "
            + "field_name AS fieldName, "
            + "field_type AS fieldType, "
            + "status AS status "
            + "FROM application_field_master "
            + "ORDER BY field_id DESC", nativeQuery = true)
    List<ApplicationFieldProjection> getAllApplicationFields();

    @Query(value = "SELECT field_id AS fieldId, "
            + "field_name AS fieldName, "
            + "field_type AS fieldType, "
            + "status AS status "
            + "FROM application_field_master "
            + "WHERE status = :status "
            + "ORDER BY field_id DESC", nativeQuery = true)
    List<ApplicationFieldProjection> getApplicationFieldsByStatus(
            @Param("status") String status);

    @Query(value = "SELECT field_id AS fieldId, "
            + "field_name AS fieldName, "
            + "field_type AS fieldType, "
            + "status AS status "
            + "FROM application_field_master "
            + "WHERE field_id = :fieldId", nativeQuery = true)
    Optional<ApplicationFieldProjection> getApplicationFieldByFieldId(
            @Param("fieldId") Long fieldId);

    @Query(value = "SELECT field_id "
            + "FROM application_field_master "
            + "WHERE field_id = :fieldId", nativeQuery = true)
    Optional<Long> existsFieldById(
            @Param("fieldId") Long fieldId);

    @Query(value = "SELECT field_id "
            + "FROM application_field_master "
            + "WHERE LOWER(field_name) = LOWER(:fieldName)", nativeQuery = true)
    Optional<Long> findByFieldNameIgnoreCase(
            @Param("fieldName") String fieldName);

    @Query(value = "SELECT field_id "
            + "FROM application_field_master "
            + "WHERE LOWER(field_name) = LOWER(:fieldName) "
            + "AND field_id <> :fieldId", nativeQuery = true)
    Optional<Long> findDuplicateForUpdate(
            @Param("fieldName") String fieldName,
            @Param("fieldId") Long fieldId);
}