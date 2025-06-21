package com.wairesd.discordbm.client.common.component;

import com.wairesd.discordbm.api.component.Button;
import com.wairesd.discordbm.api.component.ButtonStyle;

public class ButtonImpl implements Button {
    
    private final String label;
    private final String customId;
    private final ButtonStyle style;
    private final String url;
    private final boolean disabled;
    
    private ButtonImpl(Builder builder) {
        this.label = builder.label;
        this.customId = builder.customId;
        this.style = builder.style;
        this.url = builder.url;
        this.disabled = builder.disabled;
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

    public static class Builder implements Button.Builder {
        private String label;
        private String customId;
        private ButtonStyle style = ButtonStyle.PRIMARY;
        private String url;
        private boolean disabled;
        
        @Override
        public Builder label(String label) {
            this.label = label;
            return this;
        }
        
        @Override
        public Builder customId(String customId) {
            this.customId = customId;
            return this;
        }
        
        @Override
        public Builder style(ButtonStyle style) {
            this.style = style;
            return this;
        }
        
        @Override
        public Builder url(String url) {
            this.url = url;
            return this;
        }
        
        @Override
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }
        
        @Override
        public Button build() {
            return new ButtonImpl(this);
        }
    }
} 