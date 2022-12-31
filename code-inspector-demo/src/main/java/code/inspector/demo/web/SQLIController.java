package code.inspector.demo.web;

import code.inspector.demo.model.User;
import code.inspector.demo.service.SQLIService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/sqli")
public class SQLIController {

    private final SQLIService sqliService;

    public SQLIController(SQLIService sqliService) {
        this.sqliService = sqliService;
    }

    @GetMapping("add")
    @ResponseBody
    public String addUser(String name, int age) {
        return sqliService.addUser(name, age);
    }

    @RequestMapping("/select")
    @ResponseBody
    public List<User> select(String name) {
        return sqliService.selectUser(name);
    }
}
