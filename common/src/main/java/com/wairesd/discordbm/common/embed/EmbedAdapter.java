package com.wairesd.discordbm.common.embed;

import com.wairesd.discordbm.api.embed.Embed;
import com.wairesd.discordbm.api.embed.EmbedField;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedFieldd;

import java.util.List;
import java.util.stream.Collectors;

public class EmbedAdapter implements Embed {
    
    private final Embed apiEmbed;
    private final EmbedDefinition internalEmbed;

    public EmbedAdapter(Embed apiEmbed) {
        this.apiEmbed = apiEmbed;
        this.internalEmbed = convertToInternalEmbed(apiEmbed);
    }

    public EmbedAdapter(EmbedDefinition internalEmbed) {
        this.internalEmbed = internalEmbed;
        this.apiEmbed = convertToApiEmbed(internalEmbed);
    }
    
    @Override
    public String getTitle() {
        return apiEmbed.getTitle();
    }
    
    @Override
    public String getDescription() {
        return apiEmbed.getDescription();
    }
    
    @Override
    public Integer getColor() {
        return apiEmbed.getColor();
    }
    
    @Override
    public List<EmbedField> getFields() {
        return apiEmbed.getFields();
    }
    
    @Override
    public String getThumbnailUrl() {
        return apiEmbed.getThumbnailUrl();
    }
    
    @Override
    public String getImageUrl() {
        return apiEmbed.getImageUrl();
    }
    
    @Override
    public String getFooterText() {
        return apiEmbed.getFooterText();
    }
    
    @Override
    public String getFooterIconUrl() {
        return apiEmbed.getFooterIconUrl();
    }
    
    @Override
    public boolean hasTimestamp() {
        return apiEmbed.hasTimestamp();
    }

    public EmbedDefinition getInternalEmbed() {
        return internalEmbed;
    }

    private EmbedDefinition convertToInternalEmbed(Embed apiEmbed) {
        if (apiEmbed == null) {
            return null;
        }
        
        List<EmbedFieldd> fields = null;
        if (apiEmbed.getFields() != null && !apiEmbed.getFields().isEmpty()) {
            fields = apiEmbed.getFields().stream()
                .map(this::convertToInternalField)
                .collect(Collectors.toList());
        }
        
        return new EmbedDefinition.Builder()
            .title(apiEmbed.getTitle())
            .description(apiEmbed.getDescription())
            .color(apiEmbed.getColor())
            .fields(fields)
            .build();
    }

    private EmbedFieldd convertToInternalField(EmbedField apiField) {
        return new EmbedFieldd.Builder()
            .name(apiField.getName())
            .value(apiField.getValue())
            .inline(apiField.isInline())
            .build();
    }

    private Embed convertToApiEmbed(EmbedDefinition internalEmbed) {
        if (internalEmbed == null) {
            return null;
        }
        
        List<EmbedField> fields = null;
        if (internalEmbed.fields() != null && !internalEmbed.fields().isEmpty()) {
            fields = internalEmbed.fields().stream()
                .map(this::convertToApiField)
                .collect(Collectors.toList());
        }
        
        return new EmbedImpl.Builder()
            .setTitle(internalEmbed.title())
            .setDescription(internalEmbed.description())
            .setColor(internalEmbed.color())
            .setFields(fields)
            .build();
    }

    private EmbedField convertToApiField(EmbedFieldd internalField) {
        return new EmbedFieldImpl.Builder()
            .name(internalField.name())
            .value(internalField.value())
            .inline(internalField.inline())
            .build();
    }
} 