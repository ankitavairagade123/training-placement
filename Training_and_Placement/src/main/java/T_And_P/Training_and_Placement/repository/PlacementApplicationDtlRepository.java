package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.entity.PlacementApplicationDtl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementApplicationDtlRepository
        extends JpaRepository<PlacementApplicationDtl, Long> {

    List<PlacementApplicationDtl> findByApplicationHdrApplicationId(Long applicationId);

}
