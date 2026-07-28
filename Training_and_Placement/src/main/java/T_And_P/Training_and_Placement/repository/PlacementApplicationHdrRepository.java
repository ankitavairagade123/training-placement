package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.entity.PlacementApplicationHdr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementApplicationHdrRepository
        extends JpaRepository<PlacementApplicationHdr, Long> {

    public boolean existsByPlannerHdrIdAndStudentStudentId(Long plannerId, Long studentId);

    List<PlacementApplicationHdr> findByStudentStudentId(Long studentId);

    List<PlacementApplicationHdr> findByPlannerHdrId(Long plannerId);


}
