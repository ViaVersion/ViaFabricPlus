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

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.features.global.ClassiCubeAccount;
import com.viaversion.viafabricplus.screen.base.VFPScreen;
import de.florianreuth.classic4j.api.LoginProcessHandler;
import de.florianreuth.classic4j.model.classicube.account.CCAccount;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class ClassiCubeMFAScreen extends VFPScreen {

    public ClassiCubeMFAScreen() {
        super(Component.translatable("screen.viafabricplus.classicube_mfa"), true);
    }

    private EditBox mfaField;

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(mfaField = new EditBox(font, width / 2 - 150, 70 + 10, 300, 20, Component.empty()));

        mfaField.setHint(Component.nullToEmpty("MFA"));

        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.login"), _ -> {
            ClassiCubeAccount.authenticate(mfaField.getValue(), new LoginProcessHandler() {
                @Override
                public void handleMfa(CCAccount account) {
                    // Not implemented in this case
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    ViaFabricPlusImpl.impl().screens().classiCubeServerListScreen().open(prevScreen);
                }

                @Override
                public void handleException(Throwable throwable) {
                    ViaFabricPlusImpl.impl().logger().error("Error while logging in to ClassiCube!", throwable);
                    showToast(Component.nullToEmpty(throwable.getMessage()));
                }
            });
        }).pos(width / 2 - 75, mfaField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void onClose() {
        // The user wasn't logged in when opening this screen, so they canceled the login process, and the account can safely be unset
        ClassiCubeAccount.set(null);
        super.onClose();
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.renderScreenTitle(graphics);
    }

}
