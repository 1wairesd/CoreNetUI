package com.wairesd.discordbm.addons.dbmguimanager.menu.command;

import com.jodexindustries.jguiwrapper.api.gui.handler.item.HandlerContext;
import com.jodexindustries.jguiwrapper.api.gui.handler.item.ItemHandler;
import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.gui.advanced.GuiItemController;
import com.wairesd.discordbm.api.command.Command;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandItemHandler implements ItemHandler<CommandDataLoader> {

    @Override
    public void load(@NotNull CommandDataLoader loader, @NotNull GuiItemController controller, @NotNull HandlerContext context) {
        int start = loader.page() * 52;
        int end = Math.min(start + 52, loader.commands().size());
        int slot = loader.hasPrevPage() ? 1 : 0;
        for (int i = start; i < end; i++) {
            final Command cmd = loader.commands().get(i);

            controller.addSlot(slot);
            controller.setItem(slot, ItemWrapper.builder(Material.PAPER)
                    .displayName("&a" + cmd.getName())
                    .lore("&7" + cmd.getDescription())
                    .build());

//             TODO execute commands
            controller.setClickHandler(slot, (event, gui) -> {
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                player.performCommand(cmd.getName());
            });

            slot++;
            if (slot >= controller.gui().size() || (loader.hasNextPage() && slot == 53)) break;
        }
    }
}
