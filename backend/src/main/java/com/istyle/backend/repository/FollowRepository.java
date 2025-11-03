package com.istyle.backend.repository;

import com.istyle.backend.api.internal.Follow;
import com.istyle.backend.api.internal.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFollower(User follower);
    List<Follow> findByFollowee(User followee);
    boolean existsByFollowerAndFollowee(User follower, User followee);
    Follow findByFollowerAndFollowee(User follower, User followee);
    Set<Follow> findByFollowerId(int followerId);
}
