package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Comment;
import com.istyle.backend.api.internal.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPostId(int outfitId);

}
