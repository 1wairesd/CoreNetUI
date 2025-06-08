package com.wairesd.discordbm.velocity.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@DatabaseTable(tableName = "ip_blocks")
public class IpBlockEntry {

    @DatabaseField(id = true)
    private String ip;

    @DatabaseField(defaultValue = "0")
    private int attempts;

    @DatabaseField(columnName = "block_until")
    private Timestamp blockUntil;

    @DatabaseField(columnName = "current_block_time", defaultValue = "0")
    private long currentBlockTime;
}
