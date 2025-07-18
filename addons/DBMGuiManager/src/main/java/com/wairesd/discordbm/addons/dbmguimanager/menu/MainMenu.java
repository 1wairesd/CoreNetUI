package com.wairesd.discordbm.addons.dbmguimanager.menu;

import com.jodexindustries.jguiwrapper.gui.SimpleGui;
import com.wairesd.discordbm.api.DiscordBMAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;


public class MainMenu extends SimpleGui {
    private final DiscordBMAPI api;

    public MainMenu(DiscordBMAPI api) {
        super(27, "Простое меню");
        this.api = api;
        initMenu();
    }

    private String formatUptime(long uptimeMillis) {
        File configFile = new File(JavaPlugin.getProvidingPlugin(getClass()).getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        String format = config.getString("uptime-format", "short");
        long seconds = uptimeMillis / 1000 % 60;
        long minutes = uptimeMillis / (1000 * 60) % 60;
        long hours = uptimeMillis / (1000 * 60 * 60) % 24;
        long days = uptimeMillis / (1000 * 60 * 60 * 24);
        if (format.equalsIgnoreCase("long")) {
            return String.format("%d дней %d часов %d минут %d секунд", days, hours, minutes, seconds);
        } else {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        }
    }

    private void initMenu() {
        ItemStack uptimeItem = new ItemStack(Material.CLOCK);
        ItemMeta meta = uptimeItem.getItemMeta();
        long uptimeMillis = api.getUptimeMillis();
        String formatted = formatUptime(uptimeMillis);
        meta.setDisplayName("§eВремя работы клиента");
        meta.setLore(Arrays.asList("§7Аптайм:", "§f" + formatted));
        uptimeItem.setItemMeta(meta);
        holder().getInventory().setItem(4, uptimeItem);

        ItemStack menuItem = new ItemStack(Material.COMPASS);
        ItemMeta menuMeta = menuItem.getItemMeta();
        menuMeta.setDisplayName("§bОткрыть другое меню");
        menuMeta.setLore(Arrays.asList("§7Нажмите, чтобы открыть другое меню"));
        menuItem.setItemMeta(menuMeta);
        holder().getInventory().setItem(13, menuItem);

        setClickHandlers((event, g) -> {
            if (event.getRawSlot() == 13) {
                event.getWhoClicked().sendMessage("§aОткрывается меню команд!");
                if (event.getWhoClicked() instanceof org.bukkit.entity.Player) {
                    org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getWhoClicked();
                    player.closeInventory();
                    Bukkit.getScheduler().runTaskLater(
                        JavaPlugin.getProvidingPlugin(getClass()),
                        () -> new CommandListMenu(api).open(player),
                        1L
                    );
                }
            }
        }, 13);
    }
} 