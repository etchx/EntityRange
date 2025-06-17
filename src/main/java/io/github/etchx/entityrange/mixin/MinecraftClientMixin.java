package io.github.etchx.entityrange.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.etchx.entityrange.data.HitData;
import io.github.etchx.entityrange.data.HitType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.etchx.entityrange.client.EntityRangeClient.hasRecorded;
import static io.github.etchx.entityrange.client.EntityRangeClient.isRecording;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.doUpdateHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.hitData;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    /**
     * On melee attack, set lastHit
     */
    @WrapOperation(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private HitResult.Type onGetType(HitResult instance, Operation<HitResult.Type> original) {
        HitResult.Type type = original.call(instance);
        if (type == HitResult.Type.ENTITY) {
            ClientPlayerEntity player = ((MinecraftClient) (Object) this).player;
            lastHit = player.getEyePos().distanceTo(raycastHitPos);
            doUpdateHit = true;
            hitData = new HitData(Util.getEpochTimeMs(), 0, HitType.NORMAL, lastHit, 0);
        } else if (isRecording && hasRecorded) {
            hitData = new HitData(Util.getEpochTimeMs(), 0, HitType.MISS, raycastHitDistance, 0);
        }
        return type;
    }
}
