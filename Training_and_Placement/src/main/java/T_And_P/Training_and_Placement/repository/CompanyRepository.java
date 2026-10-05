package T_And_P.Training_and_Placement.repository;

import T_And_P.Training_and_Placement.bean.CompanyBean;
import T_And_P.Training_and_Placement.entity.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyMaster, Long> {

    @Query(value = "select id from company_master where id = :id", nativeQuery = true)
    Optional<Long> getByIdCompany(@Param("id") Long id);

    @Query(value = "select id from company_master "
            + "where lower(company_code) = lower(:companyCode)", nativeQuery = true)
    Optional<Long> findByCompanyCodeIgnoreCase(@Param("companyCode") String companyCode);

    @Query(value = "select id as id, "
            + "company_name as companyName, "
            + "company_code as companyCode, "
            + "company_type as companyType, "
            + "industry as industryType, "
            + "hr_name as hrName, "
            + "email as email, "
            + "pincode as pincode, "
            + "address as address, "
            + "contact_number as contactNumber, "
            + "website as website, "
            + "status as status "
            + "from company_master "
            + "order by id desc", nativeQuery = true)
    List<CompanyBean> getAllCompany();

    @Query(value = "select id as id, "
            + "company_name as companyName, "
            + "company_code as companyCode, "
            + "company_type as companyType, "
            + "industry as industryType, "
            + "hr_name as hrName, "
            + "pincode as pincode, "
            + "email as email, "
            + "address as address, "
            + "contact_number as contactNumber, "
            + "website as website, "
            + "status as status "
            + "from company_master "
            + "where id = :id", nativeQuery = true)
    Optional<CompanyBean> getByIdCompanyDetails(@Param("id") Long id);

    @Query(value = "select count(*) from company_master", nativeQuery = true)
    Long countAllCompanies();
}