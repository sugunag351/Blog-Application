package org.mountblue.BlogApplication.Controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.mountblue.BlogApplication.Entity.Post;
import org.mountblue.BlogApplication.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/newpost")
    public String showCreatePostForm() {
        return "form";
    }

    @PostMapping("/posts")
    public String addPost(@RequestParam String title,
                        @RequestParam String excerpt,
                        @RequestParam String tags,
                        @RequestParam String content) {

        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

         postService.savePost(title, excerpt, tagNames, content);

        return "redirect:/dashboard";
    }
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPost(id));
        return "post";
    }
@GetMapping("/dashboard")
public String showDashboard(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "latest") String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> authors,
        @RequestParam(required = false) List<String> tags,
        @RequestParam(defaultValue = "all") String publishedDate,
        Model model
) {

    Page<Post> posts = postService.getPosts(search, sort, authors, tags,publishedDate, page);

    model.addAttribute("posts", posts.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", posts.getTotalPages());
    model.addAttribute("sort", sort);
    model.addAttribute("publishedDate", publishedDate);

    model.addAttribute("allAuthors", postService.getAllAuthors());
    model.addAttribute("allTags", postService.getAllTags());

    model.addAttribute("selectedAuthors", authors);
    model.addAttribute("selectedTags", tags);

    return "dashboard";
}


    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {

        Post post = postService.getPost(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !post.getAuthor().equals(auth.getName())) {
            return "redirect:/dashboard?error=unauthorized";
        }
        postService.deletePost(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.getPost(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !post.getAuthor().equals(auth.getName())) {
            return "redirect:/dashboard?error=unauthorized";
        }
        model.addAttribute("post", post);

        String tags = post.getTags()
                .stream()
                .map(tag -> tag.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        model.addAttribute("tags", tags);
        return "form";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String tags,
                             @RequestParam String excerpt,
                             @RequestParam String content) {

        Post post = postService.getPost(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !post.getAuthor().equals(auth.getName())) {
            return "redirect:/dashboard?error=unauthorized";
        }

        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        postService.updatePost(id, title, excerpt, content, tagNames);
        return "redirect:/posts/" + id;
    }

}
