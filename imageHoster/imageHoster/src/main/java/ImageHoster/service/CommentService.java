package ImageHoster.service;

import ImageHoster.model.Comment;
import ImageHoster.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    // This method calls the createComment() in the Repository class, to persist the added comment into the database
    public void createComment(Comment comment){
        commentRepository.createComment(comment);
    }

    // This method calls the getComments() in the Repository class, to get the list of comments for the particular image
    public List<Comment> getComments(Integer imageId) {
        return commentRepository.getComments(imageId);
    }
}
