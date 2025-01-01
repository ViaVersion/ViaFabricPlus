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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.screen.impl.classic4j.BetaCraftScreen;
import com.viaversion.viafabricplus.screen.impl.classic4j.ClassiCubeLoginScreen;
import com.viaversion.viafabricplus.screen.impl.classic4j.ClassiCubeServerListScreen;
import com.viaversion.viafabricplus.screen.impl.realms.BedrockRealmsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ServerListScreen extends VFPScreen {

    public static final ServerListScreen INSTANCE = new ServerListScreen();

    public ServerListScreen() {
        super(Text.translatable("screen.viafabricplus.server_list"), true);
    }

    @Override
    protected void init() {
        super.init();
        this.setupDefaultSubtitle();

        // ClassiCube
        final boolean loggedIn = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount() != null;

        final ButtonWidget.Builder classiCubeBuilder = ButtonWidget.builder(ClassiCubeServerListScreen.INSTANCE.getTitle(), button -> {
            if (!loggedIn) {
                ClassiCubeLoginScreen.INSTANCE.open(this);
                return;
            }
            ClassiCubeServerListScreen.INSTANCE.open(this);
        }).position(this.width / 2 - 100, this.height / 2 - 25).size(200, 20);
        if (!loggedIn) {
            classiCubeBuilder.tooltip(Tooltip.of(Text.translatable("classicube.viafabricplus.warning")));
        }
        this.addDrawableChild(classiCubeBuilder.build());

        final ButtonWidget.Builder betaCraftBuilder = ButtonWidget.builder(BetaCraftScreen.INSTANCE.getTitle(), button -> {
            BetaCraftScreen.INSTANCE.open(this);
        }).position(this.width / 2 - 100, this.height / 2 - 25 + 20 + 3).size(200, 20);
        if (BetaCraftScreen.SERVER_LIST == null) {
            betaCraftBuilder.tooltip(Tooltip.of(Text.translatable("betacraft.viafabricplus.warning")));
        }
        this.addDrawableChild(betaCraftBuilder.build());

        final ButtonWidget.Builder bedrockRealmsBuilder = ButtonWidget.builder(BedrockRealmsScreen.INSTANCE.getTitle(), button -> {
            BedrockRealmsScreen.INSTANCE.open(this);
        }).position(this.width / 2 - 100, this.height / 2 - 25 + 40 + 6).size(200, 20);
        final boolean missingAccount = SaveManager.INSTANCE.getAccountsSave().getBedrockAccount() == null; // Only check for presence, later validate
        if (missingAccount) {
            bedrockRealmsBuilder.tooltip(Tooltip.of(Text.translatable("bedrock_realms.viafabricplus.warning")));
        }

        final ButtonWidget bedrockRealmsButton = bedrockRealmsBuilder.build();
        this.addDrawableChild(bedrockRealmsButton);
        if (missingAccount) {
            bedrockRealmsButton.active = false;
        }
    }

}
