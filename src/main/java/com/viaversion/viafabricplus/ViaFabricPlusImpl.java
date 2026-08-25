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

package com.viaversion.viafabricplus;

import com.viaversion.viafabricplus.api.ViaFabricPlusAPI;
import com.viaversion.viafabricplus.api.entrypoint.BootstrapEntrypoint;
import com.viaversion.viafabricplus.api.events.ChangeProtocolVersionEvent;
import com.viaversion.viafabricplus.api.events.LoadingCycleEvent;
import com.viaversion.viafabricplus.features.FeaturesLoading;
import com.viaversion.viafabricplus.injection.access.core.IConnection;
import com.viaversion.viafabricplus.injection.access.core.IServerData;
import com.viaversion.viafabricplus.protocoltranslator.ConversionsImpl;
import com.viaversion.viafabricplus.protocoltranslator.LimitationsImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.screen.ScreensImpl;
import com.viaversion.viafabricplus.settings.SettingsImpl;
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil;
import com.viaversion.viafabricplus.util.network.SyncTasks;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public final class ViaFabricPlusImpl implements ViaFabricPlusAPI {

    private static final ViaFabricPlusImpl INSTANCE = new ViaFabricPlusImpl();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus");

    private final List<LoadingCycleEvent> loadingCycleEvents = new ArrayList<>();
    private final List<ChangeProtocolVersionEvent> changeProtocolVersionEvents = new ArrayList<>();

    private final SettingsImpl settings = new SettingsImpl();
    private final ConversionsImpl conversions = new ConversionsImpl();
    private final LimitationsImpl limitations = new LimitationsImpl();
    private ScreensImpl screens;

    private final String version;
    private final String implVersion;
    private CompletableFuture<Void> loadingFuture;

    private ViaFabricPlusImpl() {
        final ModMetadata metadata = FabricLoader.getInstance().getModContainer("viafabricplus").get().getMetadata();
        this.version = metadata.getVersion().getFriendlyString();
        this.implVersion = metadata.getCustomValue("vfp:implVersion").getAsString();
    }

    public void init() {
        ViaFabricPlus.init(INSTANCE);

        for (final EntrypointContainer<BootstrapEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers("viafabricplus", BootstrapEntrypoint.class)) {
            container.getEntrypoint().onInitialize(INSTANCE);
        }

        try {
            Files.createDirectories(this.path);
        } catch (final IOException e) {
            this.logger.error("Failed to create ViaFabricPlus directory", e);
        }

        ClassLoaderPriorityUtil.loadOverridingJars(this.path, logger);
        this.settings.init(this);
        SyncTasks.init();
        FeaturesLoading.init();

        this.loadingFuture = ProtocolTranslator.init(this.path);
        this.loadingCycleEvents.add(cycle -> {
            if (cycle == LoadingCycleEvent.LoadingCycle.POST_GAME_LOAD) {
                this.screens = new ScreensImpl();
                this.loadingFuture.join();
                FeaturesLoading.postInit();
                this.settings.postInit();
            }
        });
        this.runLoadingCycleEvents(LoadingCycleEvent.LoadingCycle.FINAL_LOAD);
    }

    @Override
    public String version() {
        return this.version;
    }

    @Override
    public String implVersion() {
        return this.implVersion;
    }

    @Override
    public Path path() {
        return this.path;
    }

    @Override
    public ProtocolVersion targetVersion() {
        return ProtocolTranslator.getTargetVersion();
    }

    @Override
    public ProtocolVersion targetVersion(final Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getTargetVersion();
    }

    @Override
    public ProtocolVersion targetVersion(final Channel channel) {
        return ProtocolTranslator.getTargetVersion(channel);
    }

    @Override
    public void setTargetVersion(final ProtocolVersion targetVersion) {
        ProtocolTranslator.setTargetVersion(targetVersion);
    }

    @Override
    public @Nullable UserConnection userConnection() {
        return ProtocolTranslator.getPlayStateUserConnection();
    }

    @Override
    public @Nullable UserConnection userConnection(final Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getUserConnection();
    }

    @Override
    public @Nullable ProtocolVersion serverVersion(final ServerData serverData) {
        return ((IServerData) serverData).viaFabricPlus$forcedVersion();
    }

    @Override
    public void addChangeProtocolVersionEvent(final ChangeProtocolVersionEvent event) {
        this.changeProtocolVersionEvents.add(event);
    }

    public void runChangeProtocolVersionEvents(final ProtocolVersion oldVersion, final ProtocolVersion newVersion) {
        for (final ChangeProtocolVersionEvent event : this.changeProtocolVersionEvents) {
            event.onChangeProtocolVersion(oldVersion, newVersion);
        }
    }

    @Override
    public void addLoadingCycleEvent(final LoadingCycleEvent event) {
        this.loadingCycleEvents.add(event);
    }

    public void runLoadingCycleEvents(final LoadingCycleEvent.LoadingCycle cycle) {
        for (final LoadingCycleEvent event : this.loadingCycleEvents) {
            event.onLoadCycle(cycle);
        }
    }

    @Override
    public SettingsImpl settings() {
        return this.settings;
    }

    @Override
    public ConversionsImpl conversions() {
        return this.conversions;
    }

    @Override
    public LimitationsImpl limitations() {
        return this.limitations;
    }

    @Override
    public ScreensImpl screens() {
        return this.screens;
    }

    public Logger logger() {
        return this.logger;
    }

    public static ViaFabricPlusImpl impl() {
        return INSTANCE;
    }

}
