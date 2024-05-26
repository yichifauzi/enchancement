/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.mixin.vanillachanges.weaponenchantmentcooldownrequirement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	@Unique
	private float attackCooldown = 0;

	@WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/entity/LivingEntity;)I"))
	private int enchancement$weaponEnchantmentCooldownRequirement(LivingEntity entity, Operation<Integer> original) {
		if (attackCooldown < ModConfig.weaponEnchantmentCooldownRequirement) {
			return 0;
		}
		return original.call(entity);
	}

	@WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
	private float enchancement$weaponEnchantmentCooldownRequirement(PlayerEntity instance, float baseTime, Operation<Float> original) {
		float cooldown = original.call(instance, baseTime);
		this.attackCooldown = cooldown;
		return cooldown;
	}

	@Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V"))
	private void enchancement$weaponEnchantmentCooldownRequirementOnTargetDamaged(Entity target, CallbackInfo ci) {
		if (attackCooldown < ModConfig.weaponEnchantmentCooldownRequirement) {
			EnchancementUtil.shouldCancelTargetDamagedEnchantments = true;
		}
	}

	@Inject(method = "attack", at = @At("TAIL"))
	private void enchancement$weaponEnchantmentCooldownRequirement(Entity target, CallbackInfo ci) {
		attackCooldown = 0;
	}
}
