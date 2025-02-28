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

package com.viaversion.viafabricplus.injection.mixin.features.interaction.replace_block_item_use_logic;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.features.interaction.replace_block_placement_logic.ActionResultException1_12_2;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.viaversion.ViaFabricPlusHandItemProvider;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.Objects;
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
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Shadow
    private GameMode gameMode;

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

    @Inject(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 2))
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

}
