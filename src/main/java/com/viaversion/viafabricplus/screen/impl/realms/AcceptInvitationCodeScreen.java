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

import com.viaversion.viafabricplus.screen.VFPScreen;
import java.util.function.Consumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class AcceptInvitationCodeScreen extends VFPScreen {

    private final Consumer<String> serviceHandler;

    public AcceptInvitationCodeScreen(Consumer<String> serviceHandler) {
        super(Component.translatable("screen.viafabricplus.accept_invite"), true);

        this.serviceHandler = serviceHandler;
    }

    @Override
    protected void init() {
        super.init();
        setupDefaultSubtitle();

        final EditBox codeField = new EditBox(font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.empty());
        codeField.setHint(Component.translatable("base.viafabricplus.code"));

        this.addRenderableWidget(codeField);

        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.accept"), button -> {
            this.serviceHandler.accept(codeField.getValue());
            onClose();
        }).pos(this.width / 2 - Button.DEFAULT_WIDTH / 2, this.height / 2 + 20).build());
    }

}
