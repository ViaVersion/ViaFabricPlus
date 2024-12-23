/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.Protocol1_16_4To1_17;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.fixes.versioned.ActionResultException1_12_2;
import com.viaversion.viafabricplus.fixes.versioned.ClientPlayerInteractionManager1_18_2;
import com.viaversion.viafabricplus.injection.access.IClientConnection;
import com.viaversion.viafabricplus.injection.access.IClientPlayerInteractionManager;
import com.viaversion.viafabricplus.injection.access.IScreenHandler;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

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

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Shadow
    private GameMode gameMode;

    @Unique
    private ItemStack viaFabricPlus$oldCursorStack;

    @Unique
    private List<ItemStack> viaFabricPlus$oldItems;

    @Unique
    private final ClientPlayerInteractionManager1_18_2 viaFabricPlus$1_18_2InteractionManager = new ClientPlayerInteractionManager1_18_2();

    @Inject(method = {"pickItemFromBlock", "pickItemFromEntity"}, at = @At("HEAD"), cancellable = true)
    private void pickItemClientside(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            ViaFabricPlus.global().getLogger().error("Directly calling pickItemFromBlock or pickItemFromEntity is not supported in <=1.21.3. Use MinecraftClient#doItemPick instead.");
            ci.cancel();
        }
    }

    @Redirect(method = "interactBlockInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/util/ActionResult;CONSUME:Lnet/minecraft/util/ActionResult$Success;"))
    private ActionResult.Success changeSpectatorAction() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.CONSUME;
        }
    }

    @Inject(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V", shift = At.Shift.AFTER))
    private void sendPlayerPosPacket(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_17, ProtocolVersion.v1_20_5)) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround(), player.horizontalCollision));
        }
    }

    @Inject(method = "getBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
    private void changeCalculation(CallbackInfoReturnable<Integer> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            cir.setReturnValue((int) (this.currentBreakingProgress * 10.0F) - 1);
        }
    }

    @Inject(method = "sendSequencedPacket", at = @At("HEAD"))
    private void trackPlayerAction(ClientWorld world, SequencedPacketCreator packetCreator, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_14_4, ProtocolVersion.v1_18_2) && packetCreator instanceof PlayerActionC2SPacket playerActionC2SPacket) {
            this.viaFabricPlus$1_18_2InteractionManager.trackPlayerAction(playerActionC2SPacket.getAction(), playerActionC2SPacket.getPos());
        }
    }

    @Redirect(method = {"attackBlock", "cancelBlockBreaking"}, at = @At(value = "NEW", target = "(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;"))
    private PlayerActionC2SPacket trackPlayerAction(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_14_4, ProtocolVersion.v1_18_2)) {
            this.viaFabricPlus$1_18_2InteractionManager.trackPlayerAction(action, pos);
        }
        return new PlayerActionC2SPacket(action, pos, direction);
    }

    @ModifyVariable(method = "clickSlot", at = @At(value = "STORE"), ordinal = 0)
    private List<ItemStack> captureOldItems(List<ItemStack> oldItems) {
        viaFabricPlus$oldCursorStack = client.player.currentScreenHandler.getCursorStack().copy();
        return this.viaFabricPlus$oldItems = oldItems;
    }

    @WrapWithCondition(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private boolean handleWindowClick1_16_5(ClientPlayNetworkHandler instance, Packet<?> packet) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_16_4) && packet instanceof ClickSlotC2SPacket clickSlot) {
            ItemStack slotItemBeforeModification;
            if (this.viaFabricPlus$shouldBeEmpty(clickSlot.getActionType(), clickSlot.getSlot())) {
                slotItemBeforeModification = ItemStack.EMPTY;
            } else if (clickSlot.getSlot() < 0 || clickSlot.getSlot() >= viaFabricPlus$oldItems.size()) {
                slotItemBeforeModification = viaFabricPlus$oldCursorStack;
            } else {
                slotItemBeforeModification = viaFabricPlus$oldItems.get(clickSlot.getSlot());
            }

            final PacketWrapper containerClick = PacketWrapper.create(ServerboundPackets1_16_2.CONTAINER_CLICK, ((IClientConnection) networkHandler.getConnection()).viaFabricPlus$getUserConnection());
            containerClick.write(Types.UNSIGNED_BYTE, (short) clickSlot.getSyncId());
            containerClick.write(Types.SHORT, (short) clickSlot.getSlot());
            containerClick.write(Types.BYTE, (byte) clickSlot.getButton());
            containerClick.write(Types.SHORT, ((IScreenHandler) client.player.currentScreenHandler).viaFabricPlus$incrementAndGetActionId());
            containerClick.write(Types.VAR_INT, clickSlot.getActionType().ordinal());
            containerClick.write(Types.ITEM1_13_2, ItemTranslator.mcToVia(slotItemBeforeModification, ProtocolVersion.v1_16_4));
            containerClick.scheduleSendToServer(Protocol1_16_4To1_17.class);

            viaFabricPlus$oldCursorStack = null;
            viaFabricPlus$oldItems = null;
            return false;
        }

        return true;
    }

    @Redirect(method = {"method_41936", "method_41935"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;breakBlock(Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean checkFireBlock(ClientPlayerInteractionManager instance, BlockPos pos, @Local(argsOnly = true) Direction direction) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            return !this.viaFabricPlus$extinguishFire(pos, direction) && instance.breakBlock(pos);
        } else {
            return instance.breakBlock(pos);
        }
    }

    @Inject(method = "breakBlock", at = @At("TAIL"))
    private void resetBlockBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_3)) {
            this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
        }
    }

    @Inject(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2, shift = At.Shift.BEFORE))
    private void interactBlock1_12_2(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final ItemStack itemStack = player.getStackInHand(hand);
            BlockHitResult checkHitResult = hitResult;
            if (itemStack.getItem() instanceof BlockItem) {
                final BlockState clickedBlock = this.client.world.getBlockState(hitResult.getBlockPos());
                if (clickedBlock.getBlock().equals(Blocks.SNOW)) {
                    if (clickedBlock.get(SnowBlock.LAYERS) == 1) {
                        checkHitResult = hitResult.withSide(Direction.UP);
                    }
                }
                final ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, checkHitResult);
                final ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
                if (!itemPlacementContext.canPlace() || ((BlockItem) itemPlacementContext.getStack().getItem()).getPlacementState(itemPlacementContext) == null) {
                    throw new ActionResultException1_12_2(ActionResult.PASS);
                }
            }

            this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult, 0));
            if (itemStack.isEmpty()) {
                throw new ActionResultException1_12_2(ActionResult.PASS);
            }
            final ItemUsageContext itemUsageContext = new ItemUsageContext(player, hand, checkHitResult);
            ActionResult actionResult;
            if (this.gameMode.isCreative()) {
                final int count = itemStack.getCount();
                actionResult = itemStack.useOnBlock(itemUsageContext);
                itemStack.setCount(count);
            } else {
                actionResult = itemStack.useOnBlock(itemUsageContext);
            }
            if (!actionResult.isAccepted()) {
                actionResult = ActionResult.PASS; // In <= 1.12.2 FAIL is the same as PASS
            }
            throw new ActionResultException1_12_2(actionResult);
        }
    }

    @Redirect(method = "method_41929", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult eitherSuccessOrPass(ItemStack instance, World world, PlayerEntity user, Hand hand, @Local ItemStack itemStack) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            final int count = instance.getCount();

            final ActionResult actionResult = instance.use(world, user, hand);
            final ItemStack output;
            if (actionResult instanceof ActionResult.Success success) {
                output = Objects.requireNonNullElseGet(success.getNewHandStack(), () -> user.getStackInHand(hand));
            } else {
                output = user.getStackInHand(hand);
            }

            // In <= 1.8, ActionResult weren't a thing and interactItem simply returned either true or false
            // depending on if the input and output item are equal or not
            final boolean accepted = !output.isEmpty() && (output != itemStack || output.getCount() != count);
            if (actionResult.isAccepted() == accepted) {
                return actionResult;
            } else {
                return accepted ? ActionResult.SUCCESS.withNewHandStack(output) : ActionResult.PASS;
            }
        } else {
            return instance.use(world, user, hand);
        }
    }

    @Inject(method = "method_41929", at = @At("HEAD"))
    private void trackLastUsedItem(Hand hand, PlayerEntity playerEntity, MutableObject<ActionResult> mutableObject, int sequence, CallbackInfoReturnable<Packet<?>> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = playerEntity.getStackInHand(hand).copy();
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandItemInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !Hand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandBlockPlace(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !Hand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    /**
     * @author RK_01
     * @reason Block place fix
     */
    @Overwrite
    private Packet<?> method_41933(MutableObject<ActionResult> mutableObject, ClientPlayerEntity clientPlayerEntity, Hand hand, BlockHitResult blockHitResult, int sequence) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = clientPlayerEntity.getStackInHand(hand).copy();
        }
        try {
            mutableObject.setValue(this.interactBlockInternal(clientPlayerEntity, hand, blockHitResult));
            return new PlayerInteractBlockC2SPacket(hand, blockHitResult, sequence);
        } catch (ActionResultException1_12_2 e) {
            mutableObject.setValue(e.getActionResult());
            throw e;
        }
    }

    @Redirect(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"))
    private void catchPacketCancelException(ClientPlayerInteractionManager instance, ClientWorld world, SequencedPacketCreator packetCreator) {
        try {
            this.sendSequencedPacket(world, packetCreator);
        } catch (ActionResultException1_12_2 ignored) {
        }
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

    @Inject(method = "hasExperienceBar", at = @At("HEAD"), cancellable = true)
    private void removeExperienceBar(CallbackInfoReturnable<Boolean> cir) {
        if (VisualSettings.global().hideModernHUDElements.isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean viaFabricPlus$extinguishFire(BlockPos blockPos, final Direction direction) {
        blockPos = blockPos.offset(direction);
        if (this.client.world.getBlockState(blockPos).getBlock() == Blocks.FIRE) {
            this.client.world.syncWorldEvent(this.client.player, 1009, blockPos, 0);
            this.client.world.removeBlock(blockPos, false);
            return true;
        }
        return false;
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

    @Override
    public ClientPlayerInteractionManager1_18_2 viaFabricPlus$get1_18_2InteractionManager() {
        return this.viaFabricPlus$1_18_2InteractionManager;
    }

}
