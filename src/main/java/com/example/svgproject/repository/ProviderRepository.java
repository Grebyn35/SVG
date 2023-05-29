package com.example.svgproject.repository;

import com.example.svgproject.model.Post;
import com.example.svgproject.model.Provider;
import com.example.svgproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProviderRepository extends CrudRepository<Provider,Long> {

    Provider findById(long id);
    //Must have @Param because of hibernate bugg
    ArrayList<Provider> findAllByOtherSettingsContaining(@Param("otherSettings")String otherSettings);
    Page<Provider> findAllByIdIsNotNullAndHiddenIsFalseOrderBySponsoredDescPayingDescDateCreatedDesc(Pageable pageable);

    Page<Provider> findAllByIdIsNotNullOrderBySponsoredDescPayingDescDateCreatedDesc(Pageable pageable);
    Page<Provider> findAllByNameContainingAndHiddenIsFalseAndTypeListContainingAndCountyContainingAndGradeContainingOrderBySponsoredDescPayingDescDateCreatedDesc(String searchInput, String branchType, String county, String grade, Pageable pageable);

    Page<Provider> findAllByNameContainingAndHiddenIsFalseAndTypeListContainingAndCountyContainingAndGradeContainingAndPayingIsTrueOrderBySponsoredDescPayingDescDateCreatedDesc(String searchInput, String branchType, String county, String grade, Pageable pageable);
    Page<Provider> findAllByNameContainingAndHiddenIsFalseAndTypeListContainingAndCountyContainingAndGradeContainingAndPayingIsFalseOrderBySponsoredDescPayingDescDateCreatedDesc(String searchInput, String branchType, String county, String grade, Pageable pageable);
}
