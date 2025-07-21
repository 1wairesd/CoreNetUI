package com.wairesd.discordbm.common.form;

import com.wairesd.discordbm.api.form.Form;
import com.wairesd.discordbm.api.form.FormBuilder;
import com.wairesd.discordbm.api.form.FormField;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FormBuilderImpl implements FormBuilder {
    
    private String title;
    private final List<FormField> fields = new ArrayList<>();
    private String customId;

    @Override
    public FormBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public FormBuilder addField(FormField field) {
        this.fields.add(field);
        return this;
    }

    @Override
    public FormBuilder setCustomId(String customId) {
        this.customId = customId;
        return this;
    }

    @Override
    public Form build() {
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Form title is required");
        }
        
        if (fields.isEmpty()) {
            throw new IllegalStateException("Form must have at least one field");
        }
        
        if (customId == null || customId.isEmpty()) {
            customId = "form_" + UUID.randomUUID();
        }
        
        return new FormImpl(title, new ArrayList<>(fields), customId);
    }
} 