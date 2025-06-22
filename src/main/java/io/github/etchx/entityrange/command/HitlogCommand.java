package io.github.etchx.entityrange.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.etchx.entityrange.client.EntityRangeClient;
import io.github.etchx.entityrange.data.HitlogIO;
import io.github.etchx.entityrange.data.HitData;
import io.github.etchx.entityrange.data.HitType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.function.DoubleConsumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static io.github.etchx.entityrange.client.EntityRangeClient.hasRecorded;
import static io.github.etchx.entityrange.client.EntityRangeClient.isRecording;
import static io.github.etchx.entityrange.data.HitlogIO.deserializeHitlog;
import static io.github.etchx.entityrange.data.HitlogIO.getHitlogDir;
import static io.github.etchx.entityrange.data.HitlogIO.hitlogFilename;

public class HitlogCommand {

    public static int hitlogRecord(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        isRecording = !isRecording;
        if (isRecording) {
            hasRecorded = false;
            HitlogIO.hitlogFilename = Util.getFormattedCurrentTime()+".json";
            context.getSource().sendFeedback(Text.literal("Recording started").formatted(Formatting.GREEN));
        }
        else {
            context.getSource().sendFeedback(Text.literal("Recording stopped").formatted(Formatting.GREEN));
            if (!hasRecorded) {
                context.getSource().sendFeedback(Text.literal("No hits recorded"));
            }
            else {
                hasRecorded = false;
                try {
                    HitlogIO.endWriting();
                    Text text = Text.literal(hitlogFilename).formatted(Formatting.UNDERLINE).styled((style) ->
                            style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, HitlogIO.getHitlogDir().resolve(hitlogFilename).toAbsolutePath().toString())));
                    context.getSource().sendFeedback(Text.translatable("Saved hitlog in %s", text));
                    return hitlogStats(context);
                } catch (IOException e) {
                    context.getSource().sendFeedback(Text.translatable("Could not save hitlog", e).formatted(Formatting.RED));
                    return -1;
                }
            }
        }
        return 1;
    }

    private static void send(CommandContext<FabricClientCommandSource> context, String format, Object... args) {
        context.getSource().sendFeedback(Text.literal(String.format(format, args)));
    }

    private static void average(DoubleStream stream, DoubleConsumer consumer) {
        stream.average().ifPresent(consumer);
    }

    private static String capitalize(String s) {
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public static int hitlogStats(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        try {
            String filename = HitlogFileArgumentType.getFile(context, "file");
            HitData[] hitRecords = deserializeHitlog(getHitlogDir().resolve(filename));
            long totalCount = hitRecords.length;

            // Aggregate counts for each HitType
            EnumMap<HitType, Long> typeCounts = Arrays.stream(hitRecords)
                    .collect(Collectors.groupingBy(HitData::getType,
                            () -> new EnumMap<>(HitType.class),
                            Collectors.counting()));

            long missCount = typeCounts.getOrDefault(HitType.MISS, 0L);
            long hitCount = totalCount - missCount;

            // Send basic counts
            send(context, "Attacks: %d", totalCount);
            send(context, "Hits: %d (%.2f%%)", hitCount, hitCount * 100.0 / totalCount);
            send(context, "Misses: %d (%.2f%%)", missCount, missCount * 100.0 / totalCount);

            // Send detailed type counts
            for (HitType type : List.of(HitType.KB, HitType.CRIT, HitType.SWEEP, HitType.WEAK, HitType.NORMAL)) {
                send(context, "%s: %d", capitalize(type.name()), typeCounts.getOrDefault(type, 0L));
            }

            // Averages for hit records only (exclude MISS)
            var validHits = Arrays.stream(hitRecords)
                    .filter(r -> r.getType() != HitType.MISS)
                    .toList();

            average(validHits.stream().mapToDouble(HitData::getDistance),
                    v -> send(context, "Avg distance: %.3f", v));

            average(validHits.stream().mapToDouble(r -> r.getCharge() * 100),
                    v -> send(context, "Avg charge: %.2f%%", v));

            average(validHits.stream().mapToDouble(HitData::getDamage),
                    v -> send(context, "Avg damage: %.2f", v));

            // Time between hits
            if (totalCount > 1) {
                long[] deltas = new long[(int) totalCount - 1];
                for (int i = 0; i < totalCount - 1; i++) {
                    deltas[i] = hitRecords[i + 1].getTime() - hitRecords[i].getTime();
                }

                average(Arrays.stream(deltas).asDoubleStream(),
                        v -> send(context, "Avg time: %.3fs", v / 1000));

                double totalTime = Arrays.stream(deltas).sum() / 1000.0;
                double totalDamage = Arrays.stream(hitRecords)
                        .mapToDouble(HitData::getDamage)
                        .sum();
                send(context, "Total damage: %.2f", totalDamage);
                send(context, "DPS: %.2f", totalDamage / totalTime);
            }

        } catch (IOException e) {
            context.getSource().sendFeedback(Text.literal("Could not read hitlog").formatted(Formatting.RED));
            EntityRangeClient.LOGGER.error(e.getMessage());
            return -1;
        }
        return 1;
    }

    public static int hitlogDelete(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        try {
            String filename = HitlogFileArgumentType.getFile(context, "file");
            Files.delete(getHitlogDir().resolve(filename));
        } catch (IOException e) {
            context.getSource().sendFeedback(Text.literal("Could not delete hitlog").formatted(Formatting.RED));
            EntityRangeClient.LOGGER.error(e.getMessage());
            return -1;
        }
        return 1;
    }
}
