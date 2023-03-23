package com.example.svgproject.repository;

import com.example.svgproject.model.Post;
import com.example.svgproject.model.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post,Long> {

    Provider findById(long id);
    Page<Provider> findAll(Pageable pageable);
}