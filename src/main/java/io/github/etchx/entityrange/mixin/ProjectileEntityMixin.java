package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.projectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileEntityMixin {
    @Inject(method = "onEntityHit", at = @At(value = "RETURN"))
    private void getProjectileHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (((ProjectileEntity)(Object)this).getOwner().getUuid() == player.getUuid()) {
            projectileHit = true;
            lastHit = player.distanceTo(entityHitResult.getEntity());
            if (entityHitResult.getEntity() instanceof PlayerEntity) {
                targetPlayer = (PlayerEntity)entityHitResult.getEntity();
            }
            else {
                targetPlayer = null;
            }
        }
    }
}
