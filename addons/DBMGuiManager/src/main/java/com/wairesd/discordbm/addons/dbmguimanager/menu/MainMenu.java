package com.wairesd.discordbm.addons.dbmguimanager.menu;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import com.wairesd.discordbm.api.DiscordBMAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;

import java.util.List;


public class MainMenu extends AdvancedGui {

    private final Plugin plugin;
    private final DiscordBMAPI api;

    public MainMenu(Plugin plugin, DiscordBMAPI api) {
        super(27, "Простое меню");
        this.plugin = plugin;
        this.api = api;
        initMenu();
    }

    private String formatUptime(long uptimeMillis) {
        String format = plugin.getConfig().getString("uptime-format", "short");
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
        registerItem("uptime", builder -> {
            builder.slots(4)
                    .defaultItem(
                            ItemWrapper.builder(Material.CLOCK)
                                    .displayName("&eВремя работы клиента")
                                    .lore(List.of(
                                            LEGACY_AMPERSAND.deserialize("&7Аптайм:"),
                                            LEGACY_AMPERSAND.deserialize("&f" + formatUptime(api.getUptimeMillis()))
                                    ))
                                    .build()
                    );
        });

        registerItem("menu", builder -> {
            builder.slots(13)
                    .defaultItem(ItemWrapper.builder(Material.COMPASS)
                            .displayName("&bОткрыть другое меню")
                            .lore(List.of(LEGACY_AMPERSAND.deserialize("&7Нажмите, чтобы открыть другое меню")))
                            .build())
                    .defaultClickHandler((event, controller) -> {
                        HumanEntity player = event.getWhoClicked();

                        event.setCancelled(true);

                        player.sendMessage("§aОткрывается меню команд!");
                        player.closeInventory();
                        Bukkit.getScheduler().runTask(plugin, () -> new CommandListMenu(api).open(player));

                    })
            ;
        });
    }
}
