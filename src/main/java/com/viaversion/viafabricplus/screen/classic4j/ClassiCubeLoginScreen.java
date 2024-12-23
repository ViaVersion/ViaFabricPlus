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

package com.viaversion.viafabricplus.screen.classic4j;

import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.injection.access.ITextFieldWidget;
import com.viaversion.viafabricplus.base.save.impl.AccountsSave;
import com.viaversion.viafabricplus.base.screen.VFPScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ClassiCubeLoginScreen extends VFPScreen {

    public static final ClassiCubeLoginScreen INSTANCE = new ClassiCubeLoginScreen();

    public ClassiCubeLoginScreen() {
        super(Text.translatable("screen.viafabricplus.classicube_login"), true);
    }

    private TextFieldWidget nameField;
    private TextFieldWidget passwordField;

    @Override
    protected void init() {
        super.init();
        this.setupSubtitle(Text.translatable("classicube.viafabricplus.account"), ConfirmLinkScreen.opening(this, ClassiCubeHandler.CLASSICUBE_ROOT_URI.toString()));

        this.addDrawableChild(nameField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));
        this.addDrawableChild(passwordField = new TextFieldWidget(textRenderer, width / 2 - 150, nameField.getY() + 20 + 5, 300, 20, Text.empty()));
        passwordField.setRenderTextProvider((s, integer) -> Text.of("*".repeat(s.length())).asOrderedText());

        nameField.setPlaceholder(Text.translatable("base.viafabricplus.name"));
        passwordField.setPlaceholder(Text.translatable("base.viafabricplus.password"));

        nameField.setMaxLength(Integer.MAX_VALUE);
        passwordField.setMaxLength(Integer.MAX_VALUE);

        ((ITextFieldWidget) nameField).viaFabricPlus$unlockForbiddenCharacters();
        ((ITextFieldWidget) passwordField).viaFabricPlus$unlockForbiddenCharacters();

        final AccountsSave accountsSave = ViaFabricPlusImpl.INSTANCE.getSaveManager().getAccountsSave();
        if (accountsSave.getClassicubeAccount() != null) {
            nameField.setText(accountsSave.getClassicubeAccount().username());
            passwordField.setText(accountsSave.getClassicubeAccount().username());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("base.viafabricplus.login"), button -> {
            accountsSave.setClassicubeAccount(new CCAccount(nameField.getText(), passwordField.getText()));
            this.setupSubtitle(Text.translatable("classicube.viafabricplus.loading"));

            ClassiCubeHandler.requestAuthentication(accountsSave.getClassicubeAccount(), null, new LoginProcessHandler() {

                @Override
                public void handleMfa(CCAccount account) {
                    ClassiCubeMFAScreen.INSTANCE.open(prevScreen);
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    ClassiCubeServerListScreen.open(prevScreen, this);
                }

                @Override
                public void handleException(Throwable throwable) {
                    ViaFabricPlusImpl.INSTANCE.logger().error("Error while logging in to ClassiCube!", throwable);
                    setupSubtitle(Text.of(throwable.getMessage()));
                }
            });
        }).position(width / 2 - 75, passwordField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void close() {
        // The user wasn't logged in when opening this screen, so he cancelled the login process, so we can safely unset the account
        ViaFabricPlusImpl.INSTANCE.getSaveManager().getAccountsSave().setClassicubeAccount(null);
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderScreenTitle(context);
    }

}
