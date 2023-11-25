/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.screen.classic4j.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.definition.account.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.screen.common.ProtocolSelectionScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ClassiCubeMFAScreen extends VFPScreen {
    public final static ClassiCubeMFAScreen INSTANCE = new ClassiCubeMFAScreen();

    public ClassiCubeMFAScreen() {
        super("ClassiCube MFA", false);
    }

    @Override
    public void open(Screen prevScreen) {
        this.setupSubtitle(Text.translatable("classicube.viafabricplus.error.logincode"));
        super.open(prevScreen);
    }

    private TextFieldWidget mfaField;

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(mfaField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));

        mfaField.setPlaceholder(Text.literal("MFA"));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
            this.setupSubtitle(Text.translatable("classicube.viafabricplus.loading"));
            final CCAccount account = ClassiCubeAccountHandler.INSTANCE.getAccount();

            ClassiCubeHandler.requestAuthentication(account, mfaField.getText(), new LoginProcessHandler() {
                @Override
                public void handleMfa(CCAccount account) {
                    // Not implemented in this case
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    RenderSystem.recordRenderCall(() -> ClassiCubeServerListScreen.open(prevScreen, this));
                }

                @Override
                public void handleException(Throwable throwable) {
                    setupSubtitle(Text.literal(throwable.getMessage()));
                }
            });
        }).position(width / 2 - 75, mfaField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void close() {
        ClassiCubeAccountHandler.INSTANCE.setAccount(null);
        ProtocolSelectionScreen.INSTANCE.open(prevScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 70, 16777215);

        this.renderSubtitle(context);

        super.render(context, mouseX, mouseY, delta);
    }
}
