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

package com.viaversion.viafabricplus.screen.impl.classic4j;

import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.VFPScreen;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public final class ClassiCubeMFAScreen extends VFPScreen {

    public static final ClassiCubeMFAScreen INSTANCE = new ClassiCubeMFAScreen();

    public ClassiCubeMFAScreen() {
        super(Component.translatable("screen.viafabricplus.classicube_mfa"), true);
    }

    private EditBox mfaField;

    @Override
    protected void init() {
        super.init();
        if (this.getSubtitle() == null) {
            this.setupSubtitle(Component.translatable("classic4j_library.viafabricplus.error.logincode"));
        }

        this.addRenderableWidget(mfaField = new EditBox(font, width / 2 - 150, 70 + 10, 300, 20, Component.empty()));

        mfaField.setHint(Component.nullToEmpty("MFA"));

        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.login"), button -> {
            this.setupSubtitle(Component.translatable("classicube.viafabricplus.loading"));
            final CCAccount account = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount();

            ClassiCubeHandler.requestAuthentication(account, mfaField.getValue(), new LoginProcessHandler() {
                @Override
                public void handleMfa(CCAccount account) {
                    // Not implemented in this case
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    ClassiCubeServerListScreen.INSTANCE.open(prevScreen);
                }

                @Override
                public void handleException(Throwable throwable) {
                    setupSubtitle(Component.nullToEmpty(throwable.getMessage()));
                }
            });
        }).pos(width / 2 - 75, mfaField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void onClose() {
        // The user wasn't logged in when opening this screen, so he cancelled the login process, so we can safely unset the account
        SaveManager.INSTANCE.getAccountsSave().setClassicubeAccount(null);
        super.onClose();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderScreenTitle(context);
    }

}
