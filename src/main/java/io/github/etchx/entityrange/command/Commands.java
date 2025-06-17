package io.github.etchx.entityrange.command;

import com.mojang.brigadier.tree.ArgumentCommandNode;
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
            LiteralCommandNode<FabricClientCommandSource> hitlogNode = ClientCommandManager
                    .literal("hitlog")
                    .build();
            LiteralCommandNode<FabricClientCommandSource> recordNode = ClientCommandManager
                    .literal("record")
                    .executes(HitlogCommand::hitlogRecord)
                    .build();
            LiteralCommandNode<FabricClientCommandSource> statsNode = ClientCommandManager
                    .literal("stats")
                    .executes(HitlogCommand::hitlogStats)
                    .build();
            ArgumentCommandNode<FabricClientCommandSource, String> statsArgs = ClientCommandManager
                    .argument("file", HitlogFileArgumentType.file())
                    .executes(HitlogCommand::hitlogStats)
                    .build();

            dispatcher.getRoot().addChild(erNode);
            erNode.addChild(toggleNode);
            toggleNode.addChild(chatNode);
            toggleNode.addChild(hitNode);
            toggleNode.addChild(distanceNode);
            toggleNode.addChild(longNode);
            erNode.addChild(hitlogNode);
            hitlogNode.addChild(recordNode);
            hitlogNode.addChild(statsNode);
            statsNode.addChild(statsArgs);
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
