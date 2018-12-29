package sec.project.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    private final String admin_username = "admin"; // these credentials should be changed in production version
    private final String admin_password = "password"; // these credentials should be changed in production version
    
    @Autowired
    private SignupRepository signupRepository;
    
    @Autowired
    private HttpSession session;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @RequestMapping("*")
    public String defaultMapping(Model model) {
        signupRepository.save(new Signup("Adam", "Smith", "MyPrecious"));
        signupRepository.save(new Signup("John", "Brown", "MySeccret"));
        signupRepository.save(new Signup("Ben", "Kowalski", "AccessToken"));
        model.addAttribute("list", signupRepository.findAll());
        return "redirect:/main";
        
    }

    @RequestMapping("add")
    public String addUsersList(Model model) {
       model.addAttribute("list", signupRepository.findAll());
       model.addAttribute("num", signupRepository.count());
        return "add";
        
    }

    @RequestMapping("admin")
    public String adminForm(Model model) {
      
        return "admin";
    }
   
      @RequestMapping("default")
    public String defaultForm(Model model) {
      
        return "default";
    }
    
   @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String loadForm(Model model) {
        model.addAttribute("list", signupRepository.findAll());
        model.addAttribute("num", signupRepository.count());
        model.addAttribute("userId", session.getAttribute("userId"));
        return "main";
    }

    @RequestMapping(value = "/main", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String surname, @RequestParam String secret, Model model) {
        final String sql = "INSERT INTO Signup (name, surname, secret) VALUES ('" + name + "', '" + surname + "', '" + secret + "');";
        jdbcTemplate.execute(sql);
        
        Long userId = signupRepository.findAll().get(signupRepository.findAll().size() - 1).getId();

        model.addAttribute("newUserId", userId);
        session.setAttribute("userId", userId);
        return "done";
    }

    @RequestMapping(value = "/edit/{userId}", method = RequestMethod.GET)
    public String editForm(@PathVariable("userId") long id, Model model) {
            model.addAttribute("user", signupRepository.findOne(id));
        return "edit";}
     
    
    
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(@RequestParam String name, @RequestParam String surname, @RequestParam String secret, @RequestParam Long userId, Model model) {
        Signup s = signupRepository.findOne(userId);
        s.setName(name);
        s.setSurname(surname);
        s.setSecret(secret);
        signupRepository.save(s);
        return "redirect:/main";
    }
    
    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    public String adminLogin(@RequestParam String username, @RequestParam String password, Model model) {
        if (username.equals(admin_username) && password.equals(admin_password)) {
            return "redirect:/admin/panel";
        }
        return "redirect:/main";
    }
    
    @RequestMapping(value = "/admin/panel", method = RequestMethod.GET)
    public String adminPanel(Model model) {
        model.addAttribute("list", signupRepository.findAll());
        model.addAttribute("num", signupRepository.count());
        return "panel";
    }
    
    @RequestMapping(value = "/admin/empty_database", method = RequestMethod.POST)
    public String adminLogin(Model model) {
        signupRepository.deleteAll();
        return "redirect:/main";
    }
}
