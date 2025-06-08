package com.wairesd.discordbm.api.event.plugin;

import com.wairesd.discordbm.api.event.DiscordBMEvent;

public class DiscordBMReloadEvent extends DiscordBMEvent {
    private final Type type;

    public DiscordBMReloadEvent(Type type) {
        this.type = type;
    }

    public Type type() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof DiscordBMReloadEvent)) return false;
        DiscordBMReloadEvent other = (DiscordBMReloadEvent) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        Object this$type = this.type();
        Object other$type = other.type();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof DiscordBMReloadEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        Object $type = this.type();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DiscordBMReloadEvent(type=" + this.type() + ")";
    }

    public enum Type {
        CONFIG,    // Rebooting the configuration
        NETTY,     // Rebooting the network connection
        COMMANDS   // Reloading Commands
    }
}
