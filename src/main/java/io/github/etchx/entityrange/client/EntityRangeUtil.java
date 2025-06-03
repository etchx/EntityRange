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
        // projectile hit non-player on client
        // possible bug where client thinks the projectile has hit a player, but never receives arrow hit packet.
        else if (hasProjectileHit && closestProjectilePlayerTarget == null) {
            doUpdateHit = true;
            hasProjectileHit = false;
        }
        // i forgor
        else if (hasProjectileHit) {
            LOGGER.warn("Stop hitting yourself!");
            closestProjectilePlayerTarget = null;
            hasProjectileHit = false;
        }
        closestProjectileTargetSquaredDistance = Double.MAX_VALUE;
    }
}
