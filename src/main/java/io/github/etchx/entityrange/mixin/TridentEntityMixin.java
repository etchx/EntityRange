package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.projectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {
    @Inject(method = "onEntityHit", at = @At(value = "RETURN"))
    private void getTridentHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (((TridentEntity)(Object)this).getOwner() != null &&
                player != null &&
                ((TridentEntity)(Object)this).getOwner().getUuid() == player.getUuid()) {
            projectileHit = true;
            lastHit = player.distanceTo(entityHitResult.getEntity());
            targetPlayer = null;
        }
    }
}