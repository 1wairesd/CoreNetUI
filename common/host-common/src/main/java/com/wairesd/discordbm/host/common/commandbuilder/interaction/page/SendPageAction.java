package com.wairesd.discordbm.host.common.commandbuilder.interaction.page;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.components.buttons.model.ButtonConfig;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersResolved;
import com.wairesd.discordbm.host.common.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.wairesd.discordbm.host.common.config.configurators.Pages.pageMap;

public class SendPageAction implements CommandAction {
    private final String pageIdTemplate;

    public SendPageAction(Map<String, Object> props) {
        this.pageIdTemplate = (String) props.get("page_id");
    }

    @Override
    public CompletableFuture<Void> execute(Context context) {
        String pageId = PlaceholdersResolved.replaceSync(pageIdTemplate, context);
        if (pageId == null || pageId.isBlank()) pageId = "1";

        Page page = pageMap.get(pageId);
        if (page == null) {
            context.setMessageText("Page not found. (ID=" + pageId + ")");
            return CompletableFuture.completedFuture(null);
        }

        List<Button> buttons = new ArrayList<>();
        for (ButtonConfig cfg : page.getButtons()) {
            buttons.add(Button.primary("goto:" + cfg.getTargetPage(), cfg.getLabel()));
        }
        context.addActionRow(ActionRow.of(buttons));

        if (page.getEmbedConfig() != null) {
            return EmbedFactoryUtils
                    .create(page.getEmbedConfig(), context.getEvent(), context)
                    .thenAccept(context::setEmbed);
        }
        else if (page.getContent() != null) {
            return MessageFormatterUtils.format(page.getContent(), context.getEvent(), context, false)
                    .thenAccept(context::setMessageText);
        }
        else {
            context.setMessageText("Invalid page configuration - no content or embed found. (ID=" + pageId + ")");
            return CompletableFuture.completedFuture(null);
        }
    }
}