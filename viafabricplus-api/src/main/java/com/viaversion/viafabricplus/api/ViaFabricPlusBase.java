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

package com.viaversion.viafabricplus.api;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.api.events.ChangeProtocolVersionCallback;
import com.viaversion.viafabricplus.api.events.LoadingCycleCallback;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

/**
 * General API point for mods. Get instance via {@link ViaFabricPlus#getImpl()}.
 */
public interface ViaFabricPlusBase {

    /**
     * @return an <b>internally based API version</b> incremented with meaningful or breaking changes.
     */
    default int apiVersion() {
        return 5;
    }

    /**
     * The version of the mod, this is the version that is displayed in the mod list (e.g. 4.0.0)
     *
     * @return the version of the mod
     */
    String getVersion();

    /**
     * The implementation version of the current running jar file, containing commit-hash as well as the {@link #getVersion()}.
     *
     * @return the implementation version of the mod
     */
    String getImplVersion();

    /**
     * Get the root path of the mod.
     *
     * @return The root path
     */
    Path getPath();

    @Deprecated
    default Path rootPath() {
        return getPath();
    }

    /**
     * This method is used when you need the target version after connecting to the server.
     *
     * @return the target version
     */
    ProtocolVersion getTargetVersion();

    /**
     * Sets the target version
     *
     * @param newVersion the target version
     */
    void setTargetVersion(final ProtocolVersion newVersion);

    /**
     * Gets the target version from the channel attribute, can be used in early stages of the connection
     *
     * @param channel the channel
     * @return the target version
     */
    ProtocolVersion getTargetVersion(final Channel channel);

    /**
     * Gets the target version from the connection, can be used in early stages of the connection
     *
     * @param connection the connection
     * @return the target version
     */
    ProtocolVersion getTargetVersion(final ClientConnection connection);

    /**
     * Sets the target version
     *
     * @param newVersion         the target version
     * @param revertOnDisconnect if true, the previous version will be set when the player disconnects from the server
     */
    void setTargetVersion(final ProtocolVersion newVersion, final boolean revertOnDisconnect);

    /**
     * @return the current UserConnection of the connection to the server, if the player isn't connected to a server it will return null
     */
    @Nullable UserConnection getPlayNetworkUserConnection();

    /**
     * Get the UserConnection for the given connection {@link ClientConnection}.
     *
     * @param connection the connection
     * @return the UserConnection
     */
    @Nullable UserConnection getUserConnection(final ClientConnection connection);

    /**
     * Gets the per-server protocol version for the given server.
     *
     * @param serverInfo the server info
     * @return the server version
     */
    @Nullable ProtocolVersion getServerVersion(final ServerInfo serverInfo);

    /**
     * Register a callback for when the user changes the target version in the screen, or if the user joins a server with a different version.
     *
     * @param callback the callback
     */
    void registerOnChangeProtocolVersionCallback(final ChangeProtocolVersionCallback callback);

    /**
     * Register a callback for the loading cycle which covers most of the loading process of the mod. Intended to be used
     * inside {@link com.viaversion.viafabricplus.api.entrypoint.ViaFabricPlusLoadEntrypoint} implementations.
     *
     * @param callback the callback
     */
    void registerLoadingCycleCallback(final LoadingCycleCallback callback);

    /**
     * Calculates the maximum chat length for given {@link ProtocolVersion} instance.
     *
     * @return The maximum chat length
     */
    int getMaxChatLength(final ProtocolVersion version);

    /**
     * All setting groups of the mod. Note that this list is not modifiable.
     *
     * @return The setting groups
     */
    List<SettingGroup> getSettingGroups();

    @Deprecated
    default List<SettingGroup> settingGroups() {
        return getSettingGroups();
    }

    /**
     * Add a setting group to the mod.
     *
     * @param group The setting group
     */
    void addSettingGroup(final SettingGroup group);

    /**
     * Get a setting group by its translationKey.
     *
     * @param translationKey The translationKey of the setting group
     * @return The setting group or null if it does not exist
     */
    @Nullable SettingGroup getSettingGroup(final String translationKey);

    /**
     * Open the protocol selection screen.
     *
     * @param parent The parent screen
     */
    void openProtocolSelectionScreen(final Screen parent);

    /**
     * Open the settings screen.
     *
     * @param parent The parent screen
     */
    void openSettingsScreen(final Screen parent);

    /**
     * Converts a Minecraft item stack {@link ItemStack} to a ViaVersion item {@link Item}
     *
     * @param stack         The Minecraft item stack to convert {@link ItemStack}
     * @param targetVersion The target version to convert to (e.g. v1.13) {@link ProtocolVersion}
     * @return The ViaVersion item for the target version {@link Item}
     */
    @Nullable Item translateItem(final ItemStack stack, final ProtocolVersion targetVersion);

    /**
     * Converts a ViaVersion item {@link Item} to a Minecraft item stack {@link ItemStack}
     *
     * @param item          The ViaVersion item to convert {@link Item}
     * @param sourceVersion The source version of the item (e.g. b1.8) {@link ProtocolVersion}
     * @return The Minecraft item stack for the source version {@link ItemStack}
     */
    @Nullable ItemStack translateItem(final Item item, final ProtocolVersion sourceVersion);

    /**
     * @param item    The item to check
     * @param version The version to check for
     * @return true if the item exists in the given version, false otherwise, this will also check for CPE items (CustomBlocks V1 extension)
     */
    boolean itemExists(final net.minecraft.item.Item item, final ProtocolVersion version);

    /**
     * @param enchantment The enchantment to check
     * @param version     The version to check for
     * @return true if the enchantment exists in the given version, false otherwise
     */
    boolean enchantmentExists(final RegistryKey<Enchantment> enchantment, final ProtocolVersion version);

    /**
     * @param effect  The status effect to check
     * @param version The version to check for
     * @return true if the status effect exists in the given version, false otherwise
     */
    boolean effectExists(final RegistryEntry<StatusEffect> effect, final ProtocolVersion version);

    /**
     * @param pattern The banner pattern to check
     * @param version The version to check for
     * @return true if the banner pattern exists in the given version, false otherwise
     */
    boolean bannerPatternExists(final RegistryKey<BannerPattern> pattern, final ProtocolVersion version);

    /**
     * Similar to {@link #itemExists(net.minecraft.item.Item, ProtocolVersion)}, but takes in the current connection details (e.g. classic protocol extensions being loaded)
     *
     * @param item The item to check
     * @return true if the item exists in the current connection, false otherwise
     */
    boolean itemExistsInConnection(final net.minecraft.item.Item item);

    /**
     * Same as {@link #itemExists(net.minecraft.item.Item, ProtocolVersion)}, but for item stacks. This also compares against certain data components like enchantments or banner patterns.
     *
     * @param stack The item stack to check
     * @return true if the item stack exists in the given version, false otherwise
     */
    boolean itemExistsInConnection(final ItemStack stack);

    /**
     * Similar to {@link ItemStack#getCount()}, but also handles negative item counts in pre 1.11 versions
     *
     * @param stack The item stack to get the count of
     * @return the count of the item stack, can be negative in pre 1.11 versions
     */
    int getStackCount(final ItemStack stack);


}
