package com.wairesd.discordbm.addons.dbmguimanager.menu.command;

import com.jodexindustries.jguiwrapper.api.gui.GuiDataLoader;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import com.wairesd.discordbm.api.command.Command;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public record CommandDataLoader(List<Command> commands, int page) implements GuiDataLoader {

    @Override
    public void load(AdvancedGui gui, HumanEntity player) {

    }

    public boolean hasNextPage() {
        return (page + 1) * 52 < commands.size();
    }

    public boolean hasPrevPage() {
        return page > 0;
    }
}
