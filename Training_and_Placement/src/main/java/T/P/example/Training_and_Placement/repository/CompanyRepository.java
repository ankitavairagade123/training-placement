package T.P.example.Training_and_Placement.repository;

import T.P.example.Training_and_Placement.Entity.Company;
import T.P.example.Training_and_Placement.bean.CompanyBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    @Query(value = "select id from company_master where id=:id  ",nativeQuery = true)
    Optional<Long> getByIdCompany(@Param("id") Long id);

    @Query(value =  "select id as id,companyName as companyName, pincode as pincode,address as address " +
            " from company_master ",nativeQuery = true)
    List<CompanyBean> getAllCompany();


    @Query(value =  "select id as id,companyName as companyName, pincode as pincode,address as address " +
            " from company_master where id=:id ",nativeQuery = true)
    Optional<CompanyBean> getByIdCompanyDetails(Long id);
}
