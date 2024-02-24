package org.gnori.client.telegram.service.command.utils.preparers.text;

import org.gnori.client.telegram.service.command.utils.preparers.text.param.TextPreparerParam;

@FunctionalInterface
public interface TextPreparer {
    String prepare(TextPreparerParam param);
}
