package io.github.etchx.entityrange.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    /**
     * Ensure call to createEnchantedDamageLootContext with null ServerWorld does not cause exception
     */
    @WrapOperation(method = "createEnchantedDamageLootContext", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/context/LootContext$Builder;build(Ljava/util/Optional;)Lnet/minecraft/loot/context/LootContext;"))
    private static LootContext onCall(LootContext.Builder instance, Optional<Identifier> randomId, Operation<LootContext> original, ServerWorld world, int level, Entity entity, DamageSource damageSource) {
        if (world == null) {
            return LootContextInvoker.callLootContext(((LootContextBuilderAccessor) instance).getParameters(), null, null);
        } else {
            return original.call(instance, randomId);
        }
    }
}
