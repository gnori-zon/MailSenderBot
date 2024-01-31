package org.gnori.client.telegram.utils.preparers.button.data.callback;

import org.gnori.client.telegram.command.commands.callback.CallbackCommandType;
import org.gnori.client.telegram.command.commands.callback.impl.back.menustep.MenuStepCommandType;
import org.gnori.client.telegram.service.impl.bot.model.CallbackButtonData;
import org.gnori.client.telegram.utils.preparers.button.data.ButtonDataPreparer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CallbackButtonDataPreparer implements ButtonDataPreparer<CallbackButtonData, CallbackButtonDataPreparerParam> {

    @Override
    public List<CallbackButtonData> prepare(CallbackButtonDataPreparerParam param) {

        final List<CallbackButtonData> callbackButtonDataList = Optional.of(param.type())
                .map(type -> switch (type) {

                    case SELECT_START_MENU_ITEM -> getSelectStartMenuItemCallbackButtonDataList();
                    case SELECT_PROFILE_INFO_ITEM -> getSelectProfileInfoItemCallbackButtonDataList();
                    case SELECT_ACTION_MAILING_ITEM -> getSelectActionMailingItemCallbackButtonDataList();
                    case SELECT_CHANGE_MESSAGE_ITEM -> getSelectChangeMessageItemCallbackButtonDataList();
                    case SELECT_SEND_MESSAGE_MODE -> getSelectSendMessageModeCallbackButtonDataList(param.hasCurrentMail());
                })
                .orElseGet(ArrayList::new);

        if (param.withBack()) {
            callbackButtonDataList.add(getBackCallbackButtonData(param.menuStep()));
        }

        return callbackButtonDataList.stream().toList();
    }

    private List<CallbackButtonData> getSelectStartMenuItemCallbackButtonDataList() {

        return new ArrayList<>(List.of(
                new CallbackButtonData(
                        "📃Mailings history",
                        CallbackCommandType.MAILING_HISTORY.name()
                ),
                new CallbackButtonData(
                        "📧Create mailing",
                        CallbackCommandType.CREATE_MAILING.name()
                ),
                new CallbackButtonData(
                        "⚙Profile",
                        CallbackCommandType.PROFILE_INFO.name()
                )
        ));
    }

    private List<CallbackButtonData> getSelectProfileInfoItemCallbackButtonDataList() {

        return new ArrayList<>(List.of(
                new CallbackButtonData(
                        "📮Change mail",
                        CallbackCommandType.CHANGE_MAIL.name()
                ),
                new CallbackButtonData(
                        "🔑Change key",
                        CallbackCommandType.CHANGE_KEY.name()
                ),
                new CallbackButtonData(
                        "❔Help",
                        CallbackCommandType.HELP.name()
                )
        ));
    }

    private List<CallbackButtonData> getSelectActionMailingItemCallbackButtonDataList() {

        return new ArrayList<>(List.of(
                new CallbackButtonData(
                        "🧼Clear message",
                        CallbackCommandType.CLEAR_MESSAGE.name()
                ),
                new CallbackButtonData(
                        "📩Upload message",
                        CallbackCommandType.UPLOAD_MESSAGE.name()
                ),
                new CallbackButtonData(
                        "📝Change item",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM.name()
                ),
                new CallbackButtonData(
                        "✈Send",
                        CallbackCommandType.SEND.name()
                )
        ));
    }

    private List<CallbackButtonData> getSelectChangeMessageItemCallbackButtonDataList() {

        return new ArrayList<>(List.of(
                new CallbackButtonData(
                        "Title",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_TITLE.name()
                ),
                new CallbackButtonData(
                        "Content",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_TEXT.name()
                ),
                new CallbackButtonData(
                        "Attachment",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_ANNEX.name()
                ),
                new CallbackButtonData(
                        "Recipients",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_RECIPIENTS.name()
                ),
                new CallbackButtonData(
                        "Number of pcs for each",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_COUNT_FOR_RECIPIENTS.name()
                ),
                new CallbackButtonData(
                        "Date of mailing",
                        CallbackCommandType.CHANGE_MESSAGE_ITEM_SENT_DATE.name()
                )
        ));
    }

    private List<CallbackButtonData> getSelectSendMessageModeCallbackButtonDataList(boolean hasCurrentMail) {

        final List<CallbackButtonData> callbackButtonDataList = new ArrayList<>();
        callbackButtonDataList.add(
                new CallbackButtonData(
                        "👽Send anonymously",
                        CallbackCommandType.SEND_ANONYMOUSLY.name()
                )
        );

        if (!hasCurrentMail) {

            callbackButtonDataList.add(
                    new CallbackButtonData(
                            "👁Send with your mail",
                            CallbackCommandType.SEND_CURRENT_MAIL.name()
                    )
            );
        }

        return callbackButtonDataList;
    }

    private CallbackButtonData getBackCallbackButtonData(MenuStepCommandType menuStep) {

        return new CallbackButtonData(
                "Back",
                "%s%s%s".formatted(CallbackCommandType.BACK, CallbackCommandType.DATA_DELIMITER, menuStep.name())
        );
    }
}
