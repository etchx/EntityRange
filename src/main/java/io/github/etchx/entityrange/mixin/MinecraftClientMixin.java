package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.etchx.entityrange.client.EntityRangeClient.hitPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V"))
    private void displayHitDistance(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = ((MinecraftClient)(Object)this).player;
        lastHit = player.getEyePos().distanceTo(hitPos);
        if (showHitsInChat) player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
    }
}
