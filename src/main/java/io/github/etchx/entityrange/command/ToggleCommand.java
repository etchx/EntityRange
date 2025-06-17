package io.github.etchx.entityrange.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;
import static io.github.etchx.entityrange.client.EntityRangeConfig.useLongDistance;

public class ToggleCommand {
    public static int toggleChat(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        showHitsInChat = !showHitsInChat;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable("Hit distances in chat %s", showHitsInChat ?
                        Text.literal("enabled").formatted(Formatting.GREEN) :
                        Text.literal("disabled").formatted(Formatting.RED)));
        return 1;
    }

    public static int toggleHits(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        hideHitDisplay = !hideHitDisplay;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable("Hit display %s", !hideHitDisplay ?
                        Text.literal("enabled").formatted(Formatting.GREEN) :
                        Text.literal("disabled").formatted(Formatting.RED)));
        return 1;
    }

    public static int toggleDistance(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        hideDistanceDisplay = !hideDistanceDisplay;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable("Distance display %s", !hideDistanceDisplay ?
                        Text.literal("enabled").formatted(Formatting.GREEN) :
                        Text.literal("disabled").formatted(Formatting.RED)));
        return 1;
    }

    public static int toggleLong(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        useLongDistance = !useLongDistance;
        Commands.updateConfig();
        context.getSource().sendFeedback(Text.translatable("Long distance %s", useLongDistance ?
                        Text.literal("enabled").formatted(Formatting.GREEN) :
                        Text.literal("disabled").formatted(Formatting.RED)));
        return 1;
    }
}
