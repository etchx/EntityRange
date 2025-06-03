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

import static io.github.etchx.entityrange.client.EntityRangeClient.closestProjectileTargetSquaredDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestProjectilePlayerTarget;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    /**
     * Get the player closest to any projectile owned by the client player. This is necessary when client/server desyncs
     * cause onEntityHit logic to fail.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void findHit(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        PersistentProjectileEntity projectile = ((PersistentProjectileEntity)(Object) this);
        if (!(projectile instanceof TridentEntity) && // unnecessary for specifically tridents
                projectile.getOwner() != null &&
                player != null &&
                projectile.getOwner().getUuid().equals(player.getUuid())) {
            Vec3d velocity = projectile.getVelocity();
            Vec3d position = projectile.getPos().add(velocity.getX()/2, velocity.getY()/2, velocity.getZ()/2);
            PlayerEntity closestPlayer = player.getWorld().getClosestPlayer(TargetPredicate.DEFAULT, player,
                    position.getX(),
                    position.getY(),
                    position.getZ());
            if (closestPlayer != null) {
                // gets the target closest to any valid projectile
                double currentDistance = closestPlayer.squaredDistanceTo(position);
                if (currentDistance < closestProjectileTargetSquaredDistance || closestProjectilePlayerTarget == null) {
                    closestProjectileTargetSquaredDistance = currentDistance;
                    closestProjectilePlayerTarget = closestPlayer;
                }
            }
        }
    }
}
