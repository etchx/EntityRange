package io.github.etchx.entityrange.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.etchx.entityrange.client.EntityRangeScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class LayoutCommand {
    public static int openLayout(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.setScreen(new EntityRangeScreen(client.currentScreen)));
        return 1;
    }
}
