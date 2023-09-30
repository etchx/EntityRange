package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.closestDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void findHit(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (!(((PersistentProjectileEntity)(Object)this) instanceof TridentEntity) &&
                ((PersistentProjectileEntity)(Object)this).getOwner() != null &&
                player != null &&
                ((PersistentProjectileEntity)(Object)this).getOwner().getUuid() == player.getUuid()) {
            Vec3d velocity = ((PersistentProjectileEntity)(Object)this).getVelocity();
            Vec3d position = ((PersistentProjectileEntity)(Object)this).getPos().add(velocity.getX()/2, velocity.getY()/2, velocity.getZ()/2);
            PlayerEntity closestPlayer = player.getWorld().getClosestPlayer(TargetPredicate.DEFAULT, player,
                    position.getX(),
                    position.getY(),
                    position.getZ());
            if (closestPlayer != null) {
                double currentDistance = closestPlayer.squaredDistanceTo(position);
                if (currentDistance < closestDistance || targetPlayer == null) {
                    closestDistance = currentDistance;
                    targetPlayer = closestPlayer;
                }
            }
        }
    }
}