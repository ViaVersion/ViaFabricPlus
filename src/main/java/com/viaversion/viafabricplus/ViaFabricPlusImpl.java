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

package com.viaversion.viafabricplus;

import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusLoadEntrypoint;
import com.viaversion.viafabricplus.api.events.ChangeProtocolVersionCallback;
import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viafabricplus.base.overriding_jars.ClassLoaderPriorityUtil;
import com.viaversion.viafabricplus.base.sync_tasks.SyncTasks;
import com.viaversion.viafabricplus.features.FeaturesLoading;
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.ItemRegistryDiff;
import com.viaversion.viafabricplus.features.item.negative_item_count.NegativeItemUtil;
import com.viaversion.viafabricplus.features.limitation.max_chat_length.MaxChatLength;
import com.viaversion.viafabricplus.injection.access.base.IClientConnection;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.screen.impl.settings.SettingsScreen;
import com.viaversion.viafabricplus.settings.SettingsManager;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * TODO | Port 1.21.3
 *  - VehicleMovePacket handling now has distance check in ClientPlayNetworkHandler
 *  - Illusioner/Sniffer don't override visibility bounding box anymore
 *  - AbstractFireabll/EyeOfEnder#shouldRender new
 *  - Entity#baseTick doesn't set prev rotation anymore at top
 *  - LivingEntity movement code got refactored completely
 *  - BundleItem#use, ConsumableComponent behaviour in use functions
 *  - Check all screen handlers for changes
 *
 * TODO | Port 1.20.6
 *  - ClientPlayerInteractionManager#interactBlockInternal added new condition
 *  - Command arguments (Probably not everything worth, but least them with nbt)
 *  - Entity attachment calculation got changed completely
 *  - Particle handling has slightly changed
 *  - BookViewScreen/BookEditScreen networking handling
 *  - SetEquipment packet now only accepts living entities
 *  - Wolf interaction
 *
 * TODO | General
 *  - Make recipe fixes dynamic instead of a data dump in java classes
 *  - Window interactions in <= 1.16.5 has changed and can be detected by the server
 *  - Most CTS protocol features aren't supported (see https://github.com/ViaVersion/ViaFabricPlus/issues/181)
 *  - Most CPE features aren't implemented correctly (see https://github.com/ViaVersion/ViaFabricPlus/issues/152)
 *  - Via: 1.13 -> 1.12.2 block entities recode
 *  - OXYGEN_BONUS 1.21 -> 1.20.5 handling is missing (only visual)
 *
 * TODO | Movement
 *  - 1.8 lava movement
 *  - 1.13.2 water movement
 */
public final class ViaFabricPlusImpl implements ViaFabricPlusBase {

    public static final ViaFabricPlusImpl INSTANCE = new ViaFabricPlusImpl();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus");

    private String version;
    private String implVersion;

    private CompletableFuture<Void> loadingFuture;

