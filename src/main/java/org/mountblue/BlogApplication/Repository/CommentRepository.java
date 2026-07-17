package org.mountblue.BlogApplication.Repository;

import org.mountblue.BlogApplication.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

