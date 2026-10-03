package T_And_P.Training_and_Placement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.ApplicationDtlBean;
import T_And_P.Training_and_Placement.entity.PlacementApplicationDtl;

public interface PlacementApplicationDtlRepository
        extends JpaRepository<PlacementApplicationDtl, Long> {

    @Query(value = "SELECT id AS applicationDetailId, "
            + "field_name AS fieldName, "
            + "field_value AS fieldValue "
            + "FROM placement_application_dtl "
            + "WHERE application_id = :applicationId "
            + "ORDER BY id", nativeQuery = true)
    List<ApplicationDtlBean> getDetailsByApplicationId(
            @Param("applicationId") Long applicationId);
}