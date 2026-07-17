package org.mountblue.BlogApplication.Service;

import org.mountblue.BlogApplication.Entity.Comment;
import org.mountblue.BlogApplication.Entity.Post;
import org.mountblue.BlogApplication.Repository.CommentRepository;
import org.mountblue.BlogApplication.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public void addComment(Long postId, String name, String email, String text) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setName(name);
        comment.setEmail(email);
        comment.setComment(text);
        comment.setPost(post);

        commentRepository.save(comment);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public void updateComment(Long id, String name, String email, String text) {
        Comment comment = getComment(id);
        comment.setName(name);
        comment.setEmail(email);
        comment.setComment(text);

        commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
