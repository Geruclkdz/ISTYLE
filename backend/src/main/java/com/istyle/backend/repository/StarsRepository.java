package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface StarsRepository extends JpaRepository<Star, Integer> {
    Star findByPostIdAndUserId(int postId, int userId);
    int countByPostId(Integer postId);

    @Modifying
    @Transactional
    void deleteByPostId(int postId);
}
