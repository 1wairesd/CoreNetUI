package com.wairesd.discordbm.host.common.discord.response.handler.sender.option;

import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.utils.Components;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Channel {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static DiscordBMHPlatformManager platformManager;

    public static void sendChannelMessage(ResponseMessage respMsg) {
        String channelId = respMsg.channelId();
        if (channelId == null) {
            logger.error("No channelId provided for channel_message");
            return;
        }
        var jda = platformManager.getDiscordBotManager().getJda();
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            logger.error("Channel with ID {} not found for channel_message", channelId);
            return;
        }
        var msgAction = respMsg.response() != null ? channel.sendMessage(respMsg.response()) : channel.sendMessage("");
        if (respMsg.embed() != null) {
            var embed = Components.toJdaEmbed(respMsg.embed()).build();
            msgAction.setEmbeds(embed);
        }
        if (respMsg.buttons() != null && !respMsg.buttons().isEmpty()) {
            List<Button> jdaButtons = respMsg.buttons().stream()
                    .map(btn -> btn.style() == ButtonStyle.LINK ? Button.link(btn.url(), btn.label()) : Button.of(Components.getJdaButtonStyle(btn.style()), btn.customId(), btn.label()).withDisabled(btn.disabled()))
                    .collect(Collectors.toList());
            msgAction.setActionRow(jdaButtons);
        }
        msgAction.queue(success -> {
            if (respMsg.requestId() != null && !respMsg.requestId().isEmpty()) {
                String messageId = success.getId();
                platformManager.setGlobalMessageLabel(respMsg.requestId(), channelId, messageId);
            }
        });
    }
}
