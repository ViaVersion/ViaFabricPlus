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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.network;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import de.florianmichael.viafabricplus.definition.RecipesPre1_12;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Identifier;
import net.raphimc.vialoader.util.VersionEnum;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Mutable
    @Shadow
    @Final
    private Set<PlayerListEntry> listedPlayerListEntries;

    @Shadow
    public abstract void onSimulationDistance(SimulationDistanceS2CPacket packet);

    @Shadow
    private ClientWorld world;

    @Shadow public abstract ClientConnection getConnection();

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fixPlayerListOrdering(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
            this.listedPlayerListEntries = new LinkedHashSet<>();
        }
    }

    @Inject(method = "onChunkLoadDistance", at = @At("RETURN"))
    public void emulateSimulationDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_17_1)) {
            this.onSimulationDistance(new SimulationDistanceS2CPacket(packet.getDistance()));
        }
    }

    @Inject(method = {"onGameJoin", "onPlayerRespawn"}, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }

    @WrapWithCondition(method = "onPlayerSpawnPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;setReady()V"))
    public boolean moveDownloadingTerrainClosing(DownloadingTerrainScreen instance) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19);
    }

    @Inject(method = "onPlayerPositionLook", at = @At("RETURN"))
    public void closeDownloadingTerrain(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2) && MinecraftClient.getInstance().currentScreen instanceof DownloadingTerrainScreen) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @ModifyConstant(method = "onEntityPassengersSet", constant = @Constant(classValue = BoatEntity.class))
    public Class<?> dontChangePlayerYaw(Object entity, Class<?> constant) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18_2)) {
            return Integer.class;
        }
        return constant;
    }

    @WrapWithCondition(method = "onPlayerList", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public boolean removeWarning(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_3);
    }

    @WrapWithCondition(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public boolean removeError(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_20_2);
    }

    @Redirect(method = "onServerMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;isSecureChatEnforced()Z"))
    public boolean removeSecureChatWarning(ServerMetadataS2CPacket instance) {
        return instance.isSecureChatEnforced() || VisualSettings.INSTANCE.disableSecureChatWarning.isEnabled();
    }

    @Redirect(method = "onSynchronizeRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket;getRecipes()Ljava/util/List;"))
    public List<RecipeEntry<?>> rewriteRecipes(SynchronizeRecipesS2CPacket instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_11_1to1_11_2)) {
            final List<Recipe<?>> recipes = instance.getRecipes().stream().map(RecipeEntry::value).collect(Collectors.toList());
            RecipesPre1_12.editRecipes(recipes, ProtocolHack.getTargetVersion());

            final List<RecipeEntry<?>> entries = new ArrayList<>();
            int recipeId = 0;
            for (final Recipe<?> recipe : recipes) {
                entries.add(new RecipeEntry<>(new Identifier(String.valueOf(recipeId++)), recipe));
            }

            return entries;
        }
        return instance.getRecipes();
    }

    @WrapWithCondition(method = "setPublicSession", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    public boolean removeInvalidSignatureWarning(Logger instance, String s, Object o) {
        return ProtocolHack.getTargetVersion().isNewerThanOrEqualTo(VersionEnum.r1_19_4);
    }
}
