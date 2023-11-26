/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.google.common.collect.ImmutableSet;
import de.florianmichael.viafabricplus.fixes.Material1_19_4;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(AxeItem.class)
public abstract class MixinAxeItem extends MiningToolItem {

    @Unique
    private static final Set<Block> _b1_8_1_EFFECTIVE_BLOCKS = ImmutableSet.of(Blocks.OAK_PLANKS, Blocks.BOOKSHELF, Blocks.OAK_LOG, Blocks.CHEST);

    @Unique
    private static final Set<Block> _r1_16_5_EFFECTIVE_BLOCKS = ImmutableSet.of(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);

    @Unique
    private static final Set<Material1_19_4> _r1_16_5_EFFECTIVE_MATERIALS = ImmutableSet.of(Material1_19_4.WOOD, Material1_19_4.NETHER_WOOD, Material1_19_4.PLANT, Material1_19_4.REPLACEABLE_PLANT, Material1_19_4.BAMBOO, Material1_19_4.GOURD);

    public MixinAxeItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void disableUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Override
    public boolean isSuitableFor(BlockState state) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5)) {
            return false;
        }

        return super.isSuitableFor(state);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.b1_8tob1_8_1)) {
            return _b1_8_1_EFFECTIVE_BLOCKS.contains(state.getBlock()) ? this.miningSpeed : 1.0F;
        } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5)) {
            return _r1_16_5_EFFECTIVE_MATERIALS.contains(Material1_19_4.getMaterial(state)) ? this.miningSpeed : _r1_16_5_EFFECTIVE_BLOCKS.contains(state.getBlock()) ? this.miningSpeed : 1.0F;
        }

        return super.getMiningSpeedMultiplier(stack, state);
    }

}
