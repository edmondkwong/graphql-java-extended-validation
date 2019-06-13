package graphql.validation.directives.standardrules;

import graphql.GraphQLError;
import graphql.Scalars;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLInputType;
import graphql.validation.directives.AbstractDirectiveValidationRule;
import graphql.validation.rules.ValidationRuleEnvironment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

abstract class AbstractMinMaxRule extends AbstractDirectiveValidationRule {

    public AbstractMinMaxRule(String name) {
        super(name);
    }

    @Override
    public boolean appliesToType(GraphQLInputType inputType) {
        return isOneOfTheseTypes(inputType,
                Scalars.GraphQLByte,
                Scalars.GraphQLShort,
                Scalars.GraphQLInt,
                Scalars.GraphQLLong,
                Scalars.GraphQLBigDecimal,
                Scalars.GraphQLBigInteger,
                Scalars.GraphQLFloat
        );
    }


    @Override
    public List<GraphQLError> runValidation(ValidationRuleEnvironment ruleEnvironment) {
        Object argumentValue = ruleEnvironment.getFieldOrArgumentValue();
        //null values are valid
        if (argumentValue == null) {
            return Collections.emptyList();
        }

        GraphQLDirective directive = ruleEnvironment.getContextObject(GraphQLDirective.class);
        int value = getIntArg(directive, "value");

        boolean isOK;
        try {
            BigDecimal directiveBD = new BigDecimal(value);
            BigDecimal argBD = asBigDecimal(argumentValue);
            int comparisonResult = argBD.compareTo(directiveBD);
            isOK = isOK(comparisonResult);

        } catch (NumberFormatException nfe) {
            isOK = false;
        }


        if (!isOK) {
            return mkError(ruleEnvironment, directive, mkMessageParams(
                    "value", value,
                    "fieldOrArgumentValue", argumentValue));

        }
        return Collections.emptyList();
    }

    abstract protected boolean isOK(int comparisonResult);
}