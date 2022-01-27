package now.planit.Controllers;

import now.planit.Data.Repo.FacadeMySQL;
import now.planit.Data.Repo.MapperDB;
import now.planit.Data.Repo.ProjectRepo;
import now.planit.Domain.Models.Project;
import now.planit.Domain.Models.User;
import now.planit.Domain.Services.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import java.util.ArrayList;

@Controller
public class ProjectController {
    ProjectService projectService = new ProjectService(new FacadeMySQL(new ProjectRepo(new MapperDB())));
    User user;
    ArrayList<Project> projects = new ArrayList<>();
    String finish;

    @GetMapping("/myProjects")
    public String myProjects(WebRequest request,Model model) {
        updateProjects(request, model);
        return "project/myProjects";
    }

    @PostMapping("/createProject")
    public String createProject(WebRequest request, Model model) {
        user = (User) request.getAttribute("user", WebRequest.SCOPE_SESSION);
        //Database dont do empty String, so we validates here.
        if (request.getParameter("finish").equals("")){
            finish = null;
        } else{
            finish = request.getParameter("finish");
        }
        projectService.createProject(request.getParameter("name"),
                request.getParameter("start"),finish,
                Integer.parseInt(request.getParameter("budget")), user);
        return "redirect:/myProjects";
    }

    @GetMapping("/removeProject/{id}")
    public String deleteProject(@PathVariable(value = "id") String id) {
        if (user == null){
            return "login/login";
        }
        projectService.deleteProject(id, user);
        return "redirect:/myProjects";
    }

    public void updateProjects(WebRequest request, Model model){
        user = (User) request.getAttribute("user", WebRequest.SCOPE_SESSION);
        model.addAttribute("userName", user.getName());
        projects = projectService.getProjects(user);
        model.addAttribute("projects", projects);
    }
}