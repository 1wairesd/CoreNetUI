package com.wairesd.discordbm.host.common.discord.response.handler.sender.option;

import com.wairesd.discordbm.common.models.buttons.ButtonStyle;
import com.wairesd.discordbm.common.models.response.ResponseMessage;
import com.wairesd.discordbm.common.utils.logging.PluginLogger;
import com.wairesd.discordbm.common.utils.logging.Slf4jPluginLogger;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorMessages;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.error.CommandErrorType;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.utils.Components;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Direct {
    private static final PluginLogger logger = new Slf4jPluginLogger(LoggerFactory.getLogger("DiscordBM"));
    private static DiscordBMHPlatformManager platformManager;

    public static void sendDirectMessage(ResponseMessage respMsg) {
        String userId = respMsg.userId();
        if (userId == null) {
            logger.error("No userId provided for direct_message");
            return;
        }
        var jda = platformManager.getDiscordBotManager().getJda();
        try {
            var user = jda.getUserById(userId);
            if (user == null) {
                logger.error("User with ID {} not found for direct_message", userId);
                return;
            }
            user.openPrivateChannel().queue(pc -> {
                var msgAction = respMsg.response() != null ? pc.sendMessage(respMsg.response()) : pc.sendMessage("");
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
                msgAction.queue(null, error -> {
                    if (error != null && error.getClass().getSimpleName().equals("ErrorResponseException") && error.getMessage().contains("50007")) {
                        logger.warn("Failed to send DM to user {}: 50007 Cannot send messages to this user", userId);
                    }
                });
            });
        } catch (NumberFormatException e) {
            logger.error("Invalid userId for direct_message: {}", userId);
            var embed = CommandErrorMessages.createErrorEmbed(CommandErrorType.INVALID_SNOWFLAKE);
        }
    }
}
