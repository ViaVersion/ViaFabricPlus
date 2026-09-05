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

package com.viaversion.viafabricplus.screen.impl.serverlist;

import com.mojang.blaze3d.platform.NativeImage;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen;
import com.viaversion.viafabricplus.screen.base.list.VFPTextEntry;
import com.viaversion.viafabricplus.util.network.ConnectionUtil;
import de.florianreuth.classic4j.BetaCraftHandler;
import de.florianreuth.classic4j.model.betacraft.BCServerInfo;
import de.florianreuth.classic4j.model.betacraft.BCServerList;
import de.florianreuth.classic4j.model.betacraft.BCVersionCategory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public final class BetaCraftServerListScreen extends VFPTabbedScreen<BCVersionCategory> {

    public static final Component TITLE = Component.nullToEmpty("BetaCraft");
    private static final int ROW_WIDTH = 360;

    private static @Nullable BCServerList serverList;
    private static boolean loading;

    public BetaCraftServerListScreen() {
        super(TITLE, true);
    }

    @Override
    protected void init() {
        // Started before the tabs are built, so they can show the state of the request
        if (serverList == null) {
            this.load();
        }

        super.init();

        this.addFooter(Button.builder(Component.translatable("base.viafabricplus.refresh"), _ -> {
            serverList = null;
            this.load();
        }).build());
    }

    @Override
    protected List<BCVersionCategory> tabs() {
        if (serverList == null) {
            return List.of(BCVersionCategory.values());
        }

        // Categories without servers would only show an empty list
        final List<BCVersionCategory> categories = Arrays.stream(BCVersionCategory.values())
            .filter(category -> !serverList.serversOfVersionCategory(category).isEmpty())
            .toList();
        return categories.isEmpty() ? List.of(BCVersionCategory.values()) : categories;
    }

    @Override
    protected Component tabTitle(final BCVersionCategory tab) {
        final String name = tab.name();
        return Component.nullToEmpty(name.charAt(0) + name.substring(1).toLowerCase(Locale.ROOT));
    }

    @Override
    protected int entryHeight() {
        return (this.font.lineHeight + 2) * 3; // title is 2
    }

    @Override
    protected int rowWidth(final int screenWidth) {
        return Math.min(ROW_WIDTH, screenWidth - 20);
    }

    @Override
    protected List<VFPListEntry> entries(final BCVersionCategory tab) {
        if (serverList == null) { // The servers are either still loading or the request failed
            return List.of(new VFPTextEntry(loading
                ? Component.translatable("betacraft.viafabricplus.loading")
                : Component.translatable("base.viafabricplus.something_went_wrong")));
        }

        return serverList.serversOfVersionCategory(tab).stream().<VFPListEntry>map(ServerSlot::new).toList();
    }

    @Override
    protected List<VFPListEntry> results(final String query) {
        if (serverList == null) {
            return List.of();
        }

        return serverList.servers().stream()
            .filter(server -> server.name().toLowerCase(Locale.ROOT).contains(query) || server.gameVersion().toLowerCase(Locale.ROOT).contains(query))
            .<VFPListEntry>map(ServerSlot::new)
            .toList();
    }

    // Guarded against the screen being rebuilt while the request is still running, which would send it again
    private void load() {
        if (loading) {
            return;
        }

        loading = true;
        BetaCraftHandler.requestServerList(response -> {
            serverList = response;
            loading = false;
            Minecraft.getInstance().execute(this::rebuildWidgets);
        }, throwable -> {
            loading = false;
            ViaFabricPlusImpl.impl().logger().error("Error while loading BetaCraft servers!", throwable);
            showToast(Component.translatable("base.viafabricplus.something_went_wrong"));
            Minecraft.getInstance().execute(this::rebuildWidgets); // Replaces the loading text of the tabs
        });
    }

    public static class ServerSlot extends VFPListEntry {

        private static final int ICON_SIZE = 24;
        private static final int TEXT_OFFSET = SLOT_MARGIN + ICON_SIZE + SLOT_MARGIN;
        private static final int DETAILS_COLOR = 0xFFAAAAAA;

        // The icons are sent as raw PNG data, so they have to be uploaded as textures at once
        private static final Map<String, Identifier> ICONS = new HashMap<>();

        private final BCServerInfo server;
        private final Identifier icon;
        private final Component name;

        public ServerSlot(final BCServerInfo server) {
            this.server = server;
            this.icon = icon(server);
            this.name = Component.literal(server.name()).append(Component.literal(" [" + server.gameVersion() + "]").withStyle(ChatFormatting.DARK_GRAY));
        }

        private static Identifier icon(final BCServerInfo server) {
            return ICONS.computeIfAbsent(server.socket(), _ -> uploadIcon(server));
        }

        private static Identifier uploadIcon(final BCServerInfo server) {
            final Optional<byte[]> icon = server.icon();
            if (icon.isEmpty()) {
                return FaviconTexture.MISSING_LOCATION;
            }

            // The icons are not limited to the 64x64 of vanilla server icons, so FaviconTexture rejects them
            final Identifier texture = Identifier.fromNamespaceAndPath("viafabricplus", "betacraft/" + Util.sanitizeName(server.socket(), Identifier::validPathChar) + "/icon");
            NativeImage image = null;
            try {
                image = NativeImage.read(icon.get());
                Minecraft.getInstance().getTextureManager().register(texture, new DynamicTexture(() -> "BetaCraft server icon " + server.socket(), image));
                return texture;
            } catch (final Throwable throwable) {
                if (image != null) {
                    image.close();
                }

                ViaFabricPlusImpl.impl().logger().warn("Failed to read the icon of the BetaCraft server {}", server.name(), throwable);
                return FaviconTexture.MISSING_LOCATION;
            }
        }

        @Override
        public @NonNull Component getNarration() {
            return this.name;
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            ConnectionUtil.connect(server.name(), server.socket());
            super.mappedMouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void mappedRender(GuiGraphicsExtractor context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.blit(RenderPipelines.GUI_TEXTURED, this.icon, SLOT_MARGIN, (entryHeight - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            final Font font = Minecraft.getInstance().font;
            final int nameY = entryHeight / 2 - font.lineHeight - 1;
            final int detailsY = entryHeight / 2 + 1;

            final String players = this.server.playerCount() + "/" + this.server.playerLimit();
            int detailsRight = entryWidth - font.width(players) - SLOT_MARGIN;
            context.text(font, players, detailsRight, nameY, -1);

            if (this.server.onlineMode()) {
                final Component onlineMode = Component.translatable("base.viafabricplus.online_mode").withStyle(ChatFormatting.GREEN);
                final int onlineModeX = entryWidth - font.width(onlineMode) - SLOT_MARGIN;
                context.text(font, onlineMode, onlineModeX, detailsY, -1);
                detailsRight = Math.min(detailsRight, onlineModeX);
            }

            // Long names and addresses are cut off instead of overlapping the player count
            context.enableScissor(TEXT_OFFSET, 0, detailsRight - SLOT_MARGIN, entryHeight);
            context.text(font, this.name, TEXT_OFFSET, nameY, -1);
            context.text(font, this.server.socket(), TEXT_OFFSET, detailsY, DETAILS_COLOR);
            context.disableScissor();
        }
    }

}
