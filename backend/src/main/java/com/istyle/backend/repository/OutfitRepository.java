package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Integer> {
    Set<Outfit> findByUserId(Integer userId);
}
