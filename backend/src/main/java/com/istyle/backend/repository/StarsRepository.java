package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StarsRepository extends JpaRepository<Star, Integer> {
    Star findByPostIdAndUserId(int postId, int userId);
    int countByPostId(Integer postId);
}
