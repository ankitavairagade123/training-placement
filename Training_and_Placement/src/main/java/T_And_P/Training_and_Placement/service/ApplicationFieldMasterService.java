package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.constant.Status;
import T_And_P.Training_and_Placement.dto.ApplicationFieldRequestDTO;
import T_And_P.Training_and_Placement.dto.ApplicationFieldResponseDTO;
import T_And_P.Training_and_Placement.entity.ApplicationFieldMaster;
import T_And_P.Training_and_Placement.repository.ApplicationFieldMasterRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationFieldMasterService  {

    public List<ApplicationFieldResponseDTO> getAllFields();

    List<ApplicationFieldResponseDTO> getActiveFields();

    public ApplicationFieldResponseDTO saveField(ApplicationFieldRequestDTO dto);
}