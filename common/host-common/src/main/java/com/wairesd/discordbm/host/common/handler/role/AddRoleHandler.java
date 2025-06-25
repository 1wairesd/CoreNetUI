package com.wairesd.discordbm.host.common.handler.role;

import com.google.gson.Gson;
import com.wairesd.discordbm.common.models.request.AddRoleRequest;
import com.wairesd.discordbm.common.models.response.RoleActionResponse;
import io.netty.channel.ChannelHandlerContext;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class AddRoleHandler {
    private final Object jda;

    public AddRoleHandler(Object jda) {
        this.jda = jda;
    }

    public void handle(ChannelHandlerContext ctx, AddRoleRequest req) {
        JDA jdaInstance = (JDA) jda;
        Guild guild = jdaInstance.getGuildById(req.getGuildId());
        if (guild == null) {
            sendRoleActionResponse(ctx, req.getRequestId(), false, "Guild not found");
            return;
        }

        Role role = guild.getRoleById(req.getRoleId());
        if (role == null) {
            sendRoleActionResponse(ctx, req.getRequestId(), false, "Role not found");
            return;
        }

        guild.retrieveMemberById(req.getUserId()).queue(
                member -> guild.addRoleToMember(member, role).queue(
                        success -> sendRoleActionResponse(ctx, req.getRequestId(), true, null),
                        error -> sendRoleActionResponse(ctx, req.getRequestId(), false, error.getMessage())
                ),
                error -> sendRoleActionResponse(ctx, req.getRequestId(), false, error.getMessage())
        );
    }

    private void sendRoleActionResponse(ChannelHandlerContext ctx, String requestId, boolean success, String error) {
        RoleActionResponse resp = new RoleActionResponse(requestId, success, error);
        ctx.channel().writeAndFlush(new Gson().toJson(resp));
    }
}
