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

package com.viaversion.viafabricplus.api;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.Channel;
import java.nio.file.Path;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jetbrains.annotations.Nullable;

@Deprecated(forRemoval = true)
public interface ViaFabricPlusBase {

    @Deprecated(forRemoval = true)
    default int apiVersion() {
        return ViaFabricPlus.api().apiVersion();
    }

    @Deprecated(forRemoval = true)
    default String getVersion() {
        return ViaFabricPlus.api().version();
    }

    @Deprecated(forRemoval = true)
    default String getImplVersion() {
        return ViaFabricPlus.api().implVersion();
    }

    @Deprecated(forRemoval = true)
    default Path getPath() {
        return ViaFabricPlus.api().path();
    }

    @Deprecated(forRemoval = true)
    default ProtocolVersion getTargetVersion() {
        return ViaFabricPlus.api().targetVersion();
    }

    @Deprecated(forRemoval = true)
    default void setTargetVersion(final ProtocolVersion newVersion) {
        ViaFabricPlus.api().setTargetVersion(newVersion);
    }

    @Deprecated(forRemoval = true)
    default ProtocolVersion getTargetVersion(final Channel channel) {
        return ViaFabricPlus.api().protocolTranslation().targetVersion(channel);
    }

    @Deprecated(forRemoval = true)
    default ProtocolVersion getTargetVersion(final Connection connection) {
        return ViaFabricPlus.api().protocolTranslation().targetVersion(connection);
    }

    @Deprecated(forRemoval = true)
    default void setTargetVersion(final ProtocolVersion newVersion, final boolean revertOnDisconnect) {
        ViaFabricPlus.api().protocolTranslation().setTargetVersion(newVersion, revertOnDisconnect);
    }

    @Deprecated(forRemoval = true)
    @Nullable
    default UserConnection getPlayNetworkUserConnection() {
        return ViaFabricPlus.api().userConnection();
    }

    @Deprecated(forRemoval = true)
    @Nullable
    default UserConnection getUserConnection(final Connection connection) {
        return ViaFabricPlus.api().protocolTranslation().userConnection(connection);
    }

    @Deprecated(forRemoval = true)
    @Nullable
    default ProtocolVersion getServerVersion(final ServerData serverInfo) {
        return ViaFabricPlus.api().protocolTranslation().serverVersion(serverInfo);
    }

    @Deprecated(forRemoval = true)
    default int getMaxChatLength(final ProtocolVersion version) {
        return ViaFabricPlus.api().limitations().maxChatLength(version);
    }

    @Deprecated(forRemoval = true)
    default void openProtocolSelectionScreen(final Screen parent) {
        ViaFabricPlus.api().screens().openProtocolSelectionScreen(parent);
    }

    @Deprecated(forRemoval = true)
    default void openSettingsScreen(final Screen parent) {
        ViaFabricPlus.api().screens().openSettingsScreen(parent);
    }

    @Deprecated(forRemoval = true)
    @Nullable
    default Item translateItem(final ItemStack stack, final ProtocolVersion targetVersion) {
        return ViaFabricPlus.api().conversions().translateItem(stack, targetVersion);
    }

    @Deprecated(forRemoval = true)
    @Nullable
    default ItemStack translateItem(final Item item, final ProtocolVersion sourceVersion) {
        return ViaFabricPlus.api().conversions().translateItem(item, sourceVersion);
    }

    @Deprecated(forRemoval = true)
    default boolean itemExists(final net.minecraft.world.item.Item item, final ProtocolVersion version) {
        return ViaFabricPlus.api().limitations().itemExists(item, version);
    }

    @Deprecated(forRemoval = true)
    default boolean enchantmentExists(final ResourceKey<Enchantment> enchantment, final ProtocolVersion version) {
        return ViaFabricPlus.api().limitations().enchantmentExists(enchantment, version);
    }

    @Deprecated(forRemoval = true)
    default boolean effectExists(final Holder<MobEffect> effect, final ProtocolVersion version) {
        return ViaFabricPlus.api().limitations().effectExists(effect, version);
    }

    @Deprecated(forRemoval = true)
    default boolean bannerPatternExists(final ResourceKey<BannerPattern> pattern, final ProtocolVersion version) {
        return ViaFabricPlus.api().limitations().bannerPatternExists(pattern, version);
    }

    @Deprecated(forRemoval = true)
    default boolean itemExistsInConnection(final net.minecraft.world.item.Item item) {
        return ViaFabricPlus.api().limitations().itemExistsInConnection(item);
    }

    @Deprecated(forRemoval = true)
    default boolean itemExistsInConnection(final ItemStack stack) {
        return ViaFabricPlus.api().limitations().itemExistsInConnection(stack);
    }

    @Deprecated(forRemoval = true)
    default int getStackCount(final ItemStack stack) {
        return ViaFabricPlus.api().limitations().getStackCount(stack);
    }

}
