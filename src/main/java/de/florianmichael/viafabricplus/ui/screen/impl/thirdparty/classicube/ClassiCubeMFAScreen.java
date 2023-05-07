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
package de.florianmichael.viafabricplus.ui.screen.impl.thirdparty.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.highlevel.CCAccount;
import de.florianmichael.classic4j.model.classicube.highlevel.CCError;
import de.florianmichael.viafabricplus.definition.c0_30.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.integration.Classic4JImpl;
import de.florianmichael.viafabricplus.ui.screen.VFPScreen;
import de.florianmichael.viafabricplus.ui.screen.impl.base.ProtocolSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ClassiCubeMFAScreen extends VFPScreen {
    public final static ClassiCubeMFAScreen INSTANCE = new ClassiCubeMFAScreen();

    public ClassiCubeMFAScreen() {
        super("ClassiCube MFA", true);
    }

    @Override
    public void open(Screen prevScreen) {
        status = Classic4JImpl.fromError(CCError.LOGIN_CODE);
        super.open(prevScreen);
    }

    private TextFieldWidget mfaField;

    private Text status;

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(mfaField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));

        mfaField.setPlaceholder(Text.literal("MFA"));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
            status = Text.translatable("classicube.viafabricplus.loading");
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
                    status = Text.literal(throwable.getMessage());
                }
            });
        }).position(width / 2 - 75, mfaField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        mfaField.tick();
    }

    @Override
    public void close() {
        ProtocolSelectionScreen.INSTANCE.open(prevScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
        drawCenteredTextWithShadow(matrices, this.textRenderer, this.status, this.width / 2, 1, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
