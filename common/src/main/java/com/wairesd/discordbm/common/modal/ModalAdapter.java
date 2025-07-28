package com.wairesd.discordbm.common.modal;

import com.wairesd.discordbm.api.modal.Modal;
import com.wairesd.discordbm.api.modal.ModalField;
import com.wairesd.discordbm.common.models.modal.ModalDefinition;
import com.wairesd.discordbm.common.models.modal.ModalFieldDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class ModalAdapter {
    
    private final Modal modal;
    
    public ModalAdapter(Modal modal) {
        this.modal = modal;
    }
    
    public ModalDefinition getInternalModal() {
        List<ModalFieldDefinition> fieldDefs = modal.getFields().stream()
                .map(this::convertToFieldDefinition)
                .collect(Collectors.toList());
        
        return new ModalDefinition.Builder()
                .title(modal.getTitle())
                .customId(modal.getCustomId())
                .fields(fieldDefs)
                .build();
    }
    
    private ModalFieldDefinition convertToFieldDefinition(ModalField field) {
        return new ModalFieldDefinition.Builder()
                .label(field.getLabel())
                .placeholder(field.getPlaceholder())
                .type(field.getType())
                .required(field.isRequired())
                .variable(field.getVariable())
                .build();
    }
} 