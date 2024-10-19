package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

import static io.github.etchx.entityrange.client.EntityRangeClient.targetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.hitPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.entityDistance;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.useLongDistance;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Redirect(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    private EntityHitResult getLongDistance(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d) {
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, min, max, box, predicate, d);
        if (entityHitResult == null && useLongDistance && !hideDistanceDisplay) {
            int distance = 80;
            HitResult hitResult = entity.raycast(distance, 1.0f, false);
            if (hitResult != null) {
                d = hitResult.getPos().squaredDistanceTo(min);
            }
            else {
                d = distance*distance;
            }
            Vec3d rot = entity.getRotationVec(1.0f);
            max = min.add(rot.x*distance, rot.y*distance, rot.z*distance);
            box = entity.getBoundingBox().stretch(rot.multiply(distance)).expand(1.0, 1.0, 1.0);
            EntityHitResult longHitResult = ProjectileUtil.raycast(entity, min, max, box, predicate, d);
            if (longHitResult != null) {
                targetingEntity = true;
                Vec3d newPos = longHitResult.getPos();
                if (hitPos == null || newPos.distanceTo(hitPos) > 0.0001) {
                    entityDistance = entity.getEyePos().distanceTo(newPos);
                    hitPos = newPos;
                }
            }
        }
        else if (entityHitResult != null) {
            targetingEntity = true;
            Vec3d newPos = entityHitResult.getPos();
            if (hitPos == null || newPos.distanceTo(hitPos) > 0.0001) {
                entityDistance = entity.getEyePos().distanceTo(newPos);
                hitPos = newPos;
            }
        }
        return entityHitResult;
    }
}
