package com.mactso.harderfarther.utility;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Player extends PlayerEntity {

	public Player(World p_i241920_1_, BlockPos p_i241920_2_, float p_i241920_3_, GameProfile p_i241920_4_) {
		super(p_i241920_1_, p_i241920_2_, p_i241920_3_, p_i241920_4_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSpectator() {
		return this.isSpectator();
	}

	@Override
	public boolean isCreative() {
		return this.isCreative();
	}

}
