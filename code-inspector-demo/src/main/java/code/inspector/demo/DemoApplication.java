package code.inspector.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RequestMapping(path = "/")
    public String index(String input) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        parser.parseExpression(input).getValue(evaluationContext);
        return "ok";
    }
}
