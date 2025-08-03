package com.wairesd.discordbm.common.embed;

import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.embed.EmbedBuilder;

public class EmbedBuilderImpl extends EmbedBuilder {
    
    private final EmbedImpl.Builder builder = new EmbedImpl.Builder();
    
    @Override
    public EmbedBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }
    
    @Override
    public EmbedBuilder setDescription(String description) {
        builder.setDescription(description);
        return this;
    }
    
    @Override
    public EmbedBuilder setColor(int color) {
        builder.setColor(color);
        return this;
    }
    
    @Override
    public EmbedBuilder setColor(String hexColor) {
        builder.setColor(hexColor);
        return this;
    }
    
    @Override
    public EmbedBuilder addField(String name, String value, boolean inline) {
        builder.addField(name, value, inline);
        return this;
    }
    
    @Override
    public EmbedBuilder setThumbnail(String url) {
        builder.setThumbnail(url);
        return this;
    }
    
    @Override
    public EmbedBuilder setImage(String url) {
        builder.setImage(url);
        return this;
    }
    
    @Override
    public EmbedBuilder setFooter(String text) {
        builder.setFooter(text);
        return this;
    }
    
    @Override
    public EmbedBuilder setFooter(String text, String iconUrl) {
        builder.setFooter(text, iconUrl);
        return this;
    }
    
    @Override
    public EmbedBuilder setTimestamp() {
        builder.setTimestamp();
        return this;
    }
    
    @Override
    public Embed build() {
        return builder.build();
    }
} 