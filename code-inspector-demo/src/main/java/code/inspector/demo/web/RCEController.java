package code.inspector.demo.web;

import code.inspector.demo.model.Obj;
import code.inspector.demo.service.RCEService;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rce")
public class RCEController {

    private final RCEService rceService;

    public RCEController(RCEService rceService) {
        this.rceService = rceService;
    }

    @RequestMapping(path = "/")
    public String index(String input) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext evaluationContext = null;
        Expression expr = null;
        if (input.contains("test")) {
            evaluationContext = new StandardEvaluationContext();
        }else {
            evaluationContext = new SimpleEvaluationContext.Builder().build();
        }
        expr = parser.parseExpression(input);
        expr.getValue(evaluationContext);
        return "ok";
    }

    @RequestMapping("/rce1")
    public String rce1(String data) {
        return rceService.rce1(data);
    }

    @RequestMapping("/rce2")
    public String rce2(String data) {
        return rceService.rce2(data);
    }

    @RequestMapping("/rce3")
    public String rce3(String data) {
        return rceService.rce3(data);
    }

    @RequestMapping("/rce4")
    public String rce4(Obj obj) {
        return rceService.rce4(obj);
    }

    @RequestMapping("/rce5")
    public String rce5(String str) {
        return rceService.rce5(str);
    }
}
