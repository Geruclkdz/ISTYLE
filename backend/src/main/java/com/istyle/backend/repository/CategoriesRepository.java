package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CategoriesRepository extends JpaRepository<Category, Integer> {
}