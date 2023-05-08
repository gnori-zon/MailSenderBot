package org.gnori.store.entity.enums;

public enum StateMessage {
    QUEUE("⏳in the queue"),
    SUCCESS("✔sent"),
    FAIL("❌not sent");
    private final String text;

    StateMessage(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
