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

package com.viaversion.viafabricplus.screen.impl.realms;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.util.ConnectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.raphimc.minecraftauth.MinecraftAuth;
import net.raphimc.minecraftauth.service.realms.BedrockRealmsService;
import net.raphimc.minecraftauth.service.realms.model.RealmsWorld;
import net.raphimc.minecraftauth.step.bedrock.session.StepFullBedrockSession;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import net.raphimc.viabedrock.protocol.data.ProtocolConstants;
import org.apache.logging.log4j.Level;

import java.awt.*;
import java.util.List;

public final class BedrockRealmsScreen extends VFPScreen {

    public static final BedrockRealmsScreen INSTANCE = new BedrockRealmsScreen();

    private BedrockRealmsService service;
    private List<RealmsWorld> realmsWorlds;

    private SlotList slotList;
    private ButtonWidget joinButton;
    private ButtonWidget leaveButton;

    public BedrockRealmsScreen() {
        super(Text.translatable("screen.viafabricplus.bedrock_realms"), true);
    }

    @Override
    protected void init() {
        super.init();

        if (realmsWorlds != null) {
            createView();
            return;
        }

        setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.availability_check"));
        Util.getDownloadWorkerExecutor().execute(this::loadRealms);
    }

    private void loadRealms() {
        final StepFullBedrockSession.FullBedrockSession account = SaveManager.INSTANCE.getAccountsSave().refreshAndGetBedrockAccount();
        if (account == null) { // Just in case...
            setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.warning"));
            return;
        }
        service = new BedrockRealmsService(MinecraftAuth.createHttpClient(), ProtocolConstants.BEDROCK_VERSION_NAME, account.getRealmsXsts());
        service.isAvailable().thenAccept(state -> {
            if (state) {
                service.getWorlds().thenAccept(realmsWorlds -> {
                    this.realmsWorlds = realmsWorlds;
                    createView();
                }).exceptionally(throwable -> error("Failed to load realm worlds", throwable));
            } else {
                setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.unavailable"));
            }
        }).exceptionally(throwable -> error("Failed to check realms availability", throwable));
    }

    private Void error(final String message, final Throwable throwable) {
        setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.error"));
        ViaFabricPlusImpl.INSTANCE.getLogger().log(Level.ERROR, message, throwable);
        return null;
    }

    private void createView() {
        if (!this.realmsWorlds.isEmpty()) {
            setupDefaultSubtitle();
        } else {
            setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.no_worlds"));
        }
        this.addDrawableChild(slotList = new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, 30, (textRenderer.fontHeight + 2) * 4));

        this.addRefreshButton(() -> realmsWorlds = null);

        final int slotWidth = 360 - 4;

        int xPos = width / 2 - slotWidth / 2;
        this.addDrawableChild(joinButton = ButtonWidget.builder(Text.translatable("bedrock_realms.viafabricplus.join"), button -> {
            final SlotEntry entry = (SlotEntry) slotList.getFocused();
            if (entry.realmsWorld.isExpired()) {
                setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.expired"));
                return;
            } else if (!entry.realmsWorld.isCompatible()) {
                setupSubtitle(Text.translatable("bedrock_realms.viafabricplus.incompatible"));
                return;
            }
            service.joinWorld(entry.realmsWorld)
                .thenAcceptAsync(address -> ConnectionUtil.connect(address, BedrockProtocolVersion.bedrockLatest), client)
                .exceptionally(throwable -> error("Failed to join realm", throwable));
        }).position(xPos, height - 20 - 5).size(115, 20).build());
        joinButton.active = false;

        xPos += 115 + 5;
        this.addDrawableChild(leaveButton = ButtonWidget.builder(Text.translatable("bedrock_realms.viafabricplus.leave"), button -> {
            final SlotEntry entry = (SlotEntry) slotList.getFocused();
            service.leaveInvitedRealm(entry.realmsWorld).thenAccept(unused -> {
                this.realmsWorlds.remove(entry.realmsWorld);
                INSTANCE.open(prevScreen);
            }).exceptionally(throwable -> error("Failed to leave realm", throwable));
        }).position(xPos, height - 20 - 5).size(115, 20).build());
        leaveButton.active = false;

        xPos += 115 + 5;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("bedrock_realms.viafabricplus.invite"), button -> {
            final AcceptInvitationCodeScreen screen = new AcceptInvitationCodeScreen(code -> service.acceptInvite(code).thenAccept(world -> {
                this.realmsWorlds.add(world);
                INSTANCE.open(this);
            }).exceptionally(throwable -> error("Failed to accept invite", throwable)));
            screen.open(this);
        }).position(xPos, height - 20 - 5).size(115, 20).build());
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
        return realmsWorlds == null;
    }

    public final class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (RealmsWorld realmsWorld : BedrockRealmsScreen.this.realmsWorlds) {
                this.addEntry(new SlotEntry(this, realmsWorld));
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
        private final RealmsWorld realmsWorld;

        public SlotEntry(SlotList slotList, RealmsWorld realmsWorld) {
            this.slotList = slotList;
            this.realmsWorld = realmsWorld;
        }

        @Override
        public Text getNarration() {
            return Text.of(realmsWorld.getName());
        }

        @Override
        public void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            String name = "";
            final String ownerName = realmsWorld.getOwnerName();
            if (ownerName != null && !ownerName.trim().isEmpty()) {
                name += ownerName + " - ";
            }
            final String worldName = realmsWorld.getName();
            if (worldName != null && !worldName.trim().isEmpty()) {
                name += worldName;
            }
            name += " (" + realmsWorld.getState() + ")";

            context.drawTextWithShadow(textRenderer, name, 3, 3, slotList.getFocused() == this ? Color.ORANGE.getRGB() : -1);

            String version = realmsWorld.getWorldType();
            final String activeVersion = realmsWorld.getActiveVersion();
            if (activeVersion != null && !activeVersion.trim().isEmpty()) {
                version += " - " + activeVersion;
            }

            context.drawTextWithShadow(textRenderer, version, entryWidth - textRenderer.getWidth(version) - 4 - 3, 3, -1);

            final String motd = realmsWorld.getMotd();
            if (motd != null) {
                renderScrollableText(Text.of(motd), entryHeight - textRenderer.fontHeight - 3, 3 * 2);
            }
        }

    }

}
