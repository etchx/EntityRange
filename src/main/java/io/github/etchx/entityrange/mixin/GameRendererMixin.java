package io.github.etchx.entityrange.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

import static io.github.etchx.entityrange.client.EntityRangeClient.isTargetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.isInRange;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitDistance;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.useLongDistance;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    /**
     * Helper method to AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA GET ME OUT OF HERE
     */
    @Unique
    private void updateRaycastHit(Entity camera, Entity entity, Vec3d pos, double entityInteractionRange) {
        if (
                !(camera.isPlayer() && (
                ((PlayerEntity) camera).hasStatusEffect(StatusEffects.BLINDNESS) ||
                        entity.isInvisibleTo((PlayerEntity) camera)
                        ))
        ) {
            isTargetingEntity = true;
            if (raycastHitPos == null || pos.distanceTo(raycastHitPos) > 0.0001) {
                raycastHitDistance = camera.getEyePos().distanceTo(pos);
                raycastHitPos = pos;
                isInRange = raycastHitDistance <= entityInteractionRange; // client's interaction range, might be possible to simplify by checking hitresult type
            }
        }
    }

    /**
     * Calculate targeted entity distance with "smart" raycasting
     */
    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private EntityHitResult getLongDistance(Entity camera, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d,
                                            Entity camera2, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, min, max, box, predicate, d);
        if (entityHitResult != null) { // Use default raycast when entity close to interaction range
            updateRaycastHit(camera, entityHitResult.getEntity(), entityHitResult.getPos(), entityInteractionRange);
        }
        else if (useLongDistance && !hideDistanceDisplay) { // More expensive raycast when long distance is enabled
            double distance = 80; // Hardcoded distance value
            d = MathHelper.square(distance);
            HitResult hitResult = camera.raycast(distance, tickDelta, false); // this raycast checks for blocks in the way and reduces the max distance
            double e = hitResult.getPos().squaredDistanceTo(min);
            if (hitResult.getType() != HitResult.Type.MISS) {
                d = e;
                distance = Math.sqrt(e);
            }

            Vec3d rot = camera.getRotationVec(tickDelta);
            max = min.add(rot.x*distance, rot.y*distance, rot.z*distance);
            box = camera.getBoundingBox().stretch(rot.multiply(distance)).expand(1.0, 1.0, 1.0);
            EntityHitResult longHitResult = ProjectileUtil.raycast(camera, min, max, box, predicate, d);
            if (longHitResult != null) {
                updateRaycastHit(camera, longHitResult.getEntity(), longHitResult.getPos(), entityInteractionRange);
            }
        }
        return entityHitResult;
    }
}
