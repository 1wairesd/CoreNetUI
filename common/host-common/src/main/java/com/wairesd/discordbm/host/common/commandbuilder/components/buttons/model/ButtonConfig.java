package com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model;

public class ButtonConfig {
    private final String label;
    private final String targetPage;

    public ButtonConfig(String label, String targetPage) {
        this.label = label;
        this.targetPage = targetPage;
    }

    public String getLabel() {
        return label;
    }

    public String getTargetPage() {
        return targetPage;
    }
}