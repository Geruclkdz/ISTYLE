package com.istyle.backend.service;

import com.istyle.backend.api.external.PostCreateDTO;
import com.istyle.backend.api.external.PostDTO;
import com.istyle.backend.api.internal.*;
import com.istyle.backend.mapper.CommentMapper;
import com.istyle.backend.mapper.PostMapper;
import com.istyle.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.istyle.backend.api.external.CommentDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedImpl implements FeedInterface {

    private final PostsRepository postRepository;
    private final CommentsRepository commentRepository;
    private final StarsRepository starsRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;
    private final PostMapper postMapper;
    private final FollowRepository followRepository;
    private final OutfitsRepository outfitRepository;

    public List<PostDTO> getUserFeed(Integer userId) {
        var posts = postRepository.findFeedByUserId(userId);

        List<PostDTO> dtos = postMapper.mapPostsToDTOs(posts);

        for (PostDTO dto : dtos) {
            int count = starsRepository.countByPostId(dto.getId());
            dto.setStarCount(count);
        }

        return dtos;
    }

    @Override
    public void shareOutfit(PostCreateDTO postCreateDTO, Integer userId) {
        int outfitId = postCreateDTO.getOutfitId();
        Optional<Outfit> outfit = outfitRepository.findById(outfitId);
        if (outfit.isEmpty()) {
            throw new RuntimeException("Outfit not found");
        }

        Post post = Post.builder()
                .outfit(outfit.get())
                .user(userRepository.getUserById(userId))
                .text(postCreateDTO.getText())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        postRepository.save(post);
    }

    @Override
    public int addStarToPost(Integer postId, Integer userId) {
        Star existingStar = starsRepository.findByPostIdAndUserId(postId, userId);

        if (existingStar == null) {
            // Add the star
            Star star = Star.builder()
                    .post(postsRepository.findPostById(postId))
                    .user(userRepository.getUserById(userId))
                    .build();
            starsRepository.save(star);
        } else {
            starsRepository.delete(existingStar);
        }
        return starsRepository.countByPostId(postId);

    }

    @Override
    public void addCommentToPost(CommentDTO commentDTO, Integer userId) {
        Comment comment = Comment.builder()
                .post(postsRepository.findPostById(commentDTO.getPostId()))
                .user(userRepository.getUserById(userId))
                .text(commentDTO.getText())
                .build();

        commentRepository.save(comment);
    }

    @Override
    public List<CommentDTO> getCommentsForPost(Integer postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(commentMapper::map)
                .toList();
    }

    @Override
    public void followUser(int followerId, int followeeId) {
        User follower = userRepository.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new RuntimeException("Followee not found"));

        if (followRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new RuntimeException("You are already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        followRepository.save(follow);
    }

    @Override
    public void unfollowUser(int followerId, int followeeId) {
        User follower = userRepository.getUserById(followerId);
        User followee = userRepository.getUserById(followeeId);

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);

        followRepository.delete(follow);
    }

    @Override
    public List<Follow> getFollowing(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return followRepository.findByFollower(user);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = postsRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.getUserById(userId);
        boolean isOwner = post.getUser() != null && post.getUser().getId() == userId;
        boolean isModerator = user != null && "moderator".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isModerator) {
            throw new RuntimeException("Forbidden: cannot delete this post");
        }

        // remove stars referencing this post first to avoid FK constraints
        starsRepository.deleteByPostId(postId);

        // delete post (comments are cascade removed via Post entity)
        postsRepository.delete(post);
    }

    @Override
    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.getUserById(userId);
        boolean isOwner = comment.getUser() != null && comment.getUser().getId() == userId;
        boolean isModerator = user != null && "moderator".equalsIgnoreCase(user.getRole());
        if (!isOwner && !isModerator) {
            throw new RuntimeException("Forbidden: cannot delete this comment");
        }
        commentRepository.delete(comment);
    }

}
