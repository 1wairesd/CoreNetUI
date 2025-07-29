package com.wairesd.discordbm.addons.dbmguimanager.menu;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import com.wairesd.discordbm.api.DBMAPI;
import com.wairesd.discordbm.api.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
public class CommandListMenu extends AdvancedGui {
    private final DBMAPI api;
    private final int page;
    private final List<Command> commands;
    private final int pageSize;
    private final JavaPlugin plugin;

    public CommandListMenu(DBMAPI api) {
        this(api, 0);
    }

    public CommandListMenu(DBMAPI api, int page) {
        super(calcSize(api, page), "Зарегистрированные команды");
        this.api = api;
        this.page = page;
        this.commands = api.getCommandRegistration().getRegisteredCommands();
        this.pageSize = holder().getInventory().getSize() - (hasNextPage() ? 1 : 0) - (hasPrevPage() ? 1 : 0);
        this.plugin = JavaPlugin.getProvidingPlugin(getClass());
        initMenu();
    }

    private static int calcSize(DBMAPI api, int page) {
        int count = api.getCommandRegistration().getRegisteredCommands().size();
        int maxPage = (count - 1) / 52;
        if (page < maxPage) return 54;
        if (page > 0) return 54;
        int left = count - page * 52;
        int size = ((left - 1) / 9 + 1) * 9;
        return Math.max(9, Math.min(size, 54));
    }

    private boolean hasNextPage() {
        return (page + 1) * 52 < commands.size();
    }

    private boolean hasPrevPage() {
        return page > 0;
    }

    private void initMenu() {
        int start = page * 52;
        int end = Math.min(start + 52, commands.size());
        int slot = hasPrevPage() ? 1 : 0;
        for (int i = start; i < end; i++) {
            final Command cmd = commands.get(i);
            final int currentSlot = slot;
            registerItem("cmd_" + i, builder -> {
                builder.slots(currentSlot)
                        .defaultItem(ItemWrapper.builder(Material.PAPER)
                                .displayName("&a" + cmd.getName())
                                .lore("&7" + cmd.getDescription())
                                .build()
                        );
            });
            slot++;
            if (slot >= holder().getInventory().getSize() || (hasNextPage() && slot == 53)) break;
        }
        if (hasNextPage()) {
            registerItem("next_page", builder -> {
                builder.slots(53)
                        .defaultItem(ItemWrapper.builder(Material.ARROW)
                                .displayName("&bСледующая страница")
                                .build())
                        .defaultClickHandler((event, controller) -> {
                            HumanEntity player = event.getWhoClicked();
                            event.setCancelled(true);
                            player.closeInventory();
                            Bukkit.getScheduler().runTask(plugin, () -> new CommandListMenu(api, page + 1).open(player));
                        });
            });
        }
        if (hasPrevPage()) {
            registerItem("prev_page", builder -> {
                builder.slots(45)
                        .defaultItem(ItemWrapper.builder(Material.ARROW)
                                .displayName("&bПредыдущая страница")
                                .build())
                        .defaultClickHandler((event, controller) -> {
                            HumanEntity player = event.getWhoClicked();
                            event.setCancelled(true);
                            player.closeInventory();
                            Bukkit.getScheduler().runTask(plugin, () -> new CommandListMenu(api, page - 1).open(player));
                        });
            });
        }
    }
}
