package io.github.etchx.entityrange.client;

import net.minecraft.entity.player.PlayerEntity;

import static io.github.etchx.entityrange.client.EntityRangeClient.LOGGER;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestProjectilePlayerTarget;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestProjectileTargetSquaredDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.doUpdateHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.hasArrowHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.hasProjectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;

public class EntityRangeUtil {
    /**
     * Update projectile hit data
     */
    public static void updateProjectileHitData(PlayerEntity player) {
        // arrow hit on server but not on client
        if (hasArrowHit && !hasProjectileHit) {
            if (closestProjectilePlayerTarget != null) {
                lastHit = player.distanceTo(closestProjectilePlayerTarget);
                doUpdateHit = true;
                closestProjectilePlayerTarget = null;
            }
            else {
                LOGGER.warn("Arrow hit but player not found");
            }
            hasArrowHit = false;
        }
        // arrow hit on server and client
        else if (hasArrowHit) {
            doUpdateHit = true;
            hasArrowHit = false;
            hasProjectileHit = false;
        }
        // projectile hit on client, non-player target and/or non-arrow projectile
        // we can never know if it actually hit on the server unfortunately
        else if (hasProjectileHit && closestProjectilePlayerTarget == null) {
            doUpdateHit = true;
            hasProjectileHit = false;
        }
        // Last case: arrow hit a player on the client, but hasn't received arrow hit packet
        // this rolls over to future ticks, keeping the values of closestProjectilePlayerTarget and hasProjectileHit
        // if the arrow hit packet is never received, then it won't update until another projectile hits

        closestProjectileTargetSquaredDistance = Double.MAX_VALUE;
    }
}
