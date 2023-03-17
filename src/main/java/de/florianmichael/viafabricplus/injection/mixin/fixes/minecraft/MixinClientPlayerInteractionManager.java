/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import de.florianmichael.viafabricplus.injection.access.IClientPlayerEntity;
import de.florianmichael.viafabricplus.injection.access.IScreenHandler;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusHandItemProvider;
import de.florianmichael.viafabricplus.util.ItemTranslator;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow protected abstract ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Unique
    private ItemStack viafabricplus_oldCursorStack;

    @Unique
    private List<ItemStack> viafabricplus_oldItems;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8) && player instanceof IClientPlayerEntity) {
            player.swingHand(Hand.MAIN_HAND);
            ((IClientPlayerEntity) player).viafabricplus_cancelSwingOnce();
        }
    }

    @ModifyVariable(method = "clickSlot", at = @At(value = "STORE"), ordinal = 0)
    private List<ItemStack> captureOldItems(List<ItemStack> oldItems) {
        assert client.player != null;
        viafabricplus_oldCursorStack = client.player.currentScreenHandler.getCursorStack().copy();
        return this.viafabricplus_oldItems = oldItems;
    }

    // Special Cases
    @Unique
    private boolean viafabricplus_shouldEmpty(final SlotActionType type, final int slot) {
        // quick craft always uses empty stack for verification
        if (type == SlotActionType.QUICK_CRAFT) return true;

        // quick move always uses empty stack for verification since 1.12
        if (type == SlotActionType.QUICK_MOVE && ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_11_1)) return true;

        // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == SlotActionType.PICKUP && slot == -999;
    }

    @Redirect(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void modifySlotClickPacket(ClientPlayNetworkHandler instance, Packet<?> packet) {
        try {
            if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4) && packet instanceof ClickSlotC2SPacket clickSlot) {
                ItemStack slotItemBeforeModification;

                if (this.viafabricplus_shouldEmpty(clickSlot.getActionType(), clickSlot.getSlot()))
                    slotItemBeforeModification = ItemStack.EMPTY;
                else if (clickSlot.getSlot() < 0 || clickSlot.getSlot() >= viafabricplus_oldItems.size())
                    slotItemBeforeModification = viafabricplus_oldCursorStack;
                else
                    slotItemBeforeModification = viafabricplus_oldItems.get(clickSlot.getSlot());

                final PacketWrapper clickSlotPacket = PacketWrapper.create(ServerboundPackets1_16_2.CLICK_WINDOW, networkHandler.getConnection().channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get());

                clickSlotPacket.write(Type.UNSIGNED_BYTE, (short) clickSlot.getSyncId());
                clickSlotPacket.write(Type.SHORT, (short) clickSlot.getSlot());
                clickSlotPacket.write(Type.BYTE, (byte) clickSlot.getButton());
                assert client.player != null;
                clickSlotPacket.write(Type.SHORT, ((IScreenHandler) client.player.currentScreenHandler).viafabricplus_getAndIncrementLastActionId());
                clickSlotPacket.write(Type.VAR_INT, clickSlot.getActionType().ordinal());
                clickSlotPacket.write(Type.FLAT_VAR_INT_ITEM, ItemTranslator.minecraftToViaVersion(clickSlotPacket.user(), slotItemBeforeModification, ProtocolVersion.v1_16.getVersion()));

                clickSlotPacket.sendToServer(Protocol1_17To1_16_4.class);

                viafabricplus_oldCursorStack = null;
                viafabricplus_oldItems = null;

                return;
            }
        } catch (Exception ignored) {
        }

        instance.sendPacket(packet);
    }

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0)))
    private void redirectInteractItem(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_17)) {
            clientPlayNetworkHandler.sendPacket(packet);
        }
    }

    @Inject(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void injectInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ViaFabricPlusHandItemProvider.lastUsedItem = player.getStackInHand(hand).copy();
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", shift = At.Shift.BEFORE))
    public void injectInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ViaFabricPlusHandItemProvider.lastUsedItem = player.getStackInHand(hand).copy();
    }

    @Unique
    private ActionResult viafabricplus_actionResult;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void cacheActionResult(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            this.viafabricplus_actionResult = this.interactBlockInternal(player, hand, hitResult);

            if (this.viafabricplus_actionResult == ActionResult.FAIL) {
                cir.setReturnValue(this.viafabricplus_actionResult);
            }
        }
    }

    @Redirect(method = "method_41933", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlockInternal(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult provideCachedResult(ClientPlayerInteractionManager instance, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return this.viafabricplus_actionResult;
        }
        return interactBlockInternal(player, hand, hitResult);
    }
}
