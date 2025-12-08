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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
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
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract InteractionResult performUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult);

    @Shadow
    @Final
    private ClientPacketListener connection;

    @Shadow
    private BlockPos destroyBlockPos;

    @Shadow
    private float destroyProgress;

    @Shadow
    protected abstract void startPrediction(ClientLevel world, PredictiveAction packetCreator);

    @Shadow
    private GameType localPlayerMode;

    @Redirect(method = "performUseItemOn", at = @At(value = "FIELD", target = "Lnet/minecraft/world/InteractionResult;CONSUME:Lnet/minecraft/world/InteractionResult$Success;"))
    private InteractionResult.Success changeSpectatorAction() {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Inject(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;ensureHasSentCarriedItem()V", shift = At.Shift.AFTER))
    private void sendPlayerPosPacket(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_17, ProtocolVersion.v1_20_5)) {
            this.connection.send(new ServerboundMovePlayerPacket.PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround(), player.horizontalCollision));
        }
    }

    @Inject(method = "getDestroyStage", at = @At("HEAD"), cancellable = true)
    private void changeCalculation(CallbackInfoReturnable<Integer> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19_4)) {
            cir.setReturnValue((int) (this.destroyProgress * 10.0F) - 1);
        }
    }

    @Redirect(method = {"method_41936", "method_41935"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean checkFireBlock(MultiPlayerGameMode instance, BlockPos pos, @Local(argsOnly = true) Direction direction) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            return !this.viaFabricPlus$extinguishFire(pos, direction) && instance.destroyBlock(pos);
        } else {
            return instance.destroyBlock(pos);
        }
    }

    @Inject(method = "destroyBlock", at = @At("TAIL"))
    private void resetBlockBreaking(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_3)) {
            this.destroyBlockPos = new BlockPos(this.destroyBlockPos.getX(), -1, this.destroyBlockPos.getZ());
        }
    }

    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 2))
    private void interactBlock1_12_2(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            final ItemStack itemStack = player.getItemInHand(hand);
            BlockHitResult checkHitResult = hitResult;
            if (itemStack.getItem() instanceof BlockItem) {
                final BlockState clickedBlock = this.minecraft.level.getBlockState(hitResult.getBlockPos());
                if (clickedBlock.getBlock().equals(Blocks.SNOW)) {
                    if (clickedBlock.getValue(SnowLayerBlock.LAYERS) == 1) {
                        checkHitResult = hitResult.withDirection(Direction.UP);
                    }
                }
                final UseOnContext itemUsageContext = new UseOnContext(player, hand, checkHitResult);
                final BlockPlaceContext itemPlacementContext = new BlockPlaceContext(itemUsageContext);
                if (!itemPlacementContext.canPlace() || ((BlockItem) itemPlacementContext.getItemInHand().getItem()).getPlacementState(itemPlacementContext) == null) {
                    throw new ActionResultException1_12_2(InteractionResult.PASS);
                }
            }

            this.connection.send(new ServerboundUseItemOnPacket(hand, hitResult, 0));
            if (itemStack.isEmpty()) {
                throw new ActionResultException1_12_2(InteractionResult.PASS);
            }
            final UseOnContext itemUsageContext = new UseOnContext(player, hand, checkHitResult);
            InteractionResult actionResult;
            if (this.localPlayerMode.isCreative()) {
                final int count = itemStack.getCount();
                actionResult = itemStack.useOn(itemUsageContext);
                itemStack.setCount(count);
            } else {
                actionResult = itemStack.useOn(itemUsageContext);
            }
            if (!actionResult.consumesAction()) {
                actionResult = InteractionResult.PASS; // In <= 1.12.2 FAIL is the same as PASS
            }
            throw new ActionResultException1_12_2(actionResult);
        }
    }

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandItemInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !InteractionHand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void cancelOffHandBlockPlace(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && !InteractionHand.MAIN_HAND.equals(hand)) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    @Redirect(method = "method_41929", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult eitherSuccessOrPass(ItemStack instance, Level world, Player user, InteractionHand hand, @Local ItemStack itemStack) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            final int count = instance.getCount();

            final InteractionResult actionResult = instance.use(world, user, hand);
            final ItemStack output;
            if (actionResult instanceof InteractionResult.Success success) {
                output = Objects.requireNonNullElseGet(success.heldItemTransformedTo(), () -> user.getItemInHand(hand));
            } else {
                output = user.getItemInHand(hand);
            }

            // In <= 1.8, ActionResult weren't a thing and interactItem simply returned either true or false
            // depending on if the input and output item are equal or not
            final boolean accepted = !output.isEmpty() && (output != itemStack || output.getCount() != count);
            if (actionResult.consumesAction() == accepted) {
                return actionResult;
            } else {
                return accepted ? InteractionResult.SUCCESS.heldItemTransformedTo(output) : InteractionResult.PASS;
            }
        } else {
            return instance.use(world, user, hand);
        }
    }

    @Inject(method = "method_41929", at = @At("HEAD"))
    private void trackLastUsedItem(InteractionHand hand, Player playerEntity, MutableObject<InteractionResult> mutableObject, int sequence, CallbackInfoReturnable<Packet<?>> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = playerEntity.getItemInHand(hand).copy();
        }
    }

    /**
     * @author RK_01
     * @reason Block place fix
     */
    @Overwrite
    private Packet<?> method_41933(MutableObject<InteractionResult> mutableObject, LocalPlayer clientPlayerEntity, InteractionHand hand, BlockHitResult blockHitResult, int sequence) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            ViaFabricPlusHandItemProvider.lastUsedItem = clientPlayerEntity.getItemInHand(hand).copy();
        }
        try {
            mutableObject.setValue(this.performUseItemOn(clientPlayerEntity, hand, blockHitResult));
            return new ServerboundUseItemOnPacket(hand, blockHitResult, sequence);
        } catch (ActionResultException1_12_2 e) {
            mutableObject.setValue(e.getActionResult());
            throw e;
        }
    }

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V"))
    private void catchPacketCancelException(MultiPlayerGameMode instance, ClientLevel world, PredictiveAction packetCreator) {
        try {
            this.startPrediction(world, packetCreator);
        } catch (ActionResultException1_12_2 ignored) {
        }
    }

    @Unique
    private boolean viaFabricPlus$extinguishFire(BlockPos blockPos, final Direction direction) {
        blockPos = blockPos.relative(direction);
        if (this.minecraft.level.getBlockState(blockPos).getBlock() == Blocks.FIRE) {
            this.minecraft.level.levelEvent(this.minecraft.player, 1009, blockPos, 0);
            this.minecraft.level.removeBlock(blockPos, false);
            return true;
        }
        return false;
    }

}
