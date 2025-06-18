package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.hasProjectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestProjectilePlayerTarget;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    /**
     * Get the entity hit by a projectile. May not always be useful because of client/server desyncs.
     */
    @Inject(method = "onEntityHit", at = @At(value = "RETURN"))
    private void getProjectileHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        ProjectileEntity projectile = (ProjectileEntity)(Object)this;
        if (projectile.getOwner() != null &&
                player != null &&
                projectile.getOwner().getUuid().equals(player.getUuid())) {
            hasProjectileHit = true;
            lastHit = player.distanceTo(entityHitResult.getEntity());
            // if an arrow hit a player
            if (entityHitResult.getEntity() instanceof PlayerEntity && projectile instanceof PersistentProjectileEntity) {
                closestProjectilePlayerTarget = (PlayerEntity)entityHitResult.getEntity();
            }
            else {
                closestProjectilePlayerTarget = null;
            }
        }
    }
}
