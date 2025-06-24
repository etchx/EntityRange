package io.github.etchx.entityrange.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

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

    public static int getX(float percentX, int screenWidth, Text message, TextRenderer textRenderer) {
        return (int) ((screenWidth - textRenderer.getWidth(message)) / 2 * (1 + percentX/100));
    }

    public static int getY(float percentY, int screenHeight) {
        return (int) ((screenHeight - 7) / 2 * (1 - percentY/100));
    }

    public static float getPercentX(int x, int screenWidth, Text message, TextRenderer textRenderer) {
        int textWidth = textRenderer.getWidth(message);
        float base = (screenWidth - textWidth) / 2.0f;
        return ((x / base) - 1) * 100;
    }

    public static float getPercentY(int y, int screenHeight) {
        float base = (screenHeight - 7) / 2.0f;
        return (1 - (y / base)) * 100;
    }
}
