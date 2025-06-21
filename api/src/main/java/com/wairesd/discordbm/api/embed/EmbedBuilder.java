package com.wairesd.discordbm.api.embed;

import java.util.List;

/**
 * Interface for building Discord embeds
 */
public interface EmbedBuilder {
    
    /**
     * Set the title of the embed
     * 
     * @param title The title
     * @return This builder
     */
    EmbedBuilder setTitle(String title);
    
    /**
     * Set the description of the embed
     * 
     * @param description The description
     * @return This builder
     */
    EmbedBuilder setDescription(String description);
    
    /**
     * Set the color of the embed
     * 
     * @param color The color as an RGB integer
     * @return This builder
     */
    EmbedBuilder setColor(int color);
    
    /**
     * Set the color of the embed using a hex string
     * 
     * @param hexColor The color as a hex string (e.g. "#FF0000")
     * @return This builder
     */
    EmbedBuilder setColor(String hexColor);
    
    /**
     * Add a field to the embed
     * 
     * @param name The field name
     * @param value The field value
     * @param inline Whether the field should be inline
     * @return This builder
     */
    EmbedBuilder addField(String name, String value, boolean inline);
    
    /**
     * Set the thumbnail URL of the embed
     * 
     * @param url The thumbnail URL
     * @return This builder
     */
    EmbedBuilder setThumbnail(String url);
    
    /**
     * Set the image URL of the embed
     * 
     * @param url The image URL
     * @return This builder
     */
    EmbedBuilder setImage(String url);
    
    /**
     * Set the footer text of the embed
     * 
     * @param text The footer text
     * @return This builder
     */
    EmbedBuilder setFooter(String text);
    
    /**
     * Set the footer text and icon of the embed
     * 
     * @param text The footer text
     * @param iconUrl The footer icon URL
     * @return This builder
     */
    EmbedBuilder setFooter(String text, String iconUrl);
    
    /**
     * Set the timestamp of the embed to the current time
     * 
     * @return This builder
     */
    EmbedBuilder setTimestamp();
    
    /**
     * Build the embed
     * 
     * @return The built embed
     */
    Embed build();
} 