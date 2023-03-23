package com.example.svgproject.repository;

import com.example.svgproject.model.Nyhet;
import com.example.svgproject.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NyhetRepository extends CrudRepository<Nyhet,Long> {

    Nyhet findById(long id);
    Page<Nyhet> findAll(Pageable pageable);
    Page<Nyhet> findAllByCategoryContaining(String category, Pageable pageable);
    Page<Nyhet> findAllByCategoryContainingAndIdNot(String category, long id, Pageable pageable);
    Page<Nyhet> findAllByTitleContainingAndCategoryContaining(String searchInput, String category, Pageable pageable);
}