    public void init() {
        ViaFabricPlus.init(INSTANCE);

        final ModMetadata metadata = FabricLoader.getInstance().getModContainer("viafabricplus").get().getMetadata();
        version = metadata.getVersion().getFriendlyString();
        implVersion = metadata.getCustomValue("vfp:implVersion").getAsString();
        FabricLoader.getInstance().getEntrypointContainers("viafabricplus", ViaFabricPlusLoadEntrypoint.class).forEach(container -> {
            container.getEntrypoint().onPlatformLoad(INSTANCE);
        });

        // Create the directory if it doesn't exist
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                logger.error("Failed to create ViaFabricPlus directory", e);
            }
        }

        // Load overriding jars first so other code can access the new classes
        ClassLoaderPriorityUtil.loadOverridingJars(path, logger);

        // Load settings and config files
        SettingsManager.INSTANCE.init();
        SaveManager.INSTANCE.init();

        // Init features
        SyncTasks.init();
        FeaturesLoading.init();

        // Init ViaVersion protocol translator platform
        loadingFuture = ProtocolTranslator.init(path);

        registerLoadingCycleCallback(cycle -> {
            if (cycle != LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                return;
            }
            loadingFuture.join();
            SaveManager.INSTANCE.postInit();
        });
        Events.LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.FINAL_LOAD);
    }

    // --------------------------------------------------------------------------------------------
    // Proxy the most important/used internals to a general API point for mods

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getImplVersion() {
        return implVersion;
    }

    @Override
    public Path rootPath() {
        return path;
    }

    @Override
    public ProtocolVersion getTargetVersion() {
        return ProtocolTranslator.getTargetVersion();
    }

    @Override
    public ProtocolVersion getTargetVersion(Channel channel) {
        return ProtocolTranslator.getTargetVersion(channel);
    }

    @Override
    public ProtocolVersion getTargetVersion(ClientConnection connection) {
        return ((IClientConnection) connection).viaFabricPlus$getTargetVersion();
    }

    @Override
    public void setTargetVersion(ProtocolVersion newVersion) {
        ProtocolTranslator.setTargetVersion(newVersion);
    }

    @Override
    public void setTargetVersion(ProtocolVersion newVersion, boolean revertOnDisconnect) {
        ProtocolTranslator.setTargetVersion(newVersion, revertOnDisconnect);
    }

    @Override
    public UserConnection getPlayNetworkUserConnection() {
        return ProtocolTranslator.getPlayNetworkUserConnection();
    }

    @Override
    public UserConnection getUserConnection(ClientConnection connection) {
        return ((IClientConnection) connection).viaFabricPlus$getUserConnection();
    }

    @Override
    public @Nullable ProtocolVersion getServerVersion(ServerInfo serverInfo) {
        return ((IServerInfo) serverInfo).viaFabricPlus$forcedVersion();
    }

    @Override
    public void registerOnChangeProtocolVersionCallback(ChangeProtocolVersionCallback callback) {
        Events.CHANGE_PROTOCOL_VERSION.register(callback);
    }

    @Override
    public void registerLoadingCycleCallback(LoadingCycleCallback callback) {
        Events.LOADING_CYCLE.register(callback);
    }

    @Override
    public int getMaxChatLength(ProtocolVersion version) {
        return MaxChatLength.getChatLength();
    }

    @Override
    public List<SettingGroup> settingGroups() {
        return SettingsManager.INSTANCE.getGroups();
    }

    @Override
    public void addSettingGroup(SettingGroup group) {
        SettingsManager.INSTANCE.addGroup(group);
    }

    @Override
    public SettingGroup getSettingGroup(String translationKey) {
        for (SettingGroup group : SettingsManager.INSTANCE.getGroups()) {
            if (ChatUtil.uncoverTranslationKey(group.getName()).equals(translationKey)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void openProtocolSelectionScreen(Screen parent) {
        ProtocolSelectionScreen.INSTANCE.open(parent);
    }

    @Override
    public void openSettingsScreen(Screen parent) {
        SettingsScreen.INSTANCE.open(parent);
    }

    @Override
    public Item translateItem(ItemStack stack, ProtocolVersion targetVersion) {
        return ItemTranslator.mcToVia(stack, targetVersion);
    }

    @Override
    public ItemStack translateItem(Item item, ProtocolVersion sourceVersion) {
        return ItemTranslator.viaToMc(item, sourceVersion);
    }

    @Override
    public boolean itemExists(net.minecraft.item.Item item, ProtocolVersion version) {
        return ItemRegistryDiff.contains(item, version);
    }

    @Override
    public boolean itemExistsInConnection(net.minecraft.item.Item item) {
        return ItemRegistryDiff.keepItem(item);
    }

    @Override
    public int getStackCount(ItemStack stack) {
        return NegativeItemUtil.getCount(stack);
    }

    public Logger logger() {
        return logger;
    }

}
