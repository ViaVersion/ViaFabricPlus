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

import com.viaversion.viafabricplus.api.ViaFabricPlusBase;
import com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusLoadEntrypoint;
import com.viaversion.viafabricplus.api.events.ChangeProtocolVersionCallback;
import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import com.viaversion.viafabricplus.injection.access.registry.IMappedRegistry;
import com.viaversion.viafabricplus.util.network.SyncTasks;
import com.viaversion.viafabricplus.features.FeaturesLoading;
import com.viaversion.viafabricplus.features.item.filter_creative_tabs.VersionedRegistries;
import com.viaversion.viafabricplus.features.item.negative_item_count.NegativeItemUtil;
import com.viaversion.viafabricplus.features.limitation.max_chat_length.MaxChatLength;
import com.viaversion.viafabricplus.injection.access.core.IConnection;
import com.viaversion.viafabricplus.injection.access.core.IServerData;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.translator.ItemTranslator;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.impl.ProtocolSelectionScreen;
import com.viaversion.viafabricplus.screen.impl.SettingsScreen;
import com.viaversion.viafabricplus.settings.SettingsManager;
import com.viaversion.viafabricplus.util.ChatUtil;
import com.viaversion.viafabricplus.util.ClassLoaderPriorityUtil;
import com.viaversion.viafabricplus.util.network.SyncTasks;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.util.Pair;
import io.netty.channel.Channel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import static com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusLoadEntrypoint.KEY;

public final class ViaFabricPlusImpl implements ViaFabricPlusBase {

    public static final Event<LoadingCycleCallback> LOADING_CYCLE = EventFactory.createArrayBacked(LoadingCycleCallback.class, listeners -> state -> {
        for (final LoadingCycleCallback listener : listeners) {
            listener.onLoadCycle(state);
        }
    });

    public static final Event<ChangeProtocolVersionCallback> CHANGE_PROTOCOL_VERSION = EventFactory.createArrayBacked(ChangeProtocolVersionCallback.class, listeners -> (oldVersion, newVersion) -> {
        for (final ChangeProtocolVersionCallback listener : listeners) {
            listener.onChangeProtocolVersion(oldVersion, newVersion);
        }
    });

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

        for (final EntrypointContainer<ViaFabricPlusLoadEntrypoint> container : FabricLoader.getInstance().getEntrypointContainers(KEY, ViaFabricPlusLoadEntrypoint.class)) {
            container.getEntrypoint().onPlatformLoad(INSTANCE);
        }

        try {
            Files.createDirectories(path);
        } catch (final IOException e) {
            logger.error("Failed to create ViaFabricPlus directory", e);
        }

        ClassLoaderPriorityUtil.loadOverridingJars(path, logger);
        SettingsManager.INSTANCE.init();
        SaveManager.INSTANCE.init();
        SyncTasks.init();
        FeaturesLoading.init();

        this.loadingFuture = ProtocolTranslator.init(path);
        LOADING_CYCLE.register(cycle -> {
            if (cycle == LoadingCycleCallback.LoadingCycle.POST_GAME_LOAD) {
                this.loadingFuture.join();
                FeaturesLoading.postInit();
                SaveManager.INSTANCE.postInit();
            }
        });
        LOADING_CYCLE.invoker().onLoadCycle(LoadingCycleCallback.LoadingCycle.FINAL_LOAD);
    }

    // --------------------------------------------------------------------------------------------
    // Proxy the most important/used internals to a general API point for mods

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getImplVersion() {
        return this.implVersion;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion() {
        return ProtocolTranslator.getTargetVersion();
    }

    @Override
    public void setTargetVersion(ProtocolVersion newVersion) {
        ProtocolTranslator.setTargetVersion(newVersion);
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion(Channel channel) {
        return ProtocolTranslator.getTargetVersion(channel);
    }

    @Override
    public @Nullable ProtocolVersion getTargetVersion(Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getTargetVersion();
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
    public @Nullable UserConnection getUserConnection(Connection connection) {
        return ((IConnection) connection).viaFabricPlus$getUserConnection();
    }

    @Override
    public @Nullable ProtocolVersion getServerVersion(ServerData serverInfo) {
        return ((IServerData) serverInfo).viaFabricPlus$forcedVersion();
    }

    @Override
    public void registerOnChangeProtocolVersionCallback(ChangeProtocolVersionCallback callback) {
        CHANGE_PROTOCOL_VERSION.register(callback);
    }

    @Override
    public void registerLoadingCycleCallback(LoadingCycleCallback callback) {
        LOADING_CYCLE.register(callback);
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
        for (final SettingGroup group : SettingsManager.INSTANCE.getGroups()) {
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
    public boolean itemExists(net.minecraft.world.item.Item item, ProtocolVersion version) {
        return VersionedRegistries.containsItem(item, version);
    }

    @Override
    public boolean enchantmentExists(ResourceKey<Enchantment> enchantment, ProtocolVersion version) {
        return VersionedRegistries.containsEnchantment(enchantment, version);
    }

    @Override
    public boolean effectExists(Holder<MobEffect> effect, ProtocolVersion version) {
        return VersionedRegistries.containsEffect(effect, version);
    }

    @Override
    public boolean bannerPatternExists(ResourceKey<BannerPattern> pattern, ProtocolVersion version) {
        return VersionedRegistries.containsBannerPattern(pattern, version);
    }

    @Override
    public boolean itemExistsInConnection(net.minecraft.world.item.Item item) {
        return VersionedRegistries.keepItem(item);
    }

    @Override
    public boolean itemExistsInConnection(ItemStack stack) {
        return VersionedRegistries.keepItem(stack);
    }

    @Override
    public int getStackCount(ItemStack stack) {
        return NegativeItemUtil.getCount(stack);
    }

    public Logger getLogger() {
        return this.logger;
    }

}
