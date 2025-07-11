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
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.RegistryDiffs;
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil;
import com.viaversion.viafabricplus.base.sync_tasks.SyncTasks;
import com.viaversion.viafabricplus.features.FeaturesLoading;
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.ItemDiff;
import com.viaversion.viafabricplus.features.item.negative_item_count.NegativeItemUtil;
import com.viaversion.viafabricplus.features.limitation.max_chat_length.MaxChatLength;
import com.viaversion.viafabricplus.injection.access.base.IClientConnection;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.screen.impl.SettingsScreen;
import com.viaversion.viafabricplus.settings.SettingsManager;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusLoadEntrypoint.KEY;

public final class ViaFabricPlusImpl implements ViaFabricPlusBase {

    public static final ViaFabricPlusImpl INSTANCE = new ViaFabricPlusImpl();

    private final Logger logger = LogManager.getLogger("ViaFabricPlus");
    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("viafabricplus");

    private String version;
    private String implVersion;

    private CompletableFuture<Void> loadingFuture;

    public void init() {
        // Set API instance
        ViaFabricPlus.init(INSTANCE);

        // Get mod version
        final ModMetadata metadata = FabricLoader.getInstance().getModContainer("viafabricplus").get().getMetadata();
        version = metadata.getVersion().getFriendlyString();
        implVersion = metadata.getCustomValue("vfp:implVersion").getAsString();

        // Call entrypoint for addons
        for (final EntrypointContainer<ViaFabricPlusLoadEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers(KEY, ViaFabricPlusLoadEntrypoint.class)) {
            container.getEntrypoint().onPlatformLoad(INSTANCE);
        }

        // Create ViaFabricPlus directory if it doesn't exist
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("Failed to create ViaFabricPlus directory", e);
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

        // Initialize stuff after minecraft is loaded
        Events.LOADING_CYCLE.register(cycle -> {
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
    public Path getPath() {
        return path;
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion() {
        return ProtocolTranslator.getTargetVersion();
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion(Channel channel) {
        return ProtocolTranslator.getTargetVersion(channel);
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion(ClientConnection connection) {
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
    public @Nullable UserConnection getPlayNetworkUserConnection() {
        return ProtocolTranslator.getPlayNetworkUserConnection();
    }

    @Override
    public @Nullable UserConnection getUserConnection(ClientConnection connection) {
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
    public List<SettingGroup> getSettingGroups() {
        return Collections.unmodifiableList(SettingsManager.INSTANCE.getGroups());
    }

    @Override
    public void addSettingGroup(SettingGroup group) {
        SettingsManager.INSTANCE.addGroup(group);
    }

    @Override
    public @Nullable SettingGroup getSettingGroup(String translationKey) {
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
    public @Nullable Item translateItem(ItemStack stack, ProtocolVersion targetVersion) {
        return ItemTranslator.mcToVia(stack, targetVersion);
    }

    @Override
    public @Nullable ItemStack translateItem(Item item, ProtocolVersion sourceVersion) {
        return ItemTranslator.viaToMc(item, sourceVersion);
    }

    @Override
    public boolean itemExists(net.minecraft.item.Item item, ProtocolVersion version) {
        return ItemDiff.containsItem(item, version);
    }

    @Override
    public boolean enchantmentExists(RegistryKey<Enchantment> enchantment, ProtocolVersion version) {
        return RegistryDiffs.containsEnchantment(enchantment, version);
    }

    @Override
    public boolean effectExists(RegistryEntry<StatusEffect> effect, ProtocolVersion version) {
        return RegistryDiffs.containsEffect(effect, version);
    }

    @Override
    public boolean bannerPatternExists(RegistryKey<BannerPattern> pattern, ProtocolVersion version) {
        return RegistryDiffs.containsBannerPattern(pattern, version);
    }

    @Override
    public boolean itemExistsInConnection(net.minecraft.item.Item item) {
        return ItemDiff.keepItem(item);
    }

    @Override
    public boolean itemExistsInConnection(ItemStack stack) {
        return ItemDiff.keepItem(stack);
    }

    @Override
    public int getStackCount(ItemStack stack) {
        return NegativeItemUtil.getCount(stack);
    }

    public Logger getLogger() {
        return logger;
    }

}
