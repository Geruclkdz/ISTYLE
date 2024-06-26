package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ClothesRepository extends JpaRepository<Clothes, Integer> {
    List<Clothes> findByUserId(int userId);
}