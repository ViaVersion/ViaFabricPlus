/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.screen.realms;

import com.viaversion.viafabricplus.base.screen.VFPScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class AcceptInvitationCodeScreen extends VFPScreen {

    private final Consumer<String> serviceHandler;

    public AcceptInvitationCodeScreen(Consumer<String> serviceHandler) {
        super(Text.translatable("screen.viafabricplus.accept_invite"), true);

        this.serviceHandler = serviceHandler;
    }

    @Override
    protected void init() {
        super.init();
        setupDefaultSubtitle();

        final TextFieldWidget codeField = new TextFieldWidget(textRenderer, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Text.empty());
        codeField.setPlaceholder(Text.translatable("base.viafabricplus.code"));

        this.addDrawableChild(codeField);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("base.viafabricplus.accept"), button -> {
            this.serviceHandler.accept(codeField.getText());
            close();
        }).position(this.width / 2 - ButtonWidget.DEFAULT_WIDTH / 2, this.height / 2 + 20).build());
    }

}
