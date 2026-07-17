package org.mountblue.BlogApplication.Service;

import org.mountblue.BlogApplication.Entity.Post;
import org.mountblue.BlogApplication.Entity.Tag;
import org.mountblue.BlogApplication.Repository.PostRepository;
import org.mountblue.BlogApplication.Repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    public void savePost(String title, String excerpt, Set<String> tagNames, String content) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authorName = auth.getName();
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(authorName);
        post.setPublished(true);
        post.setExcerpt(excerpt);
        post.setPublishedAt();
        Set<Tag> tags = new HashSet<>();

        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(name);
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }
        post.setTags(tags);

        postRepository.save(post);
    }
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void deletePost(Long id) {
        Optional<Post> post = postRepository.findById(id);

        post.get().getTags().clear();
        postRepository.deleteById(id);
    }
    public void updatePost(Long id, String title, String excerpt, String content, Set<String> tagNames) {
        Post post = getPost(id);
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);

        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name).orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(name);
                return tagRepository.save(newTag);
            });
            tags.add(tag);
        }
        post.setTags(tags);

        postRepository.save(post);
    }

public Page<Post> getPosts(
        String search,
        String sort,
        List<String> authors,
        List<String> tags,
        String publishedDate,
        int page
) {
    Sort sorting = sort.equals("oldest")
            ? Sort.by("publishedAt").ascending()
            : Sort.by("publishedAt").descending();

    Pageable pageable = PageRequest.of(page, 10, sorting);

    LocalDateTime fromDate;

    switch (publishedDate) {
        case "last1":
            fromDate = LocalDateTime.now().minusDays(1);
            break;
        case "last7":
            fromDate = LocalDateTime.now().minusDays(7);
            break;
        case "last30":
            fromDate = LocalDateTime.now().minusDays(30);
            break;
        case "thisYear":
            fromDate = LocalDateTime.now().withDayOfYear(1);
            break;
        default:
            fromDate = LocalDateTime.of(2025, 1, 1, 0, 0);
    }

    return postRepository.filterPosts(
            search == null ? "" : search,
            (authors == null || authors.isEmpty()) ? null : authors,
            (tags == null || tags.isEmpty()) ? null : tags,
            fromDate,
            pageable
    );
}

    public List<String> getAllAuthors() {
        return postRepository.findDistinctAuthors();
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

}
