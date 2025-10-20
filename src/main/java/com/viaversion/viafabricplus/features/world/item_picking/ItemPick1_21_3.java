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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public final class ItemPick1_21_3 {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static void addPickBlock(final PlayerInventory inventory, final ItemStack stack) {
        final int index = inventory.getSlotWithStack(stack);
        if (PlayerInventory.isValidHotbarIndex(index)) {
            inventory.setSelectedSlot(index);
        } else if (index != -1) {
            inventory.swapSlotWithHotbar(index);
        } else {
            inventory.swapStackWithHotbar(stack);
        }
    }

    private static void addBlockEntityNbt(final ItemStack stack, final BlockEntity blockEntity, final DynamicRegistryManager manager) {
        try (final ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER)) {
            final NbtWriteView view = NbtWriteView.create(logging, manager);
            blockEntity.writeIdentifyingData(view);
            BlockItem.setBlockEntityData(stack, blockEntity.getType(), view);
            stack.applyComponentsFrom(blockEntity.createComponentMap());
        }
    }

    public static void doItemPick(final MinecraftClient client) {
        final boolean creativeMode = client.player.getAbilities().creativeMode;

        ItemStack itemStack;
        final HitResult crosshairTarget = client.crosshairTarget;
        if (crosshairTarget.getType() == HitResult.Type.BLOCK) {
            final BlockPos blockPos = ((BlockHitResult) crosshairTarget).getBlockPos();
            final BlockState blockState = client.world.getBlockState(blockPos);
            if (blockState.isAir()) {
                return;
            }

            final Block block = blockState.getBlock();
            itemStack = block.getPickStack(client.world, blockPos, blockState, false);
            if (itemStack.isEmpty()) {
                return;
            }

            if (creativeMode && client.isCtrlPressed() && blockState.hasBlockEntity()) {
                final BlockEntity blockEntity = client.world.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    addBlockEntityNbt(itemStack, blockEntity, client.world.getRegistryManager());
                }
            }
        } else {
            if (crosshairTarget.getType() != HitResult.Type.ENTITY || !creativeMode) {
                return;
            }

            final Entity entity = ((EntityHitResult) crosshairTarget).getEntity();
            itemStack = entity.getPickBlockStack();
            if (itemStack == null) {
                return;
            }
        }

        if (itemStack.isEmpty()) {
            return;
        }

        final PlayerInventory inventory = client.player.getInventory();
        final int index = inventory.getSlotWithStack(itemStack);
        if (creativeMode) {
            addPickBlock(inventory, itemStack);
            client.interactionManager.clickCreativeStack(client.player.getStackInHand(Hand.MAIN_HAND), 36 + inventory.getSelectedSlot());
        } else if (index != -1) {
            if (PlayerInventory.isValidHotbarIndex(index)) {
                inventory.setSelectedSlot(index);
                return;
            }

            final PacketWrapper pickFromInventory = PacketWrapper.create(ServerboundPackets1_21_2.PICK_ITEM, ProtocolTranslator.getPlayNetworkUserConnection());
            pickFromInventory.write(Types.VAR_INT, index);
            pickFromInventory.scheduleSendToServer(Protocol1_21_2To1_21_4.class);
        }
    }

}
