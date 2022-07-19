package com.plyha.demo.service;

import com.plyha.demo.dto.PostDTO;
import com.plyha.demo.entity.Image;
import com.plyha.demo.entity.Post;
import com.plyha.demo.entity.User;
import com.plyha.demo.exceptions.PostNotFoundException;
import com.plyha.demo.repository.ImageRepository;
import com.plyha.demo.repository.PostRepository;
import com.plyha.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public Post createPost(PostDTO postDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("Saving Post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long id, Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(id,user)
            .orElseThrow(()-> new PostNotFoundException("Post cannot find for username: "+user.getUsername()));
    }

    public List<Post> getAllPostByUser(Principal principal){
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDateDesc(user);
    }

    public Post likePost(Long postId, String username){
        Post post = postRepository.findById(postId)
            .orElseThrow(()-> new PostNotFoundException("Post cannot be found"));

        Optional<String> userLiked = post.getLikedUsers()
            .stream()
            .filter(x -> x.equals(username)).findAny();

        if(userLiked.isPresent()){
            post.setLikes(post.getLikes() -1);
            post.getLikedUsers().remove(username);
        } else{
            post.setLikes(post.getLikes() +1);
            post.getLikedUsers().add(username);
        }

        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal){
        Post post = getPostById(postId,principal);
        Optional<Image> image = imageRepository.findByPostId(postId);

        postRepository.delete(post);

        image.ifPresent(imageRepository::delete);
        }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found with username"+ username));

    }
}
