package T_And_P.Training_and_Placement.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import T_And_P.Training_and_Placement.bean.CompanyBean;
import T_And_P.Training_and_Placement.entity.CompanyMaster;

public interface CompanyRepository extends JpaRepository<CompanyMaster, Long> {

    @Query(value = "SELECT id "
            + "FROM company_master "
            + "WHERE id = :id", nativeQuery = true)
    Optional<Long> getByIdCompany(@Param("id") Long id);

    @Query(value = "SELECT id "
            + "FROM company_master "
            + "WHERE LOWER(company_code) = LOWER(:companyCode)", nativeQuery = true)
    Optional<Long> findByCompanyCodeIgnoreCase(
            @Param("companyCode") String companyCode);

    @Query(value = "SELECT id AS id, "
            + "company_name AS companyName, "
            + "company_code AS companyCode, "
            + "company_type AS companyType, "
            + "industry AS industryType, "
            + "hr_name AS hrName, "
            + "email AS email, "
            + "pincode AS pincode, "
            + "address AS address, "
            + "contact_number AS contactNumber, "
            + "website AS website, "
            + "status AS status "
            + "FROM company_master "
            + "ORDER BY id DESC", nativeQuery = true)
    List<CompanyBean> getAllCompany();

    @Query(value = "SELECT id AS id, "
            + "company_name AS companyName, "
            + "company_code AS companyCode, "
            + "company_type AS companyType, "
            + "industry AS industryType, "
            + "hr_name AS hrName, "
            + "pincode AS pincode, "
            + "email AS email, "
            + "address AS address, "
            + "contact_number AS contactNumber, "
            + "website AS website, "
            + "status AS status "
            + "FROM company_master "
            + "WHERE id = :id", nativeQuery = true)
    Optional<CompanyBean> getByIdCompanyDetails(@Param("id") Long id);
}