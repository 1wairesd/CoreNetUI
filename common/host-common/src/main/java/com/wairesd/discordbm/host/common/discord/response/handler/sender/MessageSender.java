package com.wairesd.discordbm.host.common.discord.response.handler.sender;

import com.wairesd.discordbm.common.models.buttons.ButtonDefinition;
import com.wairesd.discordbm.common.models.embed.EmbedDefinition;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.discord.DiscordBotListener;
import com.wairesd.discordbm.host.common.discord.response.handler.sender.option.Channel;
import com.wairesd.discordbm.host.common.discord.response.handler.sender.option.Direct;
import com.wairesd.discordbm.host.common.discord.response.handler.sender.option.Embed;
import com.wairesd.discordbm.host.common.discord.response.handler.sender.option.WithHook;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class MessageSender {
    private static  DiscordBMHPlatformManager platformManager;
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static DiscordBotListener listener;

    public static void sendChannelMessage(ResponseMessage respMsg) {
        Channel.sendChannelMessage(respMsg);
    }

    public static void sendDirectMessage(ResponseMessage respMsg) {
        Direct.sendDirectMessage(respMsg);
    }

    public static void sendResponseWithHook(InteractionHook hook, ResponseMessage respMsg) {
        WithHook.sendResponseWithHook(hook, respMsg);
    }

    public static void sendEmbed(SlashCommandInteractionEvent event, EmbedDefinition embedDef, List<ButtonDefinition> buttons, UUID requestId, boolean ephemeral) {
        Embed.sendEmbed(event, embedDef, buttons, requestId, ephemeral);
    }
}
