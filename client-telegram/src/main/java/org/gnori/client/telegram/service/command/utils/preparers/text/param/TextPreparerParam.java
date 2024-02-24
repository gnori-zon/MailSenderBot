package org.gnori.client.telegram.service.command.utils.preparers.text.param;

public sealed interface TextPreparerParam permits PatternTextPreparerParam, SimpleTextPreparerParam, WithOptionalPartTextPreparerParam {
}
