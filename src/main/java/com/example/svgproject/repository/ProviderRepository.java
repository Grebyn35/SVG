package com.example.svgproject.repository;

import com.example.svgproject.model.Provider;
import com.example.svgproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProviderRepository extends CrudRepository<Provider,Long> {

    Provider findById(long id);
    Page<Provider> findAll(Pageable pageable);
    Page<Provider> findAllByNameContainingAndTypeListContainingAndCountyContainingAndGradeContaining(String searchInput, String branchType, String county, String grade, Pageable pageable);
}
