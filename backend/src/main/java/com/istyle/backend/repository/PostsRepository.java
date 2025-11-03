package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {
    List<Post> findFeedByUserId(Integer userId);
    Post findPostById(Integer postId);
}
