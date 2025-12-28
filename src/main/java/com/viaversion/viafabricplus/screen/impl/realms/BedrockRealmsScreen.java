/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.screen.impl.realms;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.util.ConnectionUtil;
import java.awt.*;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.bedrock.BedrockAuthManager;
import net.raphimc.minecraftauth.extra.realms.model.RealmsJoinInformation;
import net.raphimc.minecraftauth.extra.realms.model.RealmsServer;
import net.raphimc.minecraftauth.extra.realms.service.impl.BedrockRealmsService;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;
import org.apache.logging.log4j.Level;

public final class BedrockRealmsScreen extends VFPScreen {

    public static final BedrockRealmsScreen INSTANCE = new BedrockRealmsScreen();

    private BedrockRealmsService service;
    private List<RealmsServer> realmsServers;

    private SlotList slotList;
    private Button joinButton;
    private Button leaveButton;

    public BedrockRealmsScreen() {
        super(Component.translatable("screen.viafabricplus.bedrock_realms"), true);
    }

    @Override
    protected void init() {
        super.init();

        if (realmsServers != null) {
            createView();
            return;
        }

        setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.availability_check"));
        Util.nonCriticalIoPool().execute(this::loadRealms);
    }

    private void loadRealms() {
        final BedrockAuthManager account = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount();
        if (account == null) { // Just in case...
            setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.warning"));
            return;
        }

        service = new BedrockRealmsService(MinecraftAuth.createHttpClient(), ProtocolConstants.BEDROCK_VERSION_NAME, account.getRealmsXstsToken());
        service.isCompatibleAsync().thenAccept(state -> {
            if (state) {
                service.getWorldsAsync().thenAccept(realmsServers -> {
                    this.realmsServers = realmsServers;
                    createView();
                }).exceptionally(throwable -> error("Failed to load realm worlds", throwable));
            } else {
                setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.unavailable"));
            }
        }).exceptionally(throwable -> error("Failed to check realms availability", throwable));
    }

    private Void error(final String message, final Throwable throwable) {
        setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.error"));
        ViaFabricPlusImpl.INSTANCE.getLogger().log(Level.ERROR, message, throwable);
        return null;
    }

    private void createView() {
        if (!this.realmsServers.isEmpty()) {
            setupDefaultSubtitle();
        } else {
            setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.no_worlds"));
        }
        this.addRenderableWidget(slotList = new SlotList(this.minecraft, width, height, 3 + 3 /* start offset */ + (font.lineHeight + 2) * 3 /* title is 2 */, 30, (font.lineHeight + 2) * 4));

        this.addRefreshButton(() -> realmsServers = null);

        final int slotWidth = 360 - 4;

        int xPos = width / 2 - slotWidth / 2;
        this.addRenderableWidget(joinButton = Button.builder(Component.translatable("bedrock_realms.viafabricplus.join"), button -> {
            final SlotEntry entry = (SlotEntry) slotList.getFocused();
            if (entry.realmsServer.isExpired()) {
                setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.expired"));
                return;
            } else if (!entry.realmsServer.isCompatible()) {
                setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.incompatible"));
                return;
            }

            try {
                final RealmsJoinInformation server = service.joinWorld(entry.realmsServer);
                if (server.getNetworkProtocol().equalsIgnoreCase(RealmsJoinInformation.PROTOCOL_NETHERNET)) {
                    setupSubtitle(Component.translatable("bedrock_realms.viafabricplus.nethernet_unsupported"));
                    return;
                }

                ConnectionUtil.connect(server.getAddress(), BedrockProtocolVersion.bedrockLatest);
            } catch (final Throwable throwable) {
                error("Failed to join realm", throwable);
            }
        }).pos(xPos, height - 20 - 5).size(115, 20).build());
        joinButton.active = false;

        xPos += 115 + 5;
        this.addRenderableWidget(leaveButton = Button.builder(Component.translatable("bedrock_realms.viafabricplus.leave"), button -> {
            final SlotEntry entry = (SlotEntry) slotList.getFocused();
            service.leaveInvitedRealmAsync(entry.realmsServer).thenAccept(unused -> {
                this.realmsServers.remove(entry.realmsServer);
                INSTANCE.open(prevScreen);
            }).exceptionally(throwable -> error("Failed to leave realm", throwable));
        }).pos(xPos, height - 20 - 5).size(115, 20).build());
        leaveButton.active = false;

        xPos += 115 + 5;
        this.addRenderableWidget(Button.builder(Component.translatable("bedrock_realms.viafabricplus.invite"), button -> {
            final AcceptInvitationCodeScreen screen = new AcceptInvitationCodeScreen(code -> service.acceptInviteAsync(code).thenAccept(world -> {
                this.realmsServers.add(world);
                INSTANCE.open(this);
            }).exceptionally(throwable -> error("Failed to accept invite", throwable)));
            screen.open(this);
        }).pos(xPos, height - 20 - 5).size(115, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        if (slotList != null && joinButton != null && leaveButton != null) {
            joinButton.active = slotList.getFocused() instanceof SlotEntry;
            leaveButton.active = slotList.getFocused() instanceof SlotEntry;
        }
    }

    @Override
    protected boolean subtitleCentered() {
        return realmsServers == null;
    }

    public final class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (RealmsServer realmsServer : BedrockRealmsScreen.this.realmsServers) {
                this.addEntry(new SlotEntry(this, realmsServer));
            }
            initScrollY(scrollAmount);
        }

        @Override
        protected void updateSlotAmount(double amount) {
            scrollAmount = amount;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

    }

    public final class SlotEntry extends VFPListEntry {

        private final SlotList slotList;
        private final RealmsServer realmsServer;

        public SlotEntry(SlotList slotList, RealmsServer realmsServer) {
            this.slotList = slotList;
            this.realmsServer = realmsServer;
        }

        @Override
        public Component getNarration() {
            return Component.nullToEmpty(realmsServer.getName());
        }

        @Override
        public void mappedRender(GuiGraphics context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final Font textRenderer = Minecraft.getInstance().font;

            String name = "";
            final String ownerName = realmsServer.getOwnerName();
            if (ownerName != null && !ownerName.trim().isEmpty()) {
                name += ownerName + " - ";
            }
            final String worldName = realmsServer.getName();
            if (worldName != null && !worldName.trim().isEmpty()) {
                name += worldName;
            }
            name += " (" + realmsServer.getState() + ")";

            context.drawString(textRenderer, name, 3, 3, slotList.getFocused() == this ? Color.ORANGE.getRGB() : -1);

            String version = realmsServer.getWorldType();
            final String activeVersion = realmsServer.getActiveVersion();
            if (activeVersion != null && !activeVersion.trim().isEmpty()) {
                version += " - " + activeVersion;
            }

            context.drawString(textRenderer, version, entryWidth - textRenderer.width(version) - 3, 3, -1);

            final String motd = realmsServer.getMotd();
            if (motd != null) {
                renderScrollableText(Component.nullToEmpty(motd), entryHeight - textRenderer.lineHeight - 3, 0);
            }
        }

    }

}
