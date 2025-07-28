package com.wairesd.discordbm.common.modal;

import com.wairesd.discordbm.api.modal.Modal;
import com.wairesd.discordbm.api.modal.ModalBuilder;
import com.wairesd.discordbm.api.modal.ModalField;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModalBuilderImpl implements ModalBuilder {
    
    private String title;
    private final List<ModalField> fields = new ArrayList<>();
    private String customId;

    @Override
    public ModalBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ModalBuilder addField(ModalField field) {
        this.fields.add(field);
        return this;
    }

    @Override
    public ModalBuilder setCustomId(String customId) {
        this.customId = customId;
        return this;
    }

    @Override
    public Modal build() {
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Form title is required");
        }
        
        if (fields.isEmpty()) {
            throw new IllegalStateException("Form must have at least one field");
        }
        
        if (customId == null || customId.isEmpty()) {
            customId = "modal_" + UUID.randomUUID();
        }
        
        return new ModalImpl(title, new ArrayList<>(fields), customId);
    }
} 