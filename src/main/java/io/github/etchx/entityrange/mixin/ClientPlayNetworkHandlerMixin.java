package io.github.etchx.entityrange.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.hasArrowHit;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    /**
     * When the client receives a packet indicating a projectile shot hit another player (arrow "ding" sound),
     * set hasArrowHit variable to true.
     */
    @Inject(method = "onGameStateChange", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket;getReason()Lnet/minecraft/network/packet/s2c/play/GameStateChangeS2CPacket$Reason;"))
    private void getArrowDistance(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        if (packet.getReason() == GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER) {
            hasArrowHit = true;
        }
    }
}
