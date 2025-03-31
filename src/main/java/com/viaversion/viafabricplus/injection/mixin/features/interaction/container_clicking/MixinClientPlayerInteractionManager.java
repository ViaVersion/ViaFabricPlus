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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.container_clicking;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.injection.access.interaction.container_clicking.IScreenHandler;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.Types1_21_4;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.packet.ServerboundPackets1_21_4;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private ItemStack viaFabricPlus$oldCursorStack;

    @Unique
    private List<ItemStack> viaFabricPlus$oldItems;

    @ModifyVariable(method = "clickSlot", at = @At(value = "STORE"), ordinal = 0)
    private List<ItemStack> captureOldItems(List<ItemStack> oldItems) {
        viaFabricPlus$oldCursorStack = client.player.currentScreenHandler.getCursorStack().copy();
        return this.viaFabricPlus$oldItems = oldItems;
    }

    @WrapWithCondition(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private boolean handleWindowClick(ClientPlayNetworkHandler instance, Packet<?> packet) {
        final ClickSlotC2SPacket clickSlotPacket = (ClickSlotC2SPacket) packet;

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            // Contains item before modification and not the actual item
            viaFabricPlus$clickSlot1_16_5(clickSlotPacket);
            return false;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            // Contain the actual item and not only the item hash
            viaFabricPlus$clickSlot1_21_4(clickSlotPacket);
            return false;
        }

        return true;
    }

    @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    private void removeClickActions(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_5tob1_5_2) && !actionType.equals(SlotActionType.PICKUP)) {
            ci.cancel();
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7) && !actionType.equals(SlotActionType.PICKUP) && !actionType.equals(SlotActionType.QUICK_MOVE) && !actionType.equals(SlotActionType.SWAP) && !actionType.equals(SlotActionType.CLONE)) {
            ci.cancel();
        }
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && actionType == SlotActionType.SWAP && button == 40) { // Pressing 'F' in inventory
            ci.cancel();
        }
    }

    @Unique
    private void viaFabricPlus$clickSlot1_21_4(final ClickSlotC2SPacket packet) {
        final PacketWrapper containerClick = PacketWrapper.create(ServerboundPackets1_21_4.CONTAINER_CLICK, ProtocolTranslator.getPlayNetworkUserConnection());
        containerClick.write(Types.VAR_INT, packet.syncId());
        containerClick.write(Types.VAR_INT, packet.revision());
        containerClick.write(Types.SHORT, packet.slot());
        containerClick.write(Types.BYTE, packet.button());
        containerClick.write(Types.VAR_INT, packet.actionType().getIndex());

        final Int2ObjectMap<ItemStackHash> stacks = packet.modifiedStacks();
        containerClick.write(Types.VAR_INT, stacks.size());
        for (int i = 0; i < stacks.size(); i++) {
            final ItemStack itemStack = client.player.currentScreenHandler.slots.get(i).getStack();
            containerClick.write(Types.SHORT, (short) i);
            containerClick.write(Types1_21_4.ITEM, ItemTranslator.mcToVia(itemStack, ProtocolVersion.v1_21_4));
        }

        final ItemStack cursorStack = client.player.currentScreenHandler.getCursorStack();
        containerClick.write(Types1_21_4.ITEM, ItemTranslator.mcToVia(cursorStack, ProtocolVersion.v1_21_4));
        containerClick.scheduleSendToServer(Protocol1_21_4To1_21_5.class);
    }

    @Unique
    private void viaFabricPlus$clickSlot1_16_5(final ClickSlotC2SPacket packet) {
        ItemStack slotItemBeforeModification;
        if (this.viaFabricPlus$shouldBeEmpty(packet.actionType(), packet.slot())) {
            slotItemBeforeModification = ItemStack.EMPTY;
        } else if (packet.slot() < 0 || packet.slot() >= viaFabricPlus$oldItems.size()) {
            slotItemBeforeModification = viaFabricPlus$oldCursorStack;
        } else {
            slotItemBeforeModification = viaFabricPlus$oldItems.get(packet.slot());
        }

        final PacketWrapper containerClick = PacketWrapper.create(ServerboundPackets1_16_2.CONTAINER_CLICK, ProtocolTranslator.getPlayNetworkUserConnection());
        containerClick.write(Types.BYTE, (byte) packet.syncId());
        containerClick.write(Types.SHORT, packet.slot());
        containerClick.write(Types.BYTE, packet.button());
        containerClick.write(Types.SHORT, ((IScreenHandler) client.player.currentScreenHandler).viaFabricPlus$incrementAndGetActionId());
        containerClick.write(Types.VAR_INT, packet.actionType().ordinal());
        containerClick.write(Types.ITEM1_13_2, ItemTranslator.mcToVia(slotItemBeforeModification, ProtocolVersion.v1_16_4));
        containerClick.scheduleSendToServer(Protocol1_16_4To1_17.class);

        viaFabricPlus$oldCursorStack = null;
        viaFabricPlus$oldItems = null;
    }

    @Unique
    private boolean viaFabricPlus$shouldBeEmpty(final SlotActionType type, final int slot) {
        // quick craft always uses empty stack for verification
        if (type == SlotActionType.QUICK_CRAFT) return true;

        // Special case: throw always uses empty stack for verification
        if (type == SlotActionType.THROW) return true;

        // quick move always uses empty stack for verification since 1.12
        if (type == SlotActionType.QUICK_MOVE && ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_11_1))
            return true;

        // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == SlotActionType.PICKUP && slot == -999;
    }

}
