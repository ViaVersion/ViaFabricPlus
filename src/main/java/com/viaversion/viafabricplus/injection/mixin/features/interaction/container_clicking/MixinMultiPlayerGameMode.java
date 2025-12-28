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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.container_clicking;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.injection.access.interaction.container_clicking.IAbstractContainerMenu;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.VersionedTypes;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viaversion.protocols.v1_21_2to1_21_4.packet.ServerboundPackets1_21_4;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.network.HashedStack;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DataFlowIssue")
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private ItemStack viaFabricPlus$oldCursorStack;

    @Unique
    private List<ItemStack> viaFabricPlus$oldItems;

    @ModifyVariable(method = "handleInventoryMouseClick", at = @At(value = "STORE"), ordinal = 0)
    private List<ItemStack> captureOldItems(List<ItemStack> oldItems) {
        viaFabricPlus$oldCursorStack = minecraft.player.containerMenu.getCarried().copy();
        return this.viaFabricPlus$oldItems = oldItems;
    }

    @WrapWithCondition(method = "handleInventoryMouseClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private boolean handleWindowClick(ClientPacketListener instance, Packet<?> packet) {
        final ServerboundContainerClickPacket clickSlotPacket = (ServerboundContainerClickPacket) packet;

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            // Contains item before modification and not the actual item
            viaFabricPlus$clickSlot1_16_5(clickSlotPacket);
            return false;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            // Contains the actual item and not only the item hash
            viaFabricPlus$clickSlot1_21_4(clickSlotPacket);
            return false;
        }

        return true;
    }

    @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
    private void removeClickActions(int syncId, int slotId, int button, ClickType actionType, Player player, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_5tob1_5_2) && !actionType.equals(ClickType.PICKUP)) {
            ci.cancel();
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_4_6tor1_4_7) && !actionType.equals(ClickType.PICKUP) && !actionType.equals(ClickType.QUICK_MOVE) && !actionType.equals(ClickType.SWAP) && !actionType.equals(ClickType.CLONE)) {
            ci.cancel();
        }
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2) && actionType == ClickType.SWAP && button == 40) { // Pressing 'F' in inventory
            ci.cancel();
        }
    }

    @Unique
    private void viaFabricPlus$clickSlot1_21_4(final ServerboundContainerClickPacket packet) {
        final PacketWrapper containerClick = PacketWrapper.create(ServerboundPackets1_21_4.CONTAINER_CLICK, ProtocolTranslator.getPlayNetworkUserConnection());
        containerClick.write(Types.VAR_INT, packet.containerId());
        containerClick.write(Types.VAR_INT, packet.stateId());
        containerClick.write(Types.SHORT, packet.slotNum());
        containerClick.write(Types.BYTE, packet.buttonNum());
        containerClick.write(Types.VAR_INT, packet.clickType().id());

        final Int2ObjectMap<HashedStack> modifiedStacks = packet.changedSlots();
        containerClick.write(Types.VAR_INT, modifiedStacks.size());
        for (Int2ObjectMap.Entry<HashedStack> entry : modifiedStacks.int2ObjectEntrySet()) {
            final ItemStack itemStack = minecraft.player.containerMenu.slots.get(entry.getIntKey()).getItem();
            containerClick.write(Types.SHORT, (short) entry.getIntKey());
            containerClick.write(VersionedTypes.V1_21_4.item, ItemTranslator.mcToVia(itemStack, ProtocolVersion.v1_21_4));
        }

        final ItemStack cursorStack = minecraft.player.containerMenu.getCarried();
        containerClick.write(VersionedTypes.V1_21_4.item, ItemTranslator.mcToVia(cursorStack, ProtocolVersion.v1_21_4));
        containerClick.scheduleSendToServer(Protocol1_21_4To1_21_5.class);
    }

    @Unique
    private void viaFabricPlus$clickSlot1_16_5(final ServerboundContainerClickPacket packet) {
        ItemStack slotItemBeforeModification;
        if (this.viaFabricPlus$shouldBeEmpty(packet.clickType(), packet.slotNum())) {
            slotItemBeforeModification = ItemStack.EMPTY;
        } else if (packet.slotNum() < 0 || packet.slotNum() >= viaFabricPlus$oldItems.size()) {
            slotItemBeforeModification = viaFabricPlus$oldCursorStack;
        } else {
            slotItemBeforeModification = viaFabricPlus$oldItems.get(packet.slotNum());
        }

        final PacketWrapper containerClick = PacketWrapper.create(ServerboundPackets1_16_2.CONTAINER_CLICK, ProtocolTranslator.getPlayNetworkUserConnection());
        containerClick.write(Types.BYTE, (byte) packet.containerId());
        containerClick.write(Types.SHORT, packet.slotNum());
        containerClick.write(Types.BYTE, packet.buttonNum());
        containerClick.write(Types.SHORT, ((IAbstractContainerMenu) minecraft.player.containerMenu).viaFabricPlus$incrementAndGetActionId());
        containerClick.write(Types.VAR_INT, packet.clickType().ordinal());
        containerClick.write(Types.ITEM1_13_2, ItemTranslator.mcToVia(slotItemBeforeModification, ProtocolVersion.v1_16_4));
        containerClick.scheduleSendToServer(Protocol1_16_4To1_17.class);

        viaFabricPlus$oldCursorStack = null;
        viaFabricPlus$oldItems = null;
    }

    @Unique
    private boolean viaFabricPlus$shouldBeEmpty(final ClickType type, final int slot) {
        // quick craft always uses empty stack for verification
        if (type == ClickType.QUICK_CRAFT) return true;

        // Special case: throw always uses empty stack for verification
        if (type == ClickType.THROW) return true;

        // quick move always uses empty stack for verification since 1.12
        if (type == ClickType.QUICK_MOVE && ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_11_1))
            return true;

        // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == ClickType.PICKUP && slot == -999;
    }

}
