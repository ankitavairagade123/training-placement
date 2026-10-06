package T_And_P.Training_and_Placement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.EligibilityBean;
import T_And_P.Training_and_Placement.entity.EligibilityMaster;

public interface EligibilityMasterRepository
        extends JpaRepository<EligibilityMaster, Long> {

    @Query(value = "SELECT id AS id, "
            + "eligibility_type AS eligibilityType, "
            + "status AS status "
            + "FROM eligibility_master "
            + "ORDER BY id DESC", nativeQuery = true)
    List<EligibilityBean> getAllEligibility();

    @Query(value = "SELECT id AS id, "
            + "eligibility_type AS eligibilityType, "
            + "status AS status "
            + "FROM eligibility_master "
            + "WHERE status = :status "
            + "ORDER BY id DESC", nativeQuery = true)
    List<EligibilityBean> getEligibilityByStatus(
            @Param("status") String status);

    @Query(value = "SELECT id AS id, "
            + "eligibility_type AS eligibilityType, "
            + "status AS status "
            + "FROM eligibility_master "
            + "WHERE id = :id", nativeQuery = true)
    Optional<EligibilityBean> getEligibilityById(
            @Param("id") Long id);

    @Query(value = "SELECT id AS id, "
            + "eligibility_type AS eligibilityType, "
            + "status AS status "
            + "FROM eligibility_master "
            + "WHERE id IN (:ids)", nativeQuery = true)
    List<EligibilityBean> getEligibilityByIds(
            @Param("ids") List<Long> ids);

    @Query(value = "SELECT id "
            + "FROM eligibility_master "
            + "WHERE id = :id", nativeQuery = true)
    Optional<Long> existsEligibilityById(
            @Param("id") Long id);

    @Query(value = "SELECT id "
            + "FROM eligibility_master "
            + "WHERE LOWER(eligibility_type) = LOWER(:eligibilityType)",
            nativeQuery = true)
    Optional<Long> findByEligibilityTypeIgnoreCase(
            @Param("eligibilityType") String eligibilityType);

    @Query(value = "SELECT id "
            + "FROM eligibility_master "
            + "WHERE LOWER(eligibility_type) = LOWER(:eligibilityType) "
            + "AND id <> :id", nativeQuery = true)
    Optional<Long> existsByEligibilityTypeIgnoreCaseAndIdNot(
            @Param("eligibilityType") String eligibilityType,
            @Param("id") Long id);
}
