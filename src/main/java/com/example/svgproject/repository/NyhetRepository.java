package com.example.svgproject.repository;

import com.example.svgproject.model.Nyhet;
import com.example.svgproject.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface NyhetRepository extends CrudRepository<Nyhet,Long> {

    Nyhet findById(long id);
    Page<Nyhet> findAllByIdIsNotNullAndIdNotOrderByPublishedDesc(long id, Pageable pageable);
    Page<Nyhet> findAllByDateCreatedAfterOrderByPublishedDesc(Date date, Pageable pageable);
    Page<Nyhet> findAllByCategoryContainingAndIdNotOrderByPublishedDesc(String category, long id, Pageable pageable);
    Page<Nyhet> findAllByTitleContainingAndDateCreatedAfterOrderByPublishedDesc(String searchInput, Date date, Pageable pageable);
}
