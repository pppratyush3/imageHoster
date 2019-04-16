package ImageHoster.controller;

import ImageHoster.model.Comment;
import ImageHoster.model.Image;
import ImageHoster.model.Tag;
import ImageHoster.model.User;
import ImageHoster.service.CommentService;
import ImageHoster.service.ImageService;
import ImageHoster.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class ImageController {


    @Autowired
    private ImageService imageService;

    @Autowired
    private TagService tagService;

    //This method displays all the images in the user home page after successful login
    @RequestMapping("images")
    public String getUserImages(Model model) {
        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "images";
    }


    @RequestMapping("images/{imageId}/{title}")
    public String showImage(@PathVariable("imageId") Integer imageId, @PathVariable("title") String title, Model model){
        Image image = imageService.getImage(imageId);
        List<Comment> comments = image.getComments();
        model.addAttribute("image", image);
        model.addAttribute("tags", image.getTags());
        model.addAttribute("comments", comments);
        return "images/image";
    }
  @RequestMapping("/images/upload")
    public String newImage() {
        return "images/upload";
    }

    @RequestMapping(value = "/images/upload", method = RequestMethod.POST)
    public String createImage(@RequestParam("file") MultipartFile file, @RequestParam("tags") String tags, Image newImage, HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggeduser");
        newImage.setUser(user);
        String uploadedImageData = convertUploadedFileToBase64(file);
        newImage.setImageFile(uploadedImageData);

        List<Tag> imageTags = findOrCreateTags(tags);
        newImage.setTags(imageTags);
        newImage.setDate(new Date());
        imageService.uploadImage(newImage);
        return "redirect:/images";
    }

    @RequestMapping(value = "/editImage")
    public String editImage(@RequestParam("imageId") Integer imageId, Model model, HttpSession session) {
        Image image = imageService.getImage(imageId);
        User imageUser = (User)session.getAttribute("loggeduser");
        String error = "Only the owner of the image can edit the image";
        model.addAttribute("image", image);
        if(image.getUser().getId() != imageUser.getId()){

            model.addAttribute("editError", error);
            return "images/image";
        }
        String tags = convertTagsToString(image.getTags());
        model.addAttribute("tags", tags);

        return "images/edit";
    }


    @RequestMapping(value = "/editImage", method = RequestMethod.PUT)
    public String editImageSubmit(@RequestParam("file") MultipartFile file, @RequestParam("imageId") Integer imageId, @RequestParam("tags") String tags, Image updatedImage, HttpSession session) throws IOException {

        Image image = imageService.getImage(imageId);
        String updatedImageData = convertUploadedFileToBase64(file);
        List<Tag> imageTags = findOrCreateTags(tags);

        if (updatedImageData.isEmpty())
            updatedImage.setImageFile(image.getImageFile());
        else {
            updatedImage.setImageFile(updatedImageData);
        }

        updatedImage.setId(imageId);
        User user = (User) session.getAttribute("loggeduser");
        updatedImage.setUser(user);
        updatedImage.setTags(imageTags);
        updatedImage.setDate(new Date());

        imageService.updateImage(updatedImage);
        return "redirect:/images/" + updatedImage.getId() +"/" + updatedImage.getTitle();
    }

  // can they proceed to delete, else an error message is displayed.
    @RequestMapping(value = "/deleteImage", method = RequestMethod.DELETE)
    public String deleteImageSubmit(@RequestParam(name = "imageId") Integer imageId, HttpSession session, Model model) {
        User imageUser = (User)session.getAttribute("loggeduser");
        Image image = imageService.getImage(imageId);
        String error = "Only the owner of the image can delete the image";

        if (image.getUser().getId() != imageUser.getId()) {
            model.addAttribute("deleteError", error);
            model.addAttribute("image", image);
            return "images/image";
        }
        //imageService.deleteImage(imageId,imageUser);
        imageService.deleteImage(imageId);
        return "redirect:/images";
    }


    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    private List<Tag> findOrCreateTags(String tagNames) {
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        while (st.hasMoreTokens()) {
            String tagName = st.nextToken().trim();
            Tag tag = tagService.getTagByName(tagName);

            if (tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }
            tags.add(tag);
        }
        return tags;
    }



    private String convertTagsToString(List<Tag> tags) {
        StringBuilder tagString = new StringBuilder();

        for (int i = 0; i <= tags.size() - 2; i++) {
            tagString.append(tags.get(i).getName()).append(",");
        }

        Tag lastTag = tags.get(tags.size() - 1);
        tagString.append(lastTag.getName());

        return tagString.toString();
    }
}
