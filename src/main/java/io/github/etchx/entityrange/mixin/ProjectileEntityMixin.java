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

import static io.github.etchx.entityrange.client.EntityRangeClient.projectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Inject(method = "onEntityHit", at = @At(value = "RETURN"))
    private void getProjectileHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (((ProjectileEntity)(Object)this).getOwner() != null &&
                player != null &&
                ((ProjectileEntity)(Object)this).getOwner().getUuid() == player.getUuid()) {
            projectileHit = true;
            lastHit = player.distanceTo(entityHitResult.getEntity());
            if (entityHitResult.getEntity() instanceof PlayerEntity && ((ProjectileEntity)(Object)this) instanceof PersistentProjectileEntity) {
                targetPlayer = (PlayerEntity)entityHitResult.getEntity();
            }
            else {
                targetPlayer = null;
            }
        }
    }
}