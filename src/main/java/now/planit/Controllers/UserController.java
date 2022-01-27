package now.planit.Controllers;


import now.planit.Data.Repo.FacadeMySQL;
import now.planit.Data.Repo.MapperDB;
import now.planit.Data.Repo.UsersRepo;
import now.planit.Domain.Models.User;
import now.planit.Exceptions.UserEditException;
import now.planit.Exceptions.UserAllreadyExistException;
import now.planit.Domain.Services.UserService;
import now.planit.Exceptions.DBConnFailedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
  UserService userService = new UserService(new FacadeMySQL(new UsersRepo(new MapperDB())));
  User user;

  @GetMapping("/registerUser")
  public String createUser() {
    return "register/register";
  }

  @GetMapping("/logged")
  public String loggedIn() {
    return "login/logged";
  }

  @PostMapping("/register")
  public String register(WebRequest request) throws UserAllreadyExistException, DBConnFailedException{
      userService.registerUser(
          request.getParameter("name"),
          request.getParameter("email"),
          request.getParameter("password"));
      return "login/login";
  }

  @PostMapping("/validateLogin")
  public String validateLogin(WebRequest request, HttpSession session, Model model) {
    user = userService.validateLogin(
            request.getParameter("mail"),
            request.getParameter("password"));

    //Set Session to user, validate user is not null.
    if (session.getAttribute("user") == null) {
      if (user != null) {
        model.addAttribute("user", user);
        request.setAttribute("user", user, WebRequest.SCOPE_SESSION);
        userService.updateUserData(user);

        return "project/projectOverview";
      }
    }
    //Will pop-up with our javascript alert with rederering timer
    return "login/loginFailed";
  }

  @GetMapping("/logout")
  public String logOut(HttpSession session) {
    session.invalidate();
    user = null;
    return "redirect:/";
  }

  @GetMapping("/myPage")
  public String myPage(Model model) {
    model.addAttribute("user", user);
    return "login/myPage";
  }

  @PostMapping("/updateUser")
  public String updateUser(WebRequest request, Model model) throws UserEditException {
    userService.updateName(request.getParameter("name"), user);
    userService.updateEmail(request.getParameter("email"), user);
    userService.updatePassword(request.getParameter("password"), user);
    model.addAttribute("user", user);
    return "redirect:/myPage";
  }

  @ExceptionHandler(DBConnFailedException.class)
  public String exceptionMessageLogin(Model model){
    String message = "No connection to the database: -->Please contact support by pressing the \"Mail us\" button on this page!";//Test
    model.addAttribute("exMessage", message );
    return "error/error";
  }

  //Presentation
  @ExceptionHandler(UserAllreadyExistException.class)
  public String exceptionMessageUserNotExist(Model model){
    model.addAttribute("exMessage", "--->User allready exits. Please choose another email/username<--");
    return "error/error";
  }

  @ExceptionHandler(UserEditException.class)
  public String exceptionMessageEditException(Model model){
    model.addAttribute("exMessage", "--->Allready exits!! Please choose another.<--");
    return "error/error";
  }
}
