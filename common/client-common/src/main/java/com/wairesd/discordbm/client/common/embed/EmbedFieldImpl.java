package com.wairesd.discordbm.client.common.embed;

import com.wairesd.discordbm.api.embed.EmbedField;

public class EmbedFieldImpl implements EmbedField {
    
    private final String name;
    private final String value;
    private final boolean inline;
    
    private EmbedFieldImpl(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.inline = builder.inline;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean isInline() {
        return inline;
    }

    public static class Builder implements EmbedField.Builder {
        private String name;
        private String value;
        private boolean inline;
        
        @Override
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        @Override
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        
        @Override
        public Builder inline(boolean inline) {
            this.inline = inline;
            return this;
        }
        
        @Override
        public EmbedField build() {
            return new EmbedFieldImpl(this);
        }
    }
} 