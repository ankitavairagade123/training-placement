package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacementApplicationHdrRepository
        extends JpaRepository<PlacementApplicationHdr, Long> {

        boolean existsByPlannerHdrIdAndStudentId(Long plannerId, Long studentId);


}
