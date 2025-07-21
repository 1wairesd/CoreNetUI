package com.wairesd.discordbm.common.embed;

import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.embed.EmbedField;

import java.util.ArrayList;
import java.util.List;

public class EmbedImpl implements Embed {
    
    private final String title;
    private final String description;
    private final Integer color;
    private final List<EmbedField> fields;
    private final String thumbnailUrl;
    private final String imageUrl;
    private final String footerText;
    private final String footerIconUrl;
    private final boolean hasTimestamp;
    
    private EmbedImpl(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.color = builder.color;
        this.fields = builder.fields != null ? builder.fields : new ArrayList<>();
        this.thumbnailUrl = builder.thumbnailUrl;
        this.imageUrl = builder.imageUrl;
        this.footerText = builder.footerText;
        this.footerIconUrl = builder.footerIconUrl;
        this.hasTimestamp = builder.hasTimestamp;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public Integer getColor() {
        return color;
    }
    
    @Override
    public List<EmbedField> getFields() {
        return fields;
    }
    
    @Override
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    @Override
    public String getImageUrl() {
        return imageUrl;
    }
    
    @Override
    public String getFooterText() {
        return footerText;
    }
    
    @Override
    public String getFooterIconUrl() {
        return footerIconUrl;
    }
    
    @Override
    public boolean hasTimestamp() {
        return hasTimestamp;
    }

    public static class Builder implements com.wairesd.discordbm.api.embed.EmbedBuilder {
        private String title;
        private String description;
        private Integer color;
        private List<EmbedField> fields;
        private String thumbnailUrl;
        private String imageUrl;
        private String footerText;
        private String footerIconUrl;
        private boolean hasTimestamp;
        
        @Override
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        @Override
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        
        @Override
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }
        
        @Override
        public Builder setColor(String hexColor) {
            if (hexColor != null && hexColor.startsWith("#")) {
                try {
                    this.color = Integer.parseInt(hexColor.substring(1), 16);
                } catch (NumberFormatException e) {
                }
            }
            return this;
        }
        
        @Override
        public Builder addField(String name, String value, boolean inline) {
            if (fields == null) {
                fields = new ArrayList<>();
            }
            fields.add(new EmbedFieldImpl.Builder()
                    .name(name)
                    .value(value)
                    .inline(inline)
                    .build());
            return this;
        }
        
        @Override
        public Builder setThumbnail(String url) {
            this.thumbnailUrl = url;
            return this;
        }
        
        @Override
        public Builder setImage(String url) {
            this.imageUrl = url;
            return this;
        }
        
        @Override
        public Builder setFooter(String text) {
            this.footerText = text;
            return this;
        }
        
        @Override
        public Builder setFooter(String text, String iconUrl) {
            this.footerText = text;
            this.footerIconUrl = iconUrl;
            return this;
        }
        
        @Override
        public Builder setTimestamp() {
            this.hasTimestamp = true;
            return this;
        }

        public Builder setFields(List<EmbedField> fields) {
            this.fields = fields;
            return this;
        }
        
        @Override
        public Embed build() {
            return new EmbedImpl(this);
        }
    }
} 