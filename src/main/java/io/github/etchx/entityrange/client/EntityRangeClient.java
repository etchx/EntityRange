package io.github.etchx.entityrange.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class EntityRangeClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("EntityRange");

    /** Position of where player's line of sight intersects with entity hitbox */
    public static Vec3d raycastHitPos;
    /** Distance from player's eye to raycastHitPos */
    public static double raycastHitDistance;
    /** Distance from player to attacked entity, to be displayed */
    public static double lastHit;
    /** Whether an entity intersects the player's line of sight */
    public static boolean isTargetingEntity;
    /** Whether a targeted entity is in interaction range */
    public static boolean isInRange;
    /** Whether the client received a PROJECTILE_HIT_PLAYER packet */
    public static boolean hasArrowHit;
    /** Player closest to the path of a flying projectile owned by client player */
    public static PlayerEntity closestProjectilePlayerTarget;
    /** Squared distance from projectile path to closest player */
    public static double closestProjectileTargetSquaredDistance = Double.MAX_VALUE;
    /** Whether a projectile hit an entity on the client */
    public static boolean hasProjectileHit;
    /** Whether a new hit occured */
    public static boolean doUpdateHit;

    @Override
    public void onInitializeClient() {
        EntityRangeConfig.load("entityrange.json");
    }
}
