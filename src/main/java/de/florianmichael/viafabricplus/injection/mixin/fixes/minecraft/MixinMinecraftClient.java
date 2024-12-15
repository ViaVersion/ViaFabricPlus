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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_11_1to1_12.Protocol1_11_1To1_12;
import com.viaversion.viaversion.protocols.v1_9_1to1_9_3.packet.ServerboundPackets1_9_3;
import de.florianmichael.viafabricplus.fixes.versioned.ItemPick1_21_3;
import de.florianmichael.viafabricplus.injection.access.IMouseKeyboard;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.settings.impl.DebugSettings;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Queue;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    protected int attackCooldown;

    @Shadow
    @Final
    public Mouse mouse;

    @Shadow
    @Final
    public Keyboard keyboard;

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void pickItemClientside(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            ItemPick1_21_3.doItemPick((MinecraftClient) (Object) this);
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private boolean disableSwing(ClientPlayerEntity instance, Hand hand) {
        return ProtocolTranslator.getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_15);
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult$Success;swingSource()Lnet/minecraft/util/ActionResult$SwingSource;", ordinal = 0))
    private ActionResult.SwingSource disableSwing(ActionResult.Success instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            return ActionResult.SwingSource.NONE;
        } else {
            return instance.swingSource();
        }
    }

    @Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult$Success;swingSource()Lnet/minecraft/util/ActionResult$SwingSource;", ordinal = 2))
    private ActionResult.SwingSource disableSwing2(ActionResult.Success instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            return ActionResult.SwingSource.NONE;
        } else {
            return instance.swingSource();
        }
    }

    @Inject(method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0, shift = At.Shift.BEFORE),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;attackCooldown:I", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;addCrashReportSection(Lnet/minecraft/util/crash/CrashReport;)V")
            )
    )
    private void processInputQueues(CallbackInfo ci) {
        if (DebugSettings.global().executeInputsSynchronously.isEnabled()) {
            Queue<Runnable> inputEvents = ((IMouseKeyboard) this.mouse).viaFabricPlus$getPendingScreenEvents();
            while (!inputEvents.isEmpty()) inputEvents.poll().run();

            inputEvents = ((IMouseKeyboard) this.keyboard).viaFabricPlus$getPendingScreenEvents();
            while (!inputEvents.isEmpty()) inputEvents.poll().run();
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V", shift = At.Shift.AFTER))
    private void sendOpenInventoryPacket(CallbackInfo ci) {
        if (DebugSettings.global().sendOpenInventoryPacket.isEnabled()) {
            final PacketWrapper clientCommand = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_COMMAND, ProtocolTranslator.getPlayNetworkUserConnection());
            clientCommand.write(Types.VAR_INT, 2); // Open Inventory Achievement
            clientCommand.scheduleSendToServer(Protocol1_11_1To1_12.class);
        }
    }

    @Inject(method = "doAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", shift = At.Shift.BEFORE, ordinal = 0))
    private void fixSwingPacketOrder(CallbackInfoReturnable<Boolean> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            this.player.swingHand(Hand.MAIN_HAND);
        }
    }

    @WrapWithCondition(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    private boolean fixSwingPacketOrder(ClientPlayerEntity instance, Hand hand) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;attackCooldown:I", ordinal = 1))
    private int moveCooldownIncrement(MinecraftClient instance) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0;
        } else {
            return attackCooldown;
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V", shift = At.Shift.BEFORE))
    private void moveCooldownIncrement(CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
    }

    @ModifyExpressionValue(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean allowBlockBreakAndItemUsageAtTheSameTime(boolean original) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_7_6) && original;
    }

    @ModifyExpressionValue(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"))
    private boolean allowItemUsageAndBlockBreakAtTheSameTime(boolean original) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_7_6) && original;
    }

}
