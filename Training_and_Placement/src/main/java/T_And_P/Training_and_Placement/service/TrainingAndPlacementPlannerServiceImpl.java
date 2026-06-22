package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;
import T_And_P.Training_and_Placement.repository.TrainingAndPlacementPlannerHdrRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingAndPlacementPlannerServiceImpl implements TrainingAndPlacementPlannerService{

    private final TrainingAndPlacementPlannerHdrRepository plannerRepository;

    public TrainingAndPlacementPlannerServiceImpl(
            TrainingAndPlacementPlannerHdrRepository plannerRepository){
        this.plannerRepository = plannerRepository;
    }

    @Override
    public List<TrainingAndPlacementPlannerHdr> getActivePlanner() {
        return plannerRepository.findByStatus(Status.ACTIVE);
    }
}
