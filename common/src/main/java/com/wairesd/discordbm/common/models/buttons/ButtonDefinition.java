package com.wairesd.discordbm.common.models.buttons;

public class ButtonDefinition {
    private final String label;
    private final String customId;
    private final ButtonStyle style;
    private final String url;
    private final boolean disabled;
    private final String formName;

    private ButtonDefinition(Builder builder) {
        this.label = builder.label;
        this.customId = builder.customId;
        this.style = builder.style;
        this.url = builder.url;
        this.disabled = builder.disabled;
        this.formName = builder.formName;
    }

    public String label() {
        return label;
    }

    public String customId() {
        return customId;
    }

    public ButtonStyle style() {
        return style;
    }

    public String url() {
        return url;
    }

    public boolean disabled() {
        return disabled;
    }

    public String formName() {
        return formName;
    }

    public static class Builder {
        private String label;
        private String customId;
        private ButtonStyle style;
        private String url;
        private boolean disabled;
        private String formName;

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder customId(String customId) {
            this.customId = customId;
            return this;
        }

        public Builder style(ButtonStyle style) {
            this.style = style;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder formName(String formName) {
            this.formName = formName;
            return this;
        }

        public ButtonDefinition build() {
            return new ButtonDefinition(this);
        }
    }
}
