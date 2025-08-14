package com.wairesd.discordbm.host.common.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@DatabaseTable(tableName = "message_labels")
public class MessageLabelEntry {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(columnName = "label_key", index = true, canBeNull = false)
    private String labelKey;

    @DatabaseField(columnName = "channel_id", canBeNull = false)
    private String channelId;

    @DatabaseField(columnName = "message_id", canBeNull = false)
    private String messageId;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private Timestamp createdAt;
}


