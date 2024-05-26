/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package moriyashiine.enchancement.client.render.entity;

import moriyashiine.enchancement.common.Enchancement;
import moriyashiine.enchancement.common.entity.projectile.IceShardEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class IceShardEntityRenderer extends ProjectileEntityRenderer<IceShardEntity> {
	private static final Identifier TEXTURE = Enchancement.id("textures/entity/projectiles/ice_shard.png");

	public IceShardEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(IceShardEntity entity) {
		return TEXTURE;
	}
}
