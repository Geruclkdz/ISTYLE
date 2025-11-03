package com.istyle.backend.mapper;

import com.istyle.backend.api.external.CommentDTO;
import com.istyle.backend.api.external.PostDTO;
import com.istyle.backend.api.internal.Comment;
import com.istyle.backend.api.internal.Post;
import com.istyle.backend.repository.CommentsRepository;
import com.istyle.backend.repository.PostsRepository;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;
    private final PostsRepository postsRepository;

    public Comment map(CommentDTO commentDTO) {
        return new Comment()
                .setId(commentDTO.getId())
                .setUser(userRepository.getUserById(commentDTO.getUserId()))
                .setText(commentDTO.getText())
                .setPost(postsRepository.findPostById(commentDTO.getPostId()));
    }

    public CommentDTO map(Comment comment) {
        return new CommentDTO()
                .setId(comment.getId())
                .setUserId(comment.getUser().getId())
                .setText(comment.getText())
                .setPostId(comment.getPost().getId());
    }
}
