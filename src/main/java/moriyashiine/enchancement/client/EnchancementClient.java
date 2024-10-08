/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.client;

import moriyashiine.enchancement.client.event.*;
import moriyashiine.enchancement.client.event.integration.appleskin.BrimstoneAppleskinEvent;
import moriyashiine.enchancement.client.particle.SparkParticle;
import moriyashiine.enchancement.client.payload.*;
import moriyashiine.enchancement.client.reloadlisteners.FrozenReloadListener;
import moriyashiine.enchancement.client.render.entity.AmethystShardEntityRenderer;
import moriyashiine.enchancement.client.render.entity.BrimstoneEntityRenderer;
import moriyashiine.enchancement.client.render.entity.IceShardEntityRenderer;
import moriyashiine.enchancement.client.render.entity.TorchEntityRenderer;
import moriyashiine.enchancement.client.render.entity.mob.FrozenPlayerEntityRenderer;
import moriyashiine.enchancement.client.screen.EnchantingTableScreen;
import moriyashiine.enchancement.client.util.EnchancementClientUtil;
import moriyashiine.enchancement.common.Enchancement;
import moriyashiine.enchancement.common.enchantment.effect.AllowLoadingProjectileEffect;
import moriyashiine.enchancement.common.init.ModComponentTypes;
import moriyashiine.enchancement.common.init.ModEntityTypes;
import moriyashiine.enchancement.common.init.ModParticleTypes;
import moriyashiine.enchancement.common.init.ModScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import org.lwjgl.glfw.GLFW;
import squeek.appleskin.api.event.HUDOverlayEvent;

import java.util.function.Supplier;

