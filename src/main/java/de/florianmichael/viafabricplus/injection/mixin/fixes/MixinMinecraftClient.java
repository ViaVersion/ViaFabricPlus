/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.v1_12_2.SyncInputExecutor;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import de.florianmichael.viafabricplus.vialoadingbase.ViaLoadingBaseStartup;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow protected int attackCooldown;

    @Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Redirect(method = "doItemUse",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V", ordinal = 0))
    private void redirectDoItemUse(HeldItemRenderer heldItemRenderer, Hand hand) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isNewerThan(ProtocolVersion.v1_8) || !(player.getStackInHand(hand).getItem() instanceof SwordItem)) {
            heldItemRenderer.resetEquipProgress(hand);
        }
    }

    @Redirect(method = "doItemUse",
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;isAccepted()Z", ordinal = 0))
    private boolean preventGenericInteract(ActionResult instance) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_7_6)) {
            return true;
        }

        return instance.isAccepted();
    }

    /**
     * This code removes the cooldown if
     */
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;attackCooldown:I", ordinal = 1))
    public int unwrapOperation(MinecraftClient instance) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return 0;
        }
        return attackCooldown;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V", shift = At.Shift.BEFORE))
    public void updateCooldown(CallbackInfo ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            ordinal = 4, shift = At.Shift.BEFORE))
    public void injectTick(CallbackInfo ci) {
        if (!DebugSettings.getClassWrapper().executeInputsInSync.getValue()) return;

        SyncInputExecutor.callback();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasRidingInventory()Z"))
    private void onInventoryKeyPressed(CallbackInfo ci) throws Exception {
        if (getNetworkHandler() != null && DebugSettings.getClassWrapper().sendOpenInventoryPacket.getValue()) {
            final UserConnection viaConnection = MinecraftClient.getInstance().getNetworkHandler().getConnection().channel.attr(ViaLoadingBaseStartup.LOCAL_VIA_CONNECTION).get();

            if (viaConnection != null && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_11_1)) {
                final PacketWrapper clientStatus = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_STATUS, viaConnection);
                clientStatus.write(Type.VAR_INT, 2); // Open Inventory Achievement

                clientStatus.sendToServer(Protocol1_12To1_11_1.class);
            }
        }
    }
}
