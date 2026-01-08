/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.block;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.Xoroshiro128PlusPlus;
import net.minecraft.world.phys.Vec3;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.Properties.class)
public abstract class MixinBlockBehaviour_Properties {

    // Bedrock random offset parameters for bamboo (uses BlockRandomOffsetDefaults::XZ)
    @Unique
    private static final float viaFabricPlus$OFFSET_MIN = -0.25f; // -4/16

    @Unique
    private static final float viaFabricPlus$OFFSET_MAX = 0.25f;  // 4/16

    @Unique
    private static final int viaFabricPlus$STEPS = 16; // Quantization steps

    @Shadow
    BlockBehaviour.OffsetFunction offsetFunction;

    @Inject(method = "offsetType", at = @At(value = "RETURN"))
    private void fixBlockOffsets(BlockBehaviour.OffsetType offsetType, CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) && offsetType != BlockBehaviour.OffsetType.NONE) {
            this.offsetFunction = (state, pos) -> viaFabricPlus$randomlyModifyPosition(pos, offsetType);
        }
    }

    /**
     * Bedrock position hash algorithm
     * Only uses X and Z coordinates (Y is NOT used)
     */
    @Unique
    private static long viaFabricPlus$positionHash(final int x, final int z) {
        // Step 1: Initial hash from X and Z
        final long v1 = (116129781L * z) ^ ((0x2FC20F00000001L * Integer.toUnsignedLong(x)) >> 32);

        // Step 2: LCG-style mixing
        // Note: Bedrock uses cdqe instruction which sign-extends low 32 bits to 64 bits
        final long temp = (v1 * (42317861L * v1 + 11L)) >>> 16;
        return (int) temp ^ 0x6A09E667F3BCC909L;
    }

    /**
     * Convert random long to float in [0, 1)
     * Bedrock: (random >>> 40) * 2^-24
     */
    @Unique
    private static float viaFabricPlus$randomToFloat(final long random) {
        return (random >>> 40) * 5.9604645e-8f;
    }

    /**
     * Calculate offset value with quantization to discrete steps (Bedrock algorithm)
     */
    @Unique
    private static float viaFabricPlus$calculateOffsetValue(final float min, final float max, final float random) {
        if (min >= max) {
            return min;
        }

        if (viaFabricPlus$STEPS == 1) {
            return (min + max) * 0.5F;
        } else if (viaFabricPlus$STEPS > 1) {
            final float range = max - min;
            final float stepSize = range / (viaFabricPlus$STEPS - 1);
            final float index = (float) Math.floor(viaFabricPlus$STEPS * random);
            return min + index * stepSize;
        } else {
            return min + (max - min) * random;
        }
    }

    /**
     * Calculate random offset for a given position
     */
    @Unique
    private static Vec3 viaFabricPlus$randomlyModifyPosition(final BlockPos pos, final BlockBehaviour.OffsetType type) {
        // Use Bedrock's custom position hash
        final long seed = viaFabricPlus$positionHash(pos.getX(), pos.getZ());

        // Use Minecraft's SplitMix64 (mixStafford13) to generate the PRNG state
        final long s0 = RandomSupport.mixStafford13(seed);
        final long s1 = RandomSupport.mixStafford13(seed + RandomSupport.GOLDEN_RATIO_64);

        // Use Minecraft's Xoroshiro128PlusPlus
        final Xoroshiro128PlusPlus prng = new Xoroshiro128PlusPlus(s0, s1);

        // Generate X offset with quantization
        final float offsetX = viaFabricPlus$calculateOffsetValue(viaFabricPlus$OFFSET_MIN, viaFabricPlus$OFFSET_MAX, viaFabricPlus$randomToFloat(prng.nextLong()));
        final float offsetY = switch (type) {
            case XZ -> {
                // Y offset - must consume random even though Y range is (0,0) in XZ mode
                // BDS advances PRNG state regardless of whether the offset is computed
                prng.nextLong();

                yield 0;
            }
            case XYZ -> {
                // Generate Y offset with quantization
                yield viaFabricPlus$calculateOffsetValue(-0.2f, 0, viaFabricPlus$randomToFloat(prng.nextLong()));
            }
            case NONE -> 0;
        };


        // Generate Z offset with quantization
        final float offsetZ = viaFabricPlus$calculateOffsetValue(viaFabricPlus$OFFSET_MIN, viaFabricPlus$OFFSET_MAX, viaFabricPlus$randomToFloat(prng.nextLong()));
        return new Vec3(offsetX, offsetY, offsetZ);
    }

}
