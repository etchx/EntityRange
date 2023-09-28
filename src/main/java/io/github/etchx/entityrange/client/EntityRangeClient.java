package io.github.etchx.entityrange.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class EntityRangeClient implements ClientModInitializer {
    public static Vec3d hitPos;
    public static double entityDistance;
    public static double lastHit;
    public static boolean targetingEntity;
    public static boolean arrowHit;
    public static PlayerEntity targetPlayer;
    public static double closestDistance = Double.MAX_VALUE;
    public static boolean projectileHit;

    @Override
    public void onInitializeClient() {
        EntityRangeConfig.load("entityrange.json");
    }
}
