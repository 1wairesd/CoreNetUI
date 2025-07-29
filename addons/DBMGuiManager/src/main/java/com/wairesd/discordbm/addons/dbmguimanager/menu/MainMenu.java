package com.wairesd.discordbm.addons.dbmguimanager.menu;

import com.jodexindustries.jguiwrapper.api.item.ItemWrapper;
import com.jodexindustries.jguiwrapper.gui.advanced.AdvancedGui;
import com.wairesd.discordbm.addons.dbmguimanager.DBMGuiManager;
import com.wairesd.discordbm.addons.dbmguimanager.menu.command.CommandListMenu;
import com.wairesd.discordbm.api.DBMAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.Plugin;


public class MainMenu extends AdvancedGui {

    private final Plugin plugin;
    private final DBMAPI api;
    private final DBMGuiManager guiManager;

    public MainMenu(DBMGuiManager guiManager, DBMAPI api) {
        super(27, "Простое меню");
        this.plugin = guiManager;
        this.api = api;
        this.guiManager = guiManager;
        initMenu();
    }

    private void initMenu() {
        registerItem("uptime", builder -> builder.slots(4)
                .defaultItem(
                        ItemWrapper.builder(Material.CLOCK)
                                .displayName("&eВремя работы клиента")
                                .lore("&7Аптайм:", "&f%uptime%")
                                .placeholderEngine(guiManager.getPlaceholderEngine())
                                .build()
                )
        );

        registerItem("menu", builder -> builder.slots(13)
                .defaultItem(
                        ItemWrapper.builder(Material.COMPASS)
                                .displayName("&bОткрыть другое меню")
                                .lore("&7Нажмите, чтобы открыть другое меню")
                                .build()
                )
                .defaultClickHandler((event, controller) -> {
                    HumanEntity player = event.getWhoClicked();

                    event.setCancelled(true);

                    player.sendMessage(LEGACY_AMPERSAND.deserialize("&aОткрывается меню команд!"));
                    close(player);
                    Bukkit.getScheduler().runTask(plugin, () -> new CommandListMenu(api).open(player));

                })
        );
    }
}
