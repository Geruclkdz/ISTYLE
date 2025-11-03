package com.istyle.backend.mapper;

import com.istyle.backend.api.external.PostDTO;
import com.istyle.backend.api.internal.Post;
import com.istyle.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {
    private final OutfitMapper outfitMapper;
    private final UserRepository userRepository;

    public PostDTO map(Post post) {
        return new PostDTO()
                .setId(post.getId())
                .setUserId(post.getUser().getId())
                .setText(post.getText())
                .setOutfitDTO(outfitMapper.map(post.getOutfit()))
                .setCreatedAt(post.getCreatedAt());
    }

    public Post map(PostDTO postDTO) {
        return new Post()
                .setId(postDTO.getId())
                .setUser(userRepository.getUserById(postDTO.getUserId()))
                .setText(postDTO.getText())
                .setOutfit(outfitMapper.map(postDTO.getOutfitDTO()))
                .setCreatedAt(postDTO.getCreatedAt());
    }

    public List<PostDTO> mapPostsToDTOs(List<Post> posts) {
        return posts.stream().map(this::map).toList();
    }

    public  List<Post> mapDTOsToPosts(List<PostDTO> postDTOs) {
        return postDTOs.stream().map(this::map).toList();
    }
}
