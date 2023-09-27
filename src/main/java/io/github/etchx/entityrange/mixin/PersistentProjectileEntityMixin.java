package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.arrowHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void findHit(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (((PersistentProjectileEntity)(Object)this).getOwner() != null &&
                player != null &&
                ((PersistentProjectileEntity)(Object)this).getOwner().getUuid() == player.getUuid()) {
            PlayerEntity closestPlayer = player.getWorld().getClosestPlayer(TargetPredicate.DEFAULT, player,
                    ((PersistentProjectileEntity)(Object)this).getX(),
                    ((PersistentProjectileEntity)(Object)this).getY(),
                    ((PersistentProjectileEntity)(Object)this).getZ());
            if (closestPlayer != null) {
                float currentDistance = closestPlayer.distanceTo(((PersistentProjectileEntity)(Object)this));
                if (currentDistance < closestDistance || targetPlayer == null) {
                    closestDistance = currentDistance;
                    targetPlayer = closestPlayer;
                }
            }
        }
    }
}
