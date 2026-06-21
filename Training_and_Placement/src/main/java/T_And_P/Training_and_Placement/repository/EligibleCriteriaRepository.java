package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.EligibleCriteriaProjection;
import T_And_P.Training_and_Placement.entity.EligibleCritriaMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface EligibleCriteriaRepository
        extends JpaRepository<EligibleCritriaMaster, Long> {

    @Query(value = """
            SELECT *
            FROM eligible_master
            WHERE id = :id
            """, nativeQuery = true)
    Optional<EligibleCritriaMaster> getByIdEligible(@Param("id") Long id);

    @Query(value = """
            SELECT *
            FROM eligible_master
            WHERE LOWER(eligible_name)=LOWER(:eligibleName)
            """, nativeQuery = true)
    Optional<EligibleCritriaMaster> findByEligibleName(
            @Param("eligibleName") String eligibleName);

    @Query(value = """
            SELECT *
            FROM eligible_master
            WHERE LOWER(eligible_name)=LOWER(:eligibleName)
            AND id <> :id
            """, nativeQuery = true)
    Optional<EligibleCritriaMaster> findDuplicateForUpdate(
            @Param("eligibleName") String eligibleName,
            @Param("id") Long id);

    @Query(value = """
            SELECT
                em.id as id,
                em.eligible_name as eligibleName,
                em.status as status
            FROM eligible_master em
            ORDER BY em.id DESC
            """, nativeQuery = true)
    List<EligibleCriteriaProjection> getAllEligibleCriteria();

    @Query(value = """
            SELECT
                em.id as id,
                em.eligible_name as eligibleName,
                em.status as status
            FROM eligible_master em
            WHERE em.id=:id
            """, nativeQuery = true)
    Optional<EligibleCriteriaProjection> getEligibleCriteriaById(
            @Param("id") Long id);
}
