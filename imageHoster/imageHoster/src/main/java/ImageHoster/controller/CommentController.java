package ImageHoster.controller;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.service.CommentService;
import ImageHoster.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
public class CommentController {

    @Autowired
    private ImageService imageService;

    @ Autowired
    private CommentService commentService;


    // This method allows the user to add comments to an image and post them. On hitting the submit button, the user will be able to see all the comments
    // added for the given image. This is done by redirecting the user to the showImages() in the ImageController class
    @RequestMapping(value = "/image/{imageId}/{title}/comments", method = RequestMethod.POST)
    public String addComment(@PathVariable(name = "imageId") Integer imageId, @PathVariable(name = "title") String title, @RequestParam(name = "comment") String comment, HttpSession session, Model model) {
        Comment newComment = new Comment();
        Image image = imageService.getImage(imageId);
        newComment.setImage(image);
        newComment.setDate(LocalDate.now());
        newComment.setText(comment);
        newComment.setUser((User) session.getAttribute("loggeduser"));
        commentService.createComment(newComment);
        image.setComments(commentService.getComments(imageId));
        model.addAttribute("image", image);
        return "redirect:/images/{imageId}/{title}";
    }

}