package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingAndPlacementPlannerHdrRepository extends JpaRepository<TrainingAndPlacementPlannerHdr,Long> {

    List<TrainingAndPlacementPlannerHdr> findByStatus(Status status);
}
