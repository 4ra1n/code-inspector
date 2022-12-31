package code.inspector.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/url")
public class URLController {
    @RequestMapping("/redirect1")
    public void redirect1(HttpServletRequest request,
                          HttpServletResponse response) {
        try {
            String newUrl = request.getParameter("url");
            response.sendRedirect(newUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/redirect2")
    public String redirect2(String url) {
        return "redirect:" + url;
    }

    @RequestMapping("/redirect3")
    public ModelAndView redirect3(String url) {
        String redirect = "redirect://" + url;
        return new ModelAndView(redirect);
    }

}
