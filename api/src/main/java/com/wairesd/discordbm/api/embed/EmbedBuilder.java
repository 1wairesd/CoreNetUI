package com.wairesd.discordbm.api.embed;

/**
 * Abstract class for building Discord embeds
 */
public abstract class EmbedBuilder {
    
    /**
     * Set the title of the embed
     * 
     * @param title The title
     * @return This builder
     */
    public abstract EmbedBuilder setTitle(String title);
    
    /**
     * Set the description of the embed
     * 
     * @param description The description
     * @return This builder
     */
    public abstract EmbedBuilder setDescription(String description);
    
    /**
     * Set the color of the embed
     * 
     * @param color The color as an RGB integer
     * @return This builder
     */
    public abstract EmbedBuilder setColor(int color);
    
    /**
     * Set the color of the embed using a hex string
     * 
     * @param hexColor The color as a hex string (e.g. "#FF0000")
     * @return This builder
     */
    public abstract EmbedBuilder setColor(String hexColor);
    
    /**
     * Add a field to the embed
     * 
     * @param name The field name
     * @param value The field value
     * @param inline Whether the field should be inline
     * @return This builder
     */
    public abstract EmbedBuilder addField(String name, String value, boolean inline);
    
    /**
     * Set the thumbnail URL of the embed
     * 
     * @param url The thumbnail URL
     * @return This builder
     */
    public abstract EmbedBuilder setThumbnail(String url);
    
    /**
     * Set the image URL of the embed
     * 
     * @param url The image URL
     * @return This builder
     */
    public abstract EmbedBuilder setImage(String url);
    
    /**
     * Set the footer text of the embed
     * 
     * @param text The footer text
     * @return This builder
     */
    public abstract EmbedBuilder setFooter(String text);
    
    /**
     * Set the footer text and icon of the embed
     * 
     * @param text The footer text
     * @param iconUrl The footer icon URL
     * @return This builder
     */
    public abstract EmbedBuilder setFooter(String text, String iconUrl);
    
    /**
     * Set the timestamp of the embed to the current time
     * 
     * @return This builder
     */
    public abstract EmbedBuilder setTimestamp();
    
    /**
     * Build the embed
     * 
     * @return The built embed
     */
    public abstract Embed build();
} 