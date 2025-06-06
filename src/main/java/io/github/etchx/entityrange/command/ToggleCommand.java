package io.github.etchx.entityrange.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.etchx.entityrange.client.EntityRangeClient;
import io.github.etchx.entityrange.client.EntityRangeConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;
import static io.github.etchx.entityrange.client.EntityRangeConfig.useLongDistance;

public class ToggleCommand {
    public static int toggleChat(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        showHitsInChat = !showHitsInChat;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable(
                String.format("Hit distances in chat %s", showHitsInChat ? "enabled" : "disabled")));
        return 1;
    }

    public static int toggleHits(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        hideHitDisplay = !hideHitDisplay;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable(
                String.format("Hit display %s", !hideHitDisplay ? "enabled" : "disabled")));
        return 1;
    }

    public static int toggleDistance(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        hideDistanceDisplay = !hideDistanceDisplay;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable(
                String.format("Distance display %s", !hideDistanceDisplay ? "enabled" : "disabled")));
        return 1;
    }

    public static int toggleLong(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        useLongDistance = !useLongDistance;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable(
                String.format("Long distance %s", useLongDistance ? "enabled" : "disabled")));
        return 1;
    }
}
