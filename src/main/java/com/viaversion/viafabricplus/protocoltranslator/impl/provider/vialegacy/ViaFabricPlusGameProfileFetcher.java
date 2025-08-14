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

package com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.viaversion.viaversion.api.minecraft.GameProfile;
import java.net.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;

public final class ViaFabricPlusGameProfileFetcher extends GameProfileFetcher {

    private static final HttpAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
    private static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    private static final GameProfileRepository GAME_PROFILE_REPOSITORY = AUTHENTICATION_SERVICE.createProfileRepository();

    @Override
    public UUID loadMojangUuid(final String playerName) throws ExecutionException, InterruptedException {
        final CompletableFuture<com.mojang.authlib.GameProfile> future = new CompletableFuture<>();
        GAME_PROFILE_REPOSITORY.findProfilesByNames(new String[]{playerName}, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(final com.mojang.authlib.GameProfile gameProfile) {
                future.complete(gameProfile);
            }

            @Override
            public void onProfileLookupFailed(final String profileName, final Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        if (!future.isDone()) {
            future.completeExceptionally(new ProfileNotFoundException());
        }
        return future.get().getId();
    }

    @Override
    public GameProfile loadGameProfile(final UUID uuid) {
        final ProfileResult result = SESSION_SERVICE.fetchProfile(uuid, true);
        if (result == null) {
            throw new ProfileNotFoundException();
        }

        final com.mojang.authlib.GameProfile gameProfile = result.profile();
        final GameProfile.Property[] properties = new GameProfile.Property[gameProfile.getProperties().size()];
        int i = 0;
        for (final Map.Entry<String, Property> entry : gameProfile.getProperties().entries()) {
            properties[i++] = new GameProfile.Property(entry.getValue().name(), entry.getValue().value(), entry.getValue().signature());
        }
        return new GameProfile(gameProfile.getName(), gameProfile.getId(), properties);
    }

}
