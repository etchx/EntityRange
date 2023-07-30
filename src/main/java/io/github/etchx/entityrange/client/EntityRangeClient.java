package io.github.etchx.entityrange.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.Vec3d;

public class EntityRangeClient implements ClientModInitializer {
    public static Vec3d hitPos;
    public static double entityDistance;
    public static double lastHit;
    public static boolean displayDistance;

    @Override
    public void onInitializeClient() {
        /**
         * Runs the mod initializer on the client environment.
         */
        EntityRangeConfig.load("entityrange.json");
    }
}
