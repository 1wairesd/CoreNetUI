package com.wairesd.discordbm.common.form;

import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.form.FormField;

import java.util.List;

public class FormImpl implements Form {
    
    private final String title;
    private final List<FormField> fields;
    private final String customId;

    public FormImpl(String title, List<FormField> fields, String customId) {
        this.title = title;
        this.fields = fields;
        this.customId = customId;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<FormField> getFields() {
        return fields;
    }

    @Override
    public String getCustomId() {
        return customId;
    }
} 