package com.wairesd.discordbm.host.common.handler.role;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.models.request.RemoveRoleRequest;
import com.wairesd.discordbm.common.models.response.RoleActionResponse;
import io.netty.channel.ChannelHandlerContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class RemoveRoleHandler {
    private final JDA jda;
    private static final Gson gson = new Gson();

    public RemoveRoleHandler(Object jda) {
        this.jda = (JDA) jda;
    }

    public void handle(ChannelHandlerContext ctx, RemoveRoleRequest req) {
        Guild guild = jda.getGuildById(req.getGuildId());
        if (guild == null) {
            sendResponse(ctx, req.getRequestId(), false, "Guild not found");
            return;
        }

        Role role = guild.getRoleById(req.getRoleId());
        if (role == null) {
            sendResponse(ctx, req.getRequestId(), false, "Role not found");
            return;
        }

        guild.retrieveMemberById(req.getUserId()).queue(
                member -> guild.removeRoleFromMember(member, role).queue(
                        success -> sendResponse(ctx, req.getRequestId(), true, null),
                        error -> sendResponse(ctx, req.getRequestId(), false, error.getMessage())
                ),
                error -> sendResponse(ctx, req.getRequestId(), false, error.getMessage())
        );
    }

    private void sendResponse(ChannelHandlerContext ctx, String requestId, boolean success, String error) {
        RoleActionResponse resp = new RoleActionResponse(requestId, success, error);
        ctx.channel().writeAndFlush(gson.toJson(resp));
    }
}
