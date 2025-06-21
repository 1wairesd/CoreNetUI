package com.wairesd.discordbm.common.component;

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

    public ButtonAdapter(ButtonDefinition internalButton) {
        this.internalButton = internalButton;
        this.apiButton = convertToApiButton(internalButton);
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
        
        return new ButtonImpl.Builder()
            .label(internalButton.label())
            .customId(internalButton.customId())
            .style(convertToApiStyle(internalButton.style()))
            .url(internalButton.url())
            .disabled(internalButton.disabled())
            .build();
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
} 