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
package de.florianmichael.viafabricplus.screen.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process.ILoginProcessHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.data.ClassiCubeServerInfo;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.request.server.ClassiCubeServerListRequest;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.settings.groups.MPPassSettings;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClassiCubeServerListScreen extends Screen {
    public final static List<ClassiCubeServerInfo> SERVERS = new ArrayList<>();
    private final static ClassiCubeServerListScreen INSTANCE = new ClassiCubeServerListScreen();
    public Screen prevScreen;

    public static ClassiCubeServerListScreen get(final Screen prevScreen) {
        ClassiCubeServerListScreen.INSTANCE.prevScreen = prevScreen;
        return ClassiCubeServerListScreen.INSTANCE;
    }

    public static void open(final Screen prevScreen, final ILoginProcessHandler loginProcessHandler) {
        if (ClassiCubeServerListScreen.SERVERS.isEmpty()) {
            final ClassiCubeServerListRequest request = new ClassiCubeServerListRequest(ClassiCubeAccountHandler.INSTANCE.getAccount());
            request.send().whenComplete((server, throwable) -> {
                if (throwable != null) {
                    loginProcessHandler.handleException(throwable);
                    return;
                }
                ClassiCubeServerListScreen.SERVERS.addAll(server.getServers());
                RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(ClassiCubeServerListScreen.get(prevScreen)));
            });
            return;
        }
        MinecraftClient.getInstance().setScreen(ClassiCubeServerListScreen.get(prevScreen));
    }

    public ClassiCubeServerListScreen() {
        super(Text.literal("ClassiCube ServerList"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, (textRenderer.fontHeight + 4) * 3));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(5, 5).size(20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Logout"), button -> {
            client.setScreen(prevScreen);
            ClassiCubeAccountHandler.INSTANCE.setAccount(null);
            SERVERS.clear();
        }).position(width - 98 - 5, 5).size(98, 20).build());
    }

    @Override
    public void close() {
        ProtocolSelectionScreen.open(new MultiplayerScreen(new TitleScreen()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredTextWithShadow(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();
        drawCenteredTextWithShadow(matrices, textRenderer, Text.literal("Logged in as " + ClassiCubeAccountHandler.INSTANCE.getAccount().username), width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<ServerSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            SERVERS.forEach(serverInfo -> this.addEntry(new ServerSlot(serverInfo)));
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 5;
        }
    }


    public static class ServerSlot extends AlwaysSelectedEntryListWidget.Entry<ServerSlot> {
        private final ClassiCubeServerInfo classiCubeServerInfo;

        public ServerSlot(ClassiCubeServerInfo classiCubeServerInfo) {
            this.classiCubeServerInfo = classiCubeServerInfo;
        }

        @Override
        public Text getNarration() {
            return Text.literal(classiCubeServerInfo.name());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            final ServerAddress serverAddress = ServerAddress.parse(classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port());
            final ServerInfo entry = new ServerInfo(classiCubeServerInfo.name(), serverAddress.getAddress(), false);
            ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = classiCubeServerInfo.mppass();

            if (MPPassSettings.INSTANCE.forceCPEIfUsingClassiCube.getValue()) {
                ((IServerInfo) entry).viafabricplus_forceVersion(InternalProtocolList.fromProtocolVersion(LegacyProtocolVersion.c0_30cpe));
            }

            ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), serverAddress, entry);
            ScreenUtil.playClickSound();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            matrices.push();
            matrices.translate(x, y, 0);

            DrawableHelper.fill(matrices, 0, 0, entryWidth - 4 /* int i = this.left + (this.width - entryWidth) / 2; int j = this.left + (this.width + entryWidth) / 2; */, entryHeight, Integer.MIN_VALUE);

            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredTextWithShadow(matrices, textRenderer, classiCubeServerInfo.name(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            drawTextWithShadow(matrices, textRenderer, classiCubeServerInfo.software().replace('&', Formatting.FORMATTING_CODE_PREFIX), 1, 1, -1);
            final String playerText = classiCubeServerInfo.players() + "/" + classiCubeServerInfo.maxplayers();
            drawTextWithShadow(matrices, textRenderer, playerText, entryWidth - textRenderer.getWidth(playerText) - 4 /* magic value from line 132 */ - 1, 1, -1);

            matrices.pop();
        }
    }
}
