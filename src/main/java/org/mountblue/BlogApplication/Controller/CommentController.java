package org.mountblue.BlogApplication.Controller;

import org.mountblue.BlogApplication.Entity.Comment;
import org.mountblue.BlogApplication.Entity.Post;
import org.mountblue.BlogApplication.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;


    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String comment) {

        commentService.addComment(postId, name, email, comment);
        return "redirect:/posts/" + postId;
    }


    @GetMapping("/comments/{id}/edit")
    public String editComment(@PathVariable Long id, Model model) {

        Comment comment = commentService.getComment(id);
        Post post = comment.getPost();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/posts/" + post.getId() + "?error=unauthorized";
        }

        model.addAttribute("post", post);
        model.addAttribute("editCommentId", id);

        return "post";
    }



    @PostMapping("/comments/{id}/update")
    public String updateComment(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String email,
                                @RequestParam String comment) {

        Comment existing = commentService.getComment(id);
        Long postId = existing.getPost().getId();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !existing.getName().equals(auth.getName())) {
            return "redirect:/posts/" + postId + "?error=unauthorized";
        }

        commentService.updateComment(id, name, email, comment);
        return "redirect:/posts/" + postId;
    }


    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getComment(id);
        Long postId = comment.getPost().getId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !comment.getName().equals(auth.getName())) {
            return "redirect:/posts/" + postId + "?error=unauthorized";
        }

        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }
}

