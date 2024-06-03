package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitsRepository extends JpaRepository<Outfit, Integer> {
    List<Outfit> findByUserId(int userId);
}
