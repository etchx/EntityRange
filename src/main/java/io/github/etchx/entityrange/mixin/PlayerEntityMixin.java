package io.github.etchx.entityrange.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.etchx.entityrange.data.HitType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static io.github.etchx.entityrange.client.EntityRangeClient.hitData;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    /**
     * Get the type of melee hit by the sound played
     */
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void getHitType(World instance, PlayerEntity source, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, Operation<Void> original) {
        if (hitData != null && hitData.getType() == HitType.NORMAL) {
            if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK) {
                hitData.setType(HitType.KB);
            } else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP) {
                hitData.setType(HitType.SWEEP);
            } else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_CRIT) {
                hitData.setType(HitType.CRIT);
            } else if (sound == SoundEvents.ENTITY_PLAYER_ATTACK_WEAK) {
                hitData.setType(HitType.WEAK);
            }
        }
        original.call(instance, source, x, y, z, sound, category, volume, pitch);
    }

    /**
     * Get the melee hit charge amount
     */
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
    private float getCharge(PlayerEntity instance, float baseTime, Operation<Float> original) {
        float charge = original.call(instance, baseTime);
        if (hitData != null && hitData.getCharge() == 0) {
            hitData.setCharge(charge);
        }
        return charge;
    }

    /**
     * Calculate the damage dealt
     */
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean getDamage(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        if (hitData != null && hitData.getDamage() == 0) {
            // cringe workaround bc GENERIC_ATTACK_DAMAGE isn't synced between client and server
            LivingEntity attacker = (LivingEntity)source.getSource();
            Map<Identifier, Double> addModifiers = new HashMap<>();
            Map<Identifier, Double> baseModifiers = new HashMap<>();
            Map<Identifier, Double> totalModifiers = new HashMap<>();
            Consumer<EntityAttributeModifier> storeModifier = m -> {
                switch (m.operation()) {
                    case ADD_VALUE -> addModifiers.put(m.id(), m.value());
                    case ADD_MULTIPLIED_BASE -> baseModifiers.put(m.id(), m.value());
                    case ADD_MULTIPLIED_TOTAL -> totalModifiers.put(m.id(), m.value());
                }
            };

            // Process equipment attribute modifiers
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                var stack = attacker.getEquippedStack(slot);
                var modifiersComponent = stack.getOrDefault(
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        AttributeModifiersComponent.DEFAULT
                );
                modifiersComponent.modifiers().stream()
                        .filter(e -> e.attribute().equals(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                        .filter(e -> e.slot().matches(slot))
                        .map(AttributeModifiersComponent.Entry::modifier)
                        .forEach(storeModifier);
            }

            // Process active status effect modifiers
            attacker.getActiveStatusEffects().values().forEach(effectInstance ->
                    effectInstance.getEffectType().value().forEachAttributeModifier(
                            effectInstance.getAmplifier(),
                            (attribute, modifier) -> {
                                if (attribute.equals(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
                                    storeModifier.accept(modifier);
                                }
                            }
                    )
            );

            // Calculate updated base damage
            double baseDamage = attacker.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            double updatedDamage = baseDamage + addModifiers.values().stream().mapToDouble(Double::doubleValue).sum();
            updatedDamage *= 1 + baseModifiers.values().stream().mapToDouble(Double::doubleValue).sum();
            updatedDamage *= totalModifiers.values().stream()
                    .mapToDouble(d -> 1.0 + d)
                    .reduce(1.0, (a, b) -> a * b);

            // had to do some wack stuff to get this to work
            float enchantDamage = EnchantmentHelper.getDamage(null, source.getWeaponStack(), instance, source, (float) updatedDamage) - (float) updatedDamage;

            updatedDamage *= 0.2F + hitData.getCharge() * hitData.getCharge() * 0.8F;
            enchantDamage *= hitData.getCharge();
            // on the client, this should only be true when there's a crit
            if (amount > baseDamage) {
                updatedDamage *= 1.5;
            }
            float totalDamage = (float) updatedDamage + enchantDamage;
            /*
            if (((PlayerEntity)(Object)this).getWorld().isClient) {
                LOGGER.info(String.valueOf(totalDamage));
            } else {
                LOGGER.info(String.valueOf(amount));
            }
            */
            hitData.setDamage(totalDamage);
        }
        return original.call(instance, source, amount);
    }

}
