package ImageHoster.controller;

import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.model.UserProfile;
import ImageHoster.service.ImageService;
import ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @RequestMapping("users/registration")
    public String registration(Model model) {
        User user = new User();
        UserProfile profile = new UserProfile();
        user.setProfile(profile);
        model.addAttribute("User", user);
        return "users/registration";
    }

    // Check has been added to validate the strength of the password. Only if the password meets the required criteria will they be redirected to
    // the login page, else, an error message is displayed during registration and the user is redirected back to the registration page.
    @RequestMapping(value = "users/registration", method = RequestMethod.POST)
    public String registerUser(User user, Model model) {
        boolean passwordValid = validatePasswordStrength(user.getPassword());
        if (!passwordValid) {
            String error = "Password must contain atleast 1 alphabet, 1 number & 1 special character";
            model.addAttribute("passwordTypeError", error);
            user = new User();
            model.addAttribute("User",  user);
            return "users/registration";
        }
        else {
            userService.registerUser(user);
            return "users/login";
        }
    }

    @RequestMapping("users/login")
    public String login() {
        return "users/login";
    }

    @RequestMapping(value = "users/login", method = RequestMethod.POST)
    public String loginUser(User user, HttpSession session) {
        User existingUser = userService.login(user);
        if (existingUser != null) {
            session.setAttribute("loggeduser", existingUser);
            return "redirect:/images";
        } else {
            return "users/login";
        }
    }

    @RequestMapping(value = "users/logout", method = RequestMethod.POST)
    public String logout(Model model, HttpSession session) {
        session.invalidate();

        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "index";
    }

    // validates that the password entered by the user during registration
    private boolean validatePasswordStrength(String password) {
        return password.matches(".*[a-zA-Z]+.*") && password.matches(".*[0-9]+.*") && password.matches(".*[^a-zA-Z0-9]+.*");

    }
}
