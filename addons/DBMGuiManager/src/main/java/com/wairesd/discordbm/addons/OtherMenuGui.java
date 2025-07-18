package com.wairesd.discordbm.addons;

import com.jodexindustries.jguiwrapper.gui.SimpleGui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.wairesd.discordbm.api.DiscordBMAPI;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Arrays;
import com.wairesd.discordbm.api.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherMenuGui extends SimpleGui {
    private final DiscordBMAPI api;
    private final int page;
    private final int pageSize;
    private final List<Command> commands;

    public OtherMenuGui(DiscordBMAPI api) {
        this(api, 0);
    }

    public OtherMenuGui(DiscordBMAPI api, int page) {
        super(calcSize(api, page), "Зарегистрированные команды");
        this.api = api;
        this.page = page;
        this.commands = api.getCommandRegistration().getRegisteredCommands();
        this.pageSize = holder().getInventory().getSize() - (hasNextPage() ? 1 : 0) - (hasPrevPage() ? 1 : 0);
        initMenu();
    }

    private static int calcSize(DiscordBMAPI api, int page) {
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
        int slot = 0;
        if (hasPrevPage()) slot = 1;
        for (int i = start; i < end; i++) {
            if (slot >= holder().getInventory().getSize() || (hasNextPage() && slot == 53)) break;
            Command cmd = commands.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a" + cmd.getName());
            meta.setLore(Arrays.asList("§7" + cmd.getDescription()));
            item.setItemMeta(meta);
            holder().getInventory().setItem(slot, item);
            slot++;
        }
        if (hasNextPage()) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName("§bСледующая страница");
            next.setItemMeta(meta);
            holder().getInventory().setItem(53, next);
        }
        if (hasPrevPage()) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.setDisplayName("§bПредыдущая страница");
            prev.setItemMeta(meta);
            holder().getInventory().setItem(45, prev);
        }
        setClickHandlers((event, g) -> {
            int raw = event.getRawSlot();
            Player player = (Player) event.getWhoClicked();
            if (hasNextPage() && raw == 53) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> new OtherMenuGui(api, page + 1).open(player), 1L);
            } else if (hasPrevPage() && raw == 45) {
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(getClass()), () -> new OtherMenuGui(api, page - 1).open(player), 1L);
            }
        }, 45, 53);
    }
} 