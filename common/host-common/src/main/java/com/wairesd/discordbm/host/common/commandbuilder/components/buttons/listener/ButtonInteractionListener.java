package com.wairesd.discordbm.host.common.commandbuilder.components.buttons.listener;

import com.google.gson.Gson;
import com.wairesd.discordbm.host.common.discord.DiscordBMHPlatformManager;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.service.ButtonActionService;
import com.wairesd.discordbm.host.common.commandbuilder.security.buttons.checker.RoleChecker;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.form.ButtonFormBuilder;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model.ButtonConfig;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model.FormButtonData;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.handler.ButtonResponseHandler;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import com.wairesd.discordbm.host.common.config.configurators.Forms;
import com.wairesd.discordbm.host.common.models.request.RequestMessage;
import com.wairesd.discordbm.host.common.network.NettyServer;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ButtonInteractionListener extends ListenerAdapter {
    private final ButtonActionService actionService = new ButtonActionService();
    private final RoleChecker permissionChecker = new RoleChecker();
    private final ButtonFormBuilder modalBuilder = new ButtonFormBuilder();
    private final ButtonResponseHandler responseHandler = new ButtonResponseHandler();
    private final NettyServer nettyServer;
    private final DiscordBMHPlatformManager platformManager;

    public ButtonInteractionListener(NettyServer nettyServer, DiscordBMHPlatformManager platformManager) {
        this.nettyServer = nettyServer;
        this.platformManager = platformManager;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();

        if (buttonId.startsWith("refresh:")) {
            String[] parts = buttonId.split(":", 4);
            if (parts.length < 4) {
                event.reply("The button format is incorrect.").setEphemeral(true).queue();
                return;
            }

            String commandName = parts[1];
            String serverName = parts[2];
            String paramsStr = parts[3];

            Map<String, String> data = parseParams(paramsStr);

            event.deferEdit().queue(hook -> {
                UUID newRequestId = UUID.randomUUID();
                boolean isEphemeral = event.getMessage() != null && event.getMessage().isEphemeral();
                platformManager.storePendingButtonRequest(newRequestId, hook, isEphemeral);

                Channel channel = nettyServer.getChannelByServerName(serverName);
                if (channel == null) {
                    hook.editOriginal("Server not found.").queue();
                    return;
                }

                RequestMessage request = new RequestMessage("request", commandName, data, newRequestId.toString());
                String json = new Gson().toJson(request);
                nettyServer.sendMessage(channel, json);
            });
        } else if (buttonId.startsWith("goto:")) {
            String targetPageId = buttonId.substring(5);
            Page page = platformManager.getPageMap().get(targetPageId);

            if (page == null) {
                event.reply("Page not found.").setEphemeral(true).queue();
                return;
            }

            event.deferEdit().queue();

            if (page.getEmbedConfig() != null) {
                EmbedFactoryUtils.create(page.getEmbedConfig(), event, new Context(event))
                        .thenAccept(embed -> {
                            List<Button> buttons = new ArrayList<>();
                            for (ButtonConfig buttonConfig : page.getButtons()) {
                                String label = buttonConfig.getLabel();
                                String targetPage = buttonConfig.getTargetPage();
                                String newButtonId = "goto:" + targetPage;
                                buttons.add(Button.primary(newButtonId, label));
                            }
                            event.getHook().editOriginalEmbeds(embed)
                                    .setComponents(ActionRow.of(buttons))
                                    .queue();
                        })
                        .exceptionally(e -> {
                            event.getHook().editOriginal("Error creating embed").queue();
                            return null;
                        });
            } else {
                String content = page.getContent();
                Context context = new Context(event);
                MessageFormatterUtils.format(content, event, context, false)
                    .thenAccept(formattedContent -> {
                        List<Button> buttons = new ArrayList<>();
                        for (ButtonConfig buttonConfig : page.getButtons()) {
                            String label = buttonConfig.getLabel();
                            String targetPage = buttonConfig.getTargetPage();
                            String newButtonId = "goto:" + targetPage;
                            buttons.add(Button.primary(newButtonId, label));
                        }
                        event.getHook().editOriginal(formattedContent)
                                .setComponents(ActionRow.of(buttons))
                                .queue();
                    });
            }
            return;
        }

        FormButtonData formData = actionService.getFormButtonData(buttonId);
        if (formData != null) {
            if (!permissionChecker.hasPermission(event, formData.getRequiredRoleId())) {
                responseHandler.replyNoPermission(event);
                return;
            }
            var form = Forms.getForms().get(formData.getFormName());
            if (form == null) {
                responseHandler.replyNoForm(event);
                return;
            }
            var modal = modalBuilder.buildModal(form);
            platformManager.getFormHandlers().put(modal.getId(), formData.getMessageTemplate());
            event.replyModal(modal).queue();
            return;
        }

        String messageTemplate = actionService.getMessage(buttonId);
        if (messageTemplate != null) {
            Context context = new Context(event);
            MessageFormatterUtils.format(messageTemplate, event, context, false)
                    .thenAccept(formattedMessage -> {
                        if (!event.isAcknowledged()) {
                            responseHandler.replyMessageOrExpired(event, formattedMessage);
                        }
                    });
        } else {
            if (!event.isAcknowledged()) {
                responseHandler.replyMessageOrExpired(event, null);
            }
        }
    }

    private Map<String, String> parseParams(String paramsStr) {
        Map<String, String> data = new HashMap<>();
        String[] pairs = paramsStr.split(":");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                data.put(keyValue[0], keyValue[1]);
            }
        }
        return data;
    }
}