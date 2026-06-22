package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.entity.TrainingAndPlacementPlannerHdr;

import java.util.List;

public interface TrainingAndPlacementPlannerService {

    List<TrainingAndPlacementPlannerHdr> getActivePlanner();
}
