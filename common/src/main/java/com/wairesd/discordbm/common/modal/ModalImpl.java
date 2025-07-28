package com.wairesd.discordbm.common.modal;

import com.wairesd.discordbm.api.modal.Modal;
import com.wairesd.discordbm.api.modal.ModalField;

import java.util.List;

public class ModalImpl implements Modal {
    
    private final String title;
    private final List<ModalField> fields;
    private final String customId;

    public ModalImpl(String title, List<ModalField> fields, String customId) {
        this.title = title;
        this.fields = fields;
        this.customId = customId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<ModalField> getFields() {
        return fields;
    }

    @Override
    public String getCustomId() {
        return customId;
    }
} 