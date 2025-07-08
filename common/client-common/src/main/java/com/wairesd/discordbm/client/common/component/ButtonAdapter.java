package com.wairesd.discordbm.client.common.component;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.component.ButtonStyle;
import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;

public class ButtonAdapter implements Button {
    
    private final Button apiButton;
    private final ButtonDefinition internalButton;

    public ButtonAdapter(Button apiButton) {
        this.apiButton = apiButton;
        this.internalButton = convertToInternalButton(apiButton);
    }
    
    @Override
    public String getLabel() {
        return apiButton.getLabel();
    }
    
    @Override
    public String getCustomId() {
        return apiButton.getCustomId();
    }
    
    @Override
    public ButtonStyle getStyle() {
        return apiButton.getStyle();
    }
    
    @Override
    public String getUrl() {
        return apiButton.getUrl();
    }
    
    @Override
    public boolean isDisabled() {
        return apiButton.isDisabled();
    }

    public ButtonDefinition getInternalButton() {
        return internalButton;
    }

    private ButtonDefinition convertToInternalButton(Button apiButton) {
        if (apiButton == null) {
            return null;
        }
        
        return new ButtonDefinition.Builder()
            .label(apiButton.getLabel())
            .customId(apiButton.getCustomId())
            .style(convertToInternalStyle(apiButton.getStyle()))
            .url(apiButton.getUrl())
            .disabled(apiButton.isDisabled())
            .formName(apiButton instanceof ButtonWithForm ? ((ButtonWithForm) apiButton).getFormName() : null)
            .build();
    }

    private com.wairesd.discordbm.common.models.buttons.ButtonStyle convertToInternalStyle(ButtonStyle apiStyle) {
        if (apiStyle == null) {
            return com.wairesd.discordbm.common.models.buttons.ButtonStyle.PRIMARY;
        }
        
        switch (apiStyle) {
            case PRIMARY:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.PRIMARY;
            case SECONDARY:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.SECONDARY;
            case SUCCESS:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.SUCCESS;
            case DANGER:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.DANGER;
            case LINK:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.LINK;
            default:
                return com.wairesd.discordbm.common.models.buttons.ButtonStyle.PRIMARY;
        }
    }

    private Button convertToApiButton(ButtonDefinition internalButton) {
        if (internalButton == null) {
            return null;
        }
        
        ButtonImpl.Builder builder = new ButtonImpl.Builder()
            .label(internalButton.label())
            .customId(internalButton.customId())
            .style(convertToApiStyle(internalButton.style()))
            .url(internalButton.url())
            .disabled(internalButton.disabled());
        if (internalButton.formName() != null) {
            builder = builder.formName(internalButton.formName());
        }
        return builder.build();
    }

    private ButtonStyle convertToApiStyle(com.wairesd.discordbm.common.models.buttons.ButtonStyle internalStyle) {
        if (internalStyle == null) {
            return ButtonStyle.PRIMARY;
        }
        
        switch (internalStyle) {
            case PRIMARY:
                return ButtonStyle.PRIMARY;
            case SECONDARY:
                return ButtonStyle.SECONDARY;
            case SUCCESS:
                return ButtonStyle.SUCCESS;
            case DANGER:
                return ButtonStyle.DANGER;
            case LINK:
                return ButtonStyle.LINK;
            default:
                return ButtonStyle.PRIMARY;
        }
    }

    public static class ButtonImpl implements Button, ButtonWithForm {
        private final String label;
        private final String customId;
        private final ButtonStyle style;
        private final String url;
        private final boolean disabled;
        private final String formName;

        private ButtonImpl(Builder builder) {
            this.label = builder.label;
            this.customId = builder.customId;
            this.style = builder.style;
            this.url = builder.url;
            this.disabled = builder.disabled;
            this.formName = builder.formName;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public String getCustomId() {
            return customId;
        }

        @Override
        public ButtonStyle getStyle() {
            return style;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public boolean isDisabled() {
            return disabled;
        }

        @Override
        public String getFormName() {
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

            public ButtonImpl build() {
                return new ButtonImpl(this);
            }
        }
    }
} 