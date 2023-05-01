package org.gnori.store.entity.enums;

public enum StateMessage {
    QUEUE("⏳в очереди"),
    SUCCESS("✔отправлено"),
    FAIL("❌не отправлено");
    private final String text;

    StateMessage(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
