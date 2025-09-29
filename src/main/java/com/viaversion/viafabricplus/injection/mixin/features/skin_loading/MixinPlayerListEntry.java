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

package com.viaversion.viafabricplus.injection.mixin.features.skin_loading;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public abstract class MixinPlayerListEntry {
//    @Redirect(method = "texturesSupplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinProvider;supplySkinTextures(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/function/Supplier;"))
//    private static Supplier<SkinTextures> fetchGameProfileProperties(PlayerSkinProvider instance, GameProfile profile, boolean requireSecure) {
//        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20) && !profile.properties().containsKey("textures")) {
//            return CompletableFuture.supplyAsync(() -> {
//                final ProfileResult profileResult = MinecraftClient.getInstance().getApiServices().sessionService().fetchProfile(profile.id(), true);
//                return profileResult == null ? profile : profileResult.profile();
//            }, Util.getMainWorkerExecutor()).thenCompose(instance::fetchSkinTextures);
//        } else {
//            return instance.supplySkinTextures(profile, requireSecure);
//        }
//    }
}
