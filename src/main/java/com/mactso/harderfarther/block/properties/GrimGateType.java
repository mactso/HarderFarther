package com.mactso.harderfarther.block.properties;

import net.minecraft.util.StringRepresentable;

public enum GrimGateType implements StringRepresentable{
	
	   FLOOR("floor"),
	   DOOR("door");

	   private final String name;

	   private GrimGateType(String s) {
	      this.name = s;
	   }

	   public String toString() {
	      return this.getSerializedName();
	   }

	   public String getSerializedName() {
	      return this.name;
	   }
}
