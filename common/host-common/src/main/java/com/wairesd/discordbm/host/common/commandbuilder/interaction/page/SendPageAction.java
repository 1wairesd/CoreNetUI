package com.wairesd.discordbm.host.common.commandbuilder.interaction.page;

import com.wairesd.discordbm.host.common.commandbuilder.core.models.actions.CommandAction;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.context.Context;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.pages.Page;
import com.wairesd.discordbm.host.common.commandbuilder.core.models.placeholders.PlaceholdersResolved;
import com.wairesd.discordbm.host.common.commandbuilder.utils.EmbedFactoryUtils;
import com.wairesd.discordbm.host.common.commandbuilder.utils.MessageFormatterUtils;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
        String pageId = resolvePageId(context);
        Page page = getPageOrSetError(context, pageId);
        if (page == null) return CompletableFuture.completedFuture(null);

        context.addActionRow(createButtonsRow(page));

        return resolvePageContent(page, context);
    }

    private String resolvePageId(Context context) {
        String id = PlaceholdersResolved.replaceSync(pageIdTemplate, context);
        return (id == null || id.isBlank()) ? "1" : id;
    }

    private Page getPageOrSetError(Context context, String pageId) {
        Page page = pageMap.get(pageId);
        if (page == null) {
            context.setMessageText("Page not found. (ID=" + pageId + ")");
        }
        return page;
    }

    private ActionRow createButtonsRow(Page page) {
        List<Button> buttons = page.getButtons().stream()
                .map(cfg -> Button.primary("goto:" + cfg.getTargetPage(), cfg.getLabel()))
                .toList();
        return ActionRow.of(buttons);
    }

    private CompletableFuture<Void> resolvePageContent(Page page, Context context) {
        if (page.getEmbedConfig() != null) {
            return EmbedFactoryUtils.create(page.getEmbedConfig(), context.getEvent(), context)
                    .thenAccept(context::setEmbed);
        }

        if (page.getContent() != null) {
            return MessageFormatterUtils.format(page.getContent(), context.getEvent(), context, false)
                    .thenAccept(context::setMessageText);
        }

        context.setMessageText("Invalid page configuration - no content or embed found. (ID=" + resolvePageId(context) + ")");
        return CompletableFuture.completedFuture(null);
    }
}
