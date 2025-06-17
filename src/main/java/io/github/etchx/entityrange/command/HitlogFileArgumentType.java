package io.github.etchx.entityrange.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class HitlogFileArgumentType implements ArgumentType<String> {
    public static HitlogFileArgumentType file() {
        return new HitlogFileArgumentType();
    }

    private static String[] getFileList() {
        String[] fileList = FabricLoader.getInstance().getGameDir().resolve("hitlogs").toFile().list();
        if (fileList == null) {
            return new String[]{};
        }
        return fileList;
    }

    public static String getFile(CommandContext<FabricClientCommandSource> context, String name) {
        try {
            return context.getArgument(name, String.class);
        } catch (IllegalArgumentException e) {
            String[] fileList = getFileList();
            if (fileList.length == 0) {
                return "invalid";
            }
            return fileList[fileList.length - 1];
        }
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        return stringReader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(getFileList(), builder);
    }
}