public class EnchancementClient implements ClientModInitializer {
	public static final KeyBinding DASH_KEYBINDING = registerKeyBinding(() -> KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + Enchancement.MOD_ID + ".dash", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories." + Enchancement.MOD_ID)));
	public static final KeyBinding SLAM_KEYBINDING = registerKeyBinding(() -> KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + Enchancement.MOD_ID + ".slam", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories." + Enchancement.MOD_ID)));
	public static final KeyBinding SLIDE_KEYBINDING = registerKeyBinding(() -> KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + Enchancement.MOD_ID + ".slide", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories." + Enchancement.MOD_ID)));
	public static final KeyBinding STRAFE_KEYBINDING = registerKeyBinding(() -> KeyBindingHelper.registerKeyBinding(new KeyBinding("key." + Enchancement.MOD_ID + ".strafe", GLFW.GLFW_KEY_R, "key.categories." + Enchancement.MOD_ID)));

	public static boolean betterCombatLoaded = false;

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntityTypes.FROZEN_PLAYER, FrozenPlayerEntityRenderer::new);
		EntityRendererRegistry.register(ModEntityTypes.ICE_SHARD, IceShardEntityRenderer::new);
		EntityRendererRegistry.register(ModEntityTypes.BRIMSTONE, BrimstoneEntityRenderer::new);
		EntityRendererRegistry.register(ModEntityTypes.AMETHYST_SHARD, AmethystShardEntityRenderer::new);
		EntityRendererRegistry.register(ModEntityTypes.TORCH, TorchEntityRenderer::new);
		ParticleFactoryRegistry.getInstance().register(ModParticleTypes.BRIMSTONE_BUBBLE, WaterBubbleParticle.Factory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARK, provider -> new SparkParticle.Factory());
		ModelPredicateProviderRegistry.register(Items.CROSSBOW, Enchancement.id("brimstone"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) && stack.contains(ModComponentTypes.BRIMSTONE_DAMAGE) ? stack.get(ModComponentTypes.BRIMSTONE_DAMAGE) / 12F : 0);
		ModelPredicateProviderRegistry.register(Items.CROSSBOW, Enchancement.id("amethyst_shard"), (stack, world, entity, seed) -> stack.contains(DataComponentTypes.CHARGED_PROJECTILES) && stack.get(DataComponentTypes.CHARGED_PROJECTILES).contains(Items.AMETHYST_SHARD) || (CrossbowItem.isCharged(stack) && AllowLoadingProjectileEffect.getItems(stack).contains(Items.AMETHYST_SHARD) && !(entity instanceof PlayerEntity)) ? 1 : 0);
		ModelPredicateProviderRegistry.register(Items.CROSSBOW, Enchancement.id("torch"), (stack, world, entity, seed) -> stack.contains(DataComponentTypes.CHARGED_PROJECTILES) && stack.get(DataComponentTypes.CHARGED_PROJECTILES).contains(Items.TORCH) || (CrossbowItem.isCharged(stack) && AllowLoadingProjectileEffect.getItems(stack).contains(Items.TORCH) && !(entity instanceof PlayerEntity)) ? 1 : 0);
		HandledScreens.register(ModScreenHandlerTypes.ENCHANTING_TABLE, EnchantingTableScreen::new);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(FrozenReloadListener.INSTANCE);
		FabricLoader.getInstance().getModContainer(Enchancement.MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(Enchancement.id("alternate_air_jump"), modContainer, ResourcePackActivationType.NORMAL));
		FabricLoader.getInstance().getModContainer(Enchancement.MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(Enchancement.id("alternate_rotation_movement_burst"), modContainer, ResourcePackActivationType.NORMAL));
		initEvents();
		initPayloads();
		betterCombatLoaded = FabricLoader.getInstance().isModLoaded("bettercombat");
		if (FabricLoader.getInstance().isModLoaded("appleskin")) {
			HUDOverlayEvent.HealthRestored.EVENT.register(new BrimstoneAppleskinEvent());
		}
	}

	private void initEvents() {
		// config
		ClientTickEvents.END_WORLD_TICK.register(new CoyoteBiteEvent());
		ItemTooltipCallback.EVENT.register(new EnchantmentDescriptionsEvent());
		ItemTooltipCallback.EVENT.register(new ToggleablePassivesEvent());
		// enchantment
		HudRenderCallback.EVENT.register(new AirJumpRenderEvent());
		ItemTooltipCallback.EVENT.register(new AutomaticallyFeedsTooltipEvent());
		HudRenderCallback.EVENT.register(new BrimstoneRenderEvent());
		HudRenderCallback.EVENT.register(new ChargeJumpRenderEvent());
		HudRenderCallback.EVENT.register(new DirectionMovementBurstRenderEvent());
		ItemTooltipCallback.EVENT.register(new RageRenderEvent());
		HudRenderCallback.EVENT.register(new RotationMovementBurstRenderEvent());
	}

	private void initPayloads() {
		// internal
		ClientPlayNetworking.registerGlobalReceiver(EnforceConfigMatchPayload.ID, new EnforceConfigMatchPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncEnchantingMaterialMapPayload.ID, new SyncEnchantingMaterialMapPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncEnchantingTableBookshelfCountPayload.ID, new SyncEnchantingTableBookshelfCountPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncEnchantingTableCostPayload.ID, new SyncEnchantingTableCostPayload.Receiver());
		// enchantment
		ClientPlayNetworking.registerGlobalReceiver(AddAirJumpParticlesPayload.ID, new AddAirJumpParticlesPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(AddMoltenParticlesPayload.ID, new AddMoltenParticlesPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(AddMovementBurstParticlesPayload.ID, new AddMovementBurstParticlesPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(PlayBrimstoneFireSoundPayload.ID, new PlayBrimstoneFireSoundPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(PlayBrimstoneTravelSoundPayload.ID, new PlayBrimstoneTravelSoundPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(PlaySparkSoundPayload.ID, new PlaySparkSoundPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(ResetFrozenTicksPayload.ID, new ResetFrozenTicksPayload.Receiver());
		ClientPlayNetworking.registerGlobalReceiver(SyncFrozenPlayerSlimStatusS2CPayload.ID, new SyncFrozenPlayerSlimStatusS2CPayload.Receiver());
	}

	private static KeyBinding registerKeyBinding(Supplier<KeyBinding> supplier) {
		KeyBinding keyBinding = supplier.get();
		EnchancementClientUtil.VANILLA_AND_ENCHANCEMENT_KEYBINDINGS.add(keyBinding);
		return keyBinding;
	}
}
