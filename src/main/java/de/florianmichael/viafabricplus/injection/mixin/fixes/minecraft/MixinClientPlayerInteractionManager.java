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

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import de.florianmichael.viafabricplus.definition.ClientPlayerInteractionManager1_18_2;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.raphimc.vialoader.util.VersionEnum;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import de.florianmichael.viafabricplus.injection.access.IClientPlayerEntity;
import de.florianmichael.viafabricplus.injection.access.IScreenHandler;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.protocolhack.provider.viaversion.ViaFabricPlusHandItemProvider;
import de.florianmichael.viafabricplus.protocolhack.util.ItemTranslator;
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
import net.minecraft.util.math.BlockPos;
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

    @Shadow
    protected abstract ActionResult interactBlockInternal(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult);

    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Shadow
    private BlockPos currentBreakingPos;

    @Unique
    private ItemStack viafabricplus_oldCursorStack;

    @Unique
    private List<ItemStack> viafabricplus_oldItems;

    @Inject(method = "breakBlock", at = @At("TAIL"))
    public void resetBlockBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_3)) {
            this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
        }
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && player instanceof IClientPlayerEntity) {
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
        if (type == SlotActionType.QUICK_MOVE && ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_11_1to1_11_2)) return true;

        // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == SlotActionType.PICKUP && slot == -999;
    }

    @WrapWithCondition(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private boolean modifySlotClickPacket(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_16_4tor1_16_5) && packet instanceof ClickSlotC2SPacket clickSlot) {
            ItemStack slotItemBeforeModification;

            if (this.viafabricplus_shouldEmpty(clickSlot.getActionType(), clickSlot.getSlot()))
                slotItemBeforeModification = ItemStack.EMPTY;
            else if (clickSlot.getSlot() < 0 || clickSlot.getSlot() >= viafabricplus_oldItems.size())
                slotItemBeforeModification = viafabricplus_oldCursorStack;
            else
                slotItemBeforeModification = viafabricplus_oldItems.get(clickSlot.getSlot());

            final UserConnection userConnection = networkHandler.getConnection().channel.attr(ProtocolHack.LOCAL_VIA_CONNECTION).get();
            final short syncId = (short) clickSlot.getSyncId();
            final short slot = (short) clickSlot.getSlot();
            final byte button = (byte) clickSlot.getButton();
            final short lastActionId = ((IScreenHandler) client.player.currentScreenHandler).viafabricplus_getAndIncrementLastActionId();
            final int actionType = clickSlot.getActionType().ordinal();
            final Item item = ItemTranslator.MC_TO_VIA_LATEST_TO_TARGET(slotItemBeforeModification, VersionEnum.r1_16);

            userConnection.getChannel().eventLoop().submit(() -> {
                final PacketWrapper clickSlotPacket = PacketWrapper.create(ServerboundPackets1_16_2.CLICK_WINDOW, userConnection);

                clickSlotPacket.write(Type.UNSIGNED_BYTE, syncId);
                clickSlotPacket.write(Type.SHORT, slot);
                clickSlotPacket.write(Type.BYTE, button);
                clickSlotPacket.write(Type.SHORT, lastActionId);
                clickSlotPacket.write(Type.VAR_INT, actionType);
                clickSlotPacket.write(Type.FLAT_VAR_INT_ITEM, item);

                try {
                    clickSlotPacket.sendToServer(Protocol1_17To1_16_4.class);
                } catch (Exception ignored) {
                }
            });

            viafabricplus_oldCursorStack = null;
            viafabricplus_oldItems = null;
            return false;
        }
        return true;
    }

    @WrapWithCondition(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0)))
    private boolean redirectInteractItem(ClientPlayNetworkHandler instance, Packet<?> packet) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_17);
    }

    @Inject(method = "interactItem", at = @At("HEAD"))
    public void trackLastUsedItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ViaFabricPlusHandItemProvider.lastUsedItem = player.getStackInHand(hand).copy();
    }

    @Inject(method = "interactBlock", at = @At("HEAD"))
    public void trackLastUsedBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ViaFabricPlusHandItemProvider.lastUsedItem = player.getStackInHand(hand).copy();
    }

    @Unique
    private ActionResult viafabricplus_actionResult;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void cacheActionResult(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            this.viafabricplus_actionResult = this.interactBlockInternal(player, hand, hitResult);

            if (this.viafabricplus_actionResult == ActionResult.FAIL) {
                cir.setReturnValue(this.viafabricplus_actionResult);
            }
        }
    }

    @Redirect(method = "method_41933", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactBlockInternal(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult provideCachedResult(ClientPlayerInteractionManager instance, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_2)) {
            return this.viafabricplus_actionResult;
        }
        return interactBlockInternal(player, hand, hitResult);
    }

    @Inject(method = "sendSequencedPacket", at = @At("HEAD"))
    public void handleBlockAcknowledgements(ClientWorld world, SequencedPacketCreator packetCreator, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isBetweenInclusive(VersionEnum.r1_14_4, VersionEnum.r1_18_2) && packetCreator instanceof PlayerActionC2SPacket playerActionC2SPacket) {
            ClientPlayerInteractionManager1_18_2.trackBlockAction(playerActionC2SPacket.getAction(), playerActionC2SPacket.getPos());
        }
    }
}
