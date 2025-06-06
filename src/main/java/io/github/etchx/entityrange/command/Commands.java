package io.github.etchx.entityrange.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.etchx.entityrange.client.EntityRangeClient;
import io.github.etchx.entityrange.client.EntityRangeConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;

public class Commands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> erNode = ClientCommandManager
                    .literal("er")
                    .build();
            LiteralCommandNode<FabricClientCommandSource> toggleNode = ClientCommandManager
                    .literal("toggle")
                    .build();
            LiteralCommandNode<FabricClientCommandSource> chatNode = ClientCommandManager
                    .literal("chat")
                    .executes(ToggleCommand::toggleChat)
                    .build();
            LiteralCommandNode<FabricClientCommandSource> hitNode = ClientCommandManager
                    .literal("hits")
                    .executes(ToggleCommand::toggleHits)
                    .build();
            LiteralCommandNode<FabricClientCommandSource> distanceNode = ClientCommandManager
                    .literal("distance")
                    .executes(ToggleCommand::toggleDistance)
                    .build();
            LiteralCommandNode<FabricClientCommandSource> longNode = ClientCommandManager
                    .literal("long")
                    .executes(ToggleCommand::toggleLong)
                    .build();

            dispatcher.getRoot().addChild(erNode);
            erNode.addChild(toggleNode);
            toggleNode.addChild(chatNode);
            toggleNode.addChild(hitNode);
            toggleNode.addChild(distanceNode);
            toggleNode.addChild(longNode);
        });
    }

    public static void updateConfig() {
        try {
            EntityRangeConfig.writeChanges(EntityRangeClient.CONFIG);
        }
        catch (IOException e) {
            EntityRangeClient.LOGGER.error("Failed to update config file", e);
        }
    }
}
