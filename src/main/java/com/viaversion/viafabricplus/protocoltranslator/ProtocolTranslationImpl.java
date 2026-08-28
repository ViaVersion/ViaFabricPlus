/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.viaversion.viaaprilfools.ViaAprilFoolsPlatformImpl;
import com.viaversion.viabackwards.ViaBackwardsPlatformImpl;
import com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation;
import com.viaversion.viafabricplus.injection.access.core.IConnection;
import com.viaversion.viafabricplus.injection.access.core.IServerData;
import com.viaversion.viafabricplus.protocoltranslator.impl.command.ViaFabricPlusCommandHandler;
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaLegacyPlatform;
import com.viaversion.viafabricplus.protocoltranslator.impl.platform.ViaFabricPlusViaVersionPlatform;
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusPlatformLoader;
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol;
import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.platform.NoopInjector;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public final class ProtocolTranslationImpl implements ProtocolTranslation {

    public static final AttributeKey<Connection> MINECRAFT_CONNECTION_ATTRIBUTE_KEY = AttributeKey.newInstance("viafabricplus-minecraft-connection");
    public static final AttributeKey<ProtocolVersion> TARGET_VERSION_ATTRIBUTE_KEY = AttributeKey.newInstance("viafabricplus-target-version");

    public static final ProtocolVersion NATIVE_VERSION = ProtocolVersion.v26_2;

    private final List<BiConsumer<ProtocolVersion, ProtocolVersion>> changeVersionListeners = new ArrayList<>();

    private ProtocolVersion targetVersion = NATIVE_VERSION;
    private ProtocolVersion previousVersion = null;

    @Override
    public ProtocolVersion targetVersion() {
        return targetVersion;
    }

    @Override
    public ProtocolVersion targetVersion(final Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getTargetVersion();
    }

    @Override
    public ProtocolVersion targetVersion(final Channel channel) {
        return channel.attr(TARGET_VERSION_ATTRIBUTE_KEY).get();
    }

    @Override
    public void setTargetVersion(final ProtocolVersion targetVersion, final boolean revertOnDisconnect) {
        final ProtocolVersion oldVersion = this.targetVersion;
        this.targetVersion = targetVersion;
        if (oldVersion != targetVersion) {
            if (revertOnDisconnect) {
                this.previousVersion = oldVersion;
            }
            for (final BiConsumer<ProtocolVersion, ProtocolVersion> listener : this.changeVersionListeners) {
                listener.accept(oldVersion, this.targetVersion);
            }
        }
    }

    @Override
    public void addChangeProtocolVersionListener(final BiConsumer<ProtocolVersion, ProtocolVersion> listener) {
        this.changeVersionListeners.add(listener);
    }

    @Override
    public @Nullable UserConnection userConnection() {
        final ClientPacketListener handler = Minecraft.getInstance().getConnection();
        if (handler != null) {
            return ((IConnection) handler.getConnection()).viaFabricPlus$getUserConnection();
        } else {
            return null;
        }
    }

    @Override
    public @Nullable UserConnection userConnection(final Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getUserConnection();
    }

    @Override
    public @Nullable ProtocolVersion serverVersion(final ServerData serverData) {
        return ((IServerData) serverData).viaFabricPlus$forcedVersion();
    }

    public void injectionPreviousVersionHandler(final Channel channel) {
        if (this.previousVersion != null) {
            channel.closeFuture().addListener(_ -> {
                setTargetVersion(this.previousVersion);
                this.previousVersion = null;
            });
        }
    }

    public CompletableFuture<Void> init(final Path path) {
        if (SharedConstants.getProtocolVersion() != NATIVE_VERSION.getOriginalVersion()) {
            throw new IllegalStateException("Native version is not the same as the current version");
        }

        // Register command callback for /viafabricplus
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            final ViaFabricPlusCommandHandler commandHandler = (ViaFabricPlusCommandHandler) Via.getManager().getCommandHandler();
            final RequiredArgumentBuilder<FabricClientCommandSource, String> executor = RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("args", StringArgumentType.greedyString()).executes(commandHandler::execute).suggests(commandHandler::suggestion);

            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("viafabricplus").then(executor).executes(commandHandler::execute));
        });

        return CompletableFuture.runAsync(() -> {
            // Load ViaVersion and register all platforms and their components
            ViaManagerImpl.initAndLoad(
                new ViaFabricPlusViaVersionPlatform(path.toFile()),
                new NoopInjector(),
                new ViaFabricPlusCommandHandler(),
                new ViaFabricPlusPlatformLoader(),
                () -> {
                    new ViaBackwardsPlatformImpl();
                    new ViaFabricPlusViaLegacyPlatform();
                    new ViaAprilFoolsPlatformImpl();
                }
            );
            ProtocolVersion.register(ProtocolVersionDetector.AUTO_DETECT_VERSION);
            ViaFabricPlusProtocol.INSTANCE.initialize();
        }, Util.backgroundExecutor());
    }

}
