package org.gnori.client.telegram.service.command.utils.preparers.text;

import org.gnori.client.telegram.service.command.utils.preparers.text.param.PatternTextPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.SimpleTextPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.TextPreparerParam;
import org.gnori.client.telegram.service.command.utils.preparers.text.param.WithOptionalPartTextPreparerParam;
import org.springframework.stereotype.Component;

@Component
public class TextPreparerImpl implements TextPreparer {

    private static final String UNDEFINED_TYPE_PATTERN = "Undefined textPreparerParam: %s";

    @Override
    public String prepare(TextPreparerParam param) {

        if (param instanceof SimpleTextPreparerParam simpleParam) {
            return simpleParam.getText();

        } else if (param instanceof WithOptionalPartTextPreparerParam withOptionalPartParam) {
            return withOptionalPart(withOptionalPartParam);

        } else if (param instanceof PatternTextPreparerParam patternParam) {
            return patternParam.pattern().formatted(patternParam.params());
        }

        throw new IllegalStateException(UNDEFINED_TYPE_PATTERN.formatted(param.getClass()));
    }

    private String withOptionalPart(WithOptionalPartTextPreparerParam param) {

        final String prefix = param.text();
        final String suffix = param.includeOptionalPart()
                ? param.optionalPart()
                : "";

        return "%s%s".formatted(prefix, suffix);
    }
}
