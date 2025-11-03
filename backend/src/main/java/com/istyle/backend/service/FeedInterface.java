package com.istyle.backend.service;

import com.istyle.backend.api.external.CommentDTO;
import com.istyle.backend.api.external.PostCreateDTO;
import com.istyle.backend.api.external.PostDTO;
import com.istyle.backend.api.internal.Follow;


import java.util.List;

public interface FeedInterface {
    List<PostDTO> getUserFeed(Integer userId);
    void shareOutfit(PostCreateDTO postCreateDTO, Integer userId);
    int addStarToPost(Integer postId, Integer userId);
    void addCommentToPost(CommentDTO commentDTO, Integer userId);
    List<CommentDTO> getCommentsForPost(Integer postId);
    void followUser(int followerId, int followeeId);
    void unfollowUser(int followerId, int followeeId);
    List <Follow> getFollowing(int userId);
    void deletePost(Integer postId, Integer userId);
    void deleteComment(Integer commentId, Integer userId);
}
