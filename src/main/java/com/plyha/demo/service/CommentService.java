package com.plyha.demo.service;

import com.plyha.demo.dto.CommentDTO;
import com.plyha.demo.entity.Comment;
import com.plyha.demo.entity.Post;
import com.plyha.demo.entity.User;
import com.plyha.demo.exceptions.PostNotFoundException;
import com.plyha.demo.repository.CommentRepository;
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
public class CommentService {
    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Long postId, CommentDTO commentDTO, Principal principal){
        User user = getUserByPrincipal(principal);
        Post post = postRepository.findById(postId)
            .orElseThrow(()-> new PostNotFoundException("Post cannot be found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUsername(user.getUsername());
        comment.setUserId(user.getId());
        comment.setMessage(commentDTO.getMessage());

        LOG.info("Saving comment for Post: {}", post.getId());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllCommentsForPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new PostNotFoundException("Cannot find post"));
        List<Comment> comments = commentRepository.findAllByPost(post);
        return comments;
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> comment =  commentRepository.findById(commentId);
        comment.ifPresent(commentRepository::delete);
    }
    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found with username"+ username));

    }
}
