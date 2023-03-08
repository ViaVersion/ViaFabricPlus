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
package de.florianmichael.viafabricplus.provider;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.model.GameProfile;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.providers.GameProfileFetcher;

import java.net.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ViaFabricPlusGameProfileFetcher extends GameProfileFetcher {
    public static final HttpAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
    public static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    public static final GameProfileRepository GAME_PROFILE_REPOSITORY = AUTHENTICATION_SERVICE.createProfileRepository();

    @Override
    public UUID loadMojangUUID(String playerName) throws Exception {
        final CompletableFuture<com.mojang.authlib.GameProfile> future = new CompletableFuture<>();
        GAME_PROFILE_REPOSITORY.findProfilesByNames(new String[]{playerName}, Agent.MINECRAFT, new ProfileLookupCallback() {
            @Override
            public void onProfileLookupSucceeded(com.mojang.authlib.GameProfile profile) {
                future.complete(profile);
            }

            @Override
            public void onProfileLookupFailed(com.mojang.authlib.GameProfile profile, Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        if (!future.isDone()) {
            future.completeExceptionally(new ProfileNotFoundException());
        }
        return future.get().getId();
    }

    @Override
    public GameProfile loadGameProfile(UUID uuid) throws Exception {
        final com.mojang.authlib.GameProfile inProfile = new com.mojang.authlib.GameProfile(uuid, null);
        final com.mojang.authlib.GameProfile mojangProfile = SESSION_SERVICE.fillProfileProperties(inProfile, true);
        if (mojangProfile.equals(inProfile)) throw new ProfileNotFoundException();

        final GameProfile gameProfile = new GameProfile(mojangProfile.getName(), mojangProfile.getId());
        for (final var entry : mojangProfile.getProperties().entries()) {
            final Property prop = entry.getValue();
            gameProfile.addProperty(new GameProfile.Property(prop.getName(), prop.getValue(), prop.getSignature()));
        }
        return gameProfile;
    }
}