package code.inspector.demo.web;

import code.inspector.demo.service.DOSService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dos")
public class DOSController {

    private final DOSService dosService;

    public DOSController(DOSService dosService) {
        this.dosService = dosService;
    }


    @RequestMapping("/dos1")
    public String dos1(String a, String b) {
        return dosService.dos1(a, b);
    }

    @RequestMapping("/dos2")
    public String rce2(int a) {
        return dosService.dos2(a);
    }

    @RequestMapping("/dos3")
    public String dop3(int a) {
        return dosService.dos3(a);
    }

    @RequestMapping("/dos4")
    public String dop4(int a) {
        return dosService.dos4(a);
    }
}
