package com.wairesd.discordbm.api.embed;

import java.util.List;

/**
 * Interface representing a Discord embed
 */
public interface Embed {
    
    /**
     * Get the title of the embed
     * 
     * @return The title
     */
    String getTitle();
    
    /**
     * Get the description of the embed
     * 
     * @return The description
     */
    String getDescription();
    
    /**
     * Get the color of the embed
     * 
     * @return The color as an RGB integer
     */
    Integer getColor();
    
    /**
     * Get the fields of the embed
     * 
     * @return The fields
     */
    List<EmbedField> getFields();
    
    /**
     * Get the thumbnail URL of the embed
     * 
     * @return The thumbnail URL
     */
    String getThumbnailUrl();
    
    /**
     * Get the image URL of the embed
     * 
     * @return The image URL
     */
    String getImageUrl();
    
    /**
     * Get the footer text of the embed
     * 
     * @return The footer text
     */
    String getFooterText();
    
    /**
     * Get the footer icon URL of the embed
     * 
     * @return The footer icon URL
     */
    String getFooterIconUrl();
    
    /**
     * Check if the embed has a timestamp
     * 
     * @return True if the embed has a timestamp, false otherwise
     */
    boolean hasTimestamp();
} 