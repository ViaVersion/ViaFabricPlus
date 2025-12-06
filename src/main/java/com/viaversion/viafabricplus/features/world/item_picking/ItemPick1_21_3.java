/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.features.world.item_picking;

import com.mojang.logging.LogUtils;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.Protocol1_21_2To1_21_4;
import com.viaversion.viaversion.protocols.v1_21to1_21_2.packet.ServerboundPackets1_21_2;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;

public final class ItemPick1_21_3 {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static void addPickBlock(final Inventory inventory, final ItemStack stack) {
        final int index = inventory.findSlotMatchingItem(stack);
        if (Inventory.isHotbarSlot(index)) {
            inventory.setSelectedSlot(index);
        } else if (index != -1) {
            inventory.pickSlot(index);
        } else {
            inventory.addAndPickItem(stack);
        }
    }

    private static void addBlockEntityNbt(final ItemStack stack, final BlockEntity blockEntity, final RegistryAccess manager) {
        try (final ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), LOGGER)) {
            final TagValueOutput view = TagValueOutput.createWithContext(logging, manager);
            blockEntity.saveMetadata(view);
            BlockItem.setBlockEntityData(stack, blockEntity.getType(), view);
            stack.applyComponents(blockEntity.collectComponents());
        }
    }

    public static void doItemPick(final Minecraft client) {
        final boolean creativeMode = client.player.getAbilities().instabuild;

        ItemStack itemStack;
        final HitResult crosshairTarget = client.hitResult;
        if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
            final BlockState blockState = client.level.getBlockState(blockPos);
            if (blockState.isAir()) {
                return;
            }

            final Block block = blockState.getBlock();
            itemStack = block.getCloneItemStack(client.level, blockPos, blockState, false);
            if (itemStack.isEmpty()) {
                return;
            }

            if (creativeMode && client.hasControlDown() && blockState.hasBlockEntity()) {
                final BlockEntity blockEntity = client.level.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    addBlockEntityNbt(itemStack, blockEntity, client.level.registryAccess());
                }
            }
        } else {
            if (crosshairTarget.getType() != HitResult.Type.ENTITY || !creativeMode) {
                return;
            }

            final Entity entity = ((EntityHitResult) crosshairTarget).getEntity();
            itemStack = entity.getPickResult();
            if (itemStack == null) {
                return;
            }
        }

        if (itemStack.isEmpty()) {
            return;
        }

        final Inventory inventory = client.player.getInventory();
        final int index = inventory.findSlotMatchingItem(itemStack);
        if (creativeMode) {
            addPickBlock(inventory, itemStack);
            client.gameMode.handleCreativeModeItemAdd(client.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + inventory.getSelectedSlot());
        } else if (index != -1) {
            if (Inventory.isHotbarSlot(index)) {
                inventory.setSelectedSlot(index);
                return;
            }

            final PacketWrapper pickFromInventory = PacketWrapper.create(ServerboundPackets1_21_2.PICK_ITEM, ProtocolTranslator.getPlayNetworkUserConnection());
            pickFromInventory.write(Types.VAR_INT, index);
            pickFromInventory.scheduleSendToServer(Protocol1_21_2To1_21_4.class);
        }
    }

}
