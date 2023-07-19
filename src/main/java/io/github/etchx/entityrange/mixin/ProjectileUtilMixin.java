package io.github.etchx.entityrange.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

import static io.github.etchx.entityrange.client.EntityRangeClient.intersectPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastResult;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    @Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Box;raycast(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;)Ljava/util/Optional;"))
    private static Optional<Vec3d> getIntersectPos(Box instance, Vec3d min, Vec3d max) {
        Optional<Vec3d> Optional = instance.raycast(min, max);
        if (!raycastResult) {
            intersectPos = Optional.orElse(null);
        }
        return Optional;
    }
    @Inject(at = @At(value = "TAIL"), method = "raycast")
    private static void getResult(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d, CallbackInfoReturnable<@Nullable EntityHitResult> cir) {
        raycastResult = true;
    }
}
