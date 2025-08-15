package com.wairesd.discordbm.host.common.handler.role;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.models.request.RemoveRoleRequest;
import com.wairesd.discordbm.common.models.response.RoleActionResponse;
import io.netty.channel.ChannelHandlerContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RemoveRoleHandler {

    private static final Gson gson = new Gson();
    private static final String GUILD_NOT_FOUND_ERROR = "Guild not found";
    private static final String ROLE_NOT_FOUND_ERROR = "Role not found";

    private final JDA jda;

    public RemoveRoleHandler(Object jda) {
        this.jda = (JDA) jda;
    }

    public void handle(ChannelHandlerContext ctx, RemoveRoleRequest req) {
        Guild guild = validateAndGetGuild(ctx, req);
        if (guild == null) {
            return;
        }

        Role role = validateAndGetRole(ctx, req, guild);
        if (role == null) {
            return;
        }

        removeRoleFromMember(ctx, req, guild, role);
    }

    private Guild validateAndGetGuild(ChannelHandlerContext ctx, RemoveRoleRequest req) {
        Guild guild = jda.getGuildById(req.getGuildId());
        if (guild == null) {
            sendErrorResponse(ctx, req.getRequestId(), GUILD_NOT_FOUND_ERROR);
        }
        return guild;
    }

    private Role validateAndGetRole(ChannelHandlerContext ctx, RemoveRoleRequest req, Guild guild) {
        Role role = guild.getRoleById(req.getRoleId());
        if (role == null) {
            sendErrorResponse(ctx, req.getRequestId(), ROLE_NOT_FOUND_ERROR);
        }
        return role;
    }

    private void removeRoleFromMember(ChannelHandlerContext ctx, RemoveRoleRequest req, Guild guild, Role role) {
        guild.retrieveMemberById(req.getUserId()).queue(
                member -> executeRemoveRoleAction(ctx, req, guild, member, role),
                error -> sendErrorResponse(ctx, req.getRequestId(), error.getMessage())
        );
    }

    private void executeRemoveRoleAction(ChannelHandlerContext ctx, RemoveRoleRequest req, Guild guild, Member member, Role role) {
        guild.removeRoleFromMember(member, role).queue(
                success -> sendSuccessResponse(ctx, req.getRequestId()),
                error -> sendErrorResponse(ctx, req.getRequestId(), error.getMessage())
        );
    }

    private void sendSuccessResponse(ChannelHandlerContext ctx, String requestId) {
        sendRoleActionResponse(ctx, requestId, true, null);
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, String requestId, String errorMessage) {
        sendRoleActionResponse(ctx, requestId, false, errorMessage);
    }

    private void sendRoleActionResponse(ChannelHandlerContext ctx, String requestId, boolean success, String error) {
        RoleActionResponse response = new RoleActionResponse(requestId, success, error);
        ctx.channel().writeAndFlush(gson.toJson(response));
    }
}