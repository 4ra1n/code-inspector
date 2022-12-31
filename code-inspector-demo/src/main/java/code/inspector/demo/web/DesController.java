package code.inspector.demo.web;

import code.inspector.demo.model.Obj;
import code.inspector.demo.service.DesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DesController {

    private final DesService desService;

    public DesController(DesService desService){
        this.desService =desService;
    }

    @RequestMapping("/jdk")
    @ResponseBody
    public String jdk(Obj obj) {
        return desService.jdk(obj);
    }

    @RequestMapping
    @ResponseBody
    public String fastjson(Obj obj){
        return desService.fastjson(obj);
    }

    @RequestMapping
    @ResponseBody
    public String snakeyaml(Obj obj){
        return desService.yaml(obj);
    }

    @RequestMapping
    @ResponseBody
    public String jackson(Obj obj){
        return desService.jackson(obj);
    }

    @RequestMapping
    @ResponseBody
    public String hessian(Obj obj){
        return desService.hessian(obj);
    }

    @RequestMapping
    @ResponseBody
    public String xmlDecoder(Obj obj){
        return desService.xml(obj);
    }
}
