package lamp.agent.genie.spring.boot.base.impl;

import lamp.agent.genie.utils.ExpressionParser;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;


public class SpringExpressionParser implements ExpressionParser{

	private org.springframework.expression.ExpressionParser parser = new SpelExpressionParser();

	public String getValue(String value, Map<String, Object> parameters) {
		if (value == null) {
			return null;
		}
		Expression expression = parser.parseExpression(value, new TemplateParserContext("${", "}"));
		StandardEvaluationContext evaluationContext = new StandardEvaluationContext(parameters);
		evaluationContext.addPropertyAccessor(new MapAccessor());
		return expression.getValue(evaluationContext, String.class);
	}

}
