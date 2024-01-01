/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.screen.classic4j;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.injection.access.ITextFieldWidget;
import de.florianmichael.viafabricplus.save.impl.AccountsSave;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.screen.base.ProtocolSelectionScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ClassiCubeLoginScreen extends VFPScreen {
    public static final ClassiCubeLoginScreen INSTANCE = new ClassiCubeLoginScreen();

    public ClassiCubeLoginScreen() {
        super("ClassiCube Login", true);
    }

    @Override
    public void open(Screen prevScreen) {
        this.setupSubtitle(Text.translatable("classicube.viafabricplus.account"), ConfirmLinkScreen.opening(this, ClassiCubeHandler.CLASSICUBE_ROOT_URI.toString()));
        super.open(prevScreen);
    }

    private TextFieldWidget nameField;
    private TextFieldWidget passwordField;

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(nameField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));
        this.addDrawableChild(passwordField = new TextFieldWidget(textRenderer, width / 2 - 150, nameField.getY() + 20 + 5, 300, 20, Text.empty()));
        passwordField.setRenderTextProvider((s, integer) -> Text.literal("*".repeat(s.length())).asOrderedText());

        nameField.setPlaceholder(Text.literal("Name"));
        passwordField.setPlaceholder(Text.literal("Password"));

        nameField.setMaxLength(Integer.MAX_VALUE);
        passwordField.setMaxLength(Integer.MAX_VALUE);

        ((ITextFieldWidget) nameField).viaFabricPlus$unlockForbiddenCharacters();
        ((ITextFieldWidget) passwordField).viaFabricPlus$unlockForbiddenCharacters();

        final AccountsSave accountsSave = ViaFabricPlus.global().getSaveManager().getAccountsSave();
        if (accountsSave.getClassicubeAccount() != null) {
            nameField.setText(accountsSave.getClassicubeAccount().username());
            passwordField.setText(accountsSave.getClassicubeAccount().username());
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
            accountsSave.setClassicubeAccount(new CCAccount(nameField.getText(), passwordField.getText()));
            this.setupSubtitle(Text.translatable("classicube.viafabricplus.loading"));

            ClassiCubeHandler.requestAuthentication(accountsSave.getClassicubeAccount(), null, new LoginProcessHandler() {

                @Override
                public void handleMfa(CCAccount account) {
                    ClassiCubeMFAScreen.INSTANCE.open(prevScreen);
                }

                @Override
                public void handleSuccessfulLogin(CCAccount account) {
                    RenderSystem.recordRenderCall(() -> ClassiCubeServerListScreen.open(prevScreen, this));
                }

                @Override
                public void handleException(Throwable throwable) {
                    ViaFabricPlus.global().getLogger().error("Error while logging in to ClassiCube: " + throwable.getMessage());
                    setupSubtitle(Text.literal(throwable.getMessage()));
                }
            });
        }).position(width / 2 - 75, passwordField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void close() {
        // The user wasn't logged in when opening this screen, so he cancelled the login process, so we can safely unset the account
        ViaFabricPlus.global().getSaveManager().getAccountsSave().setClassicubeAccount(null);
        ProtocolSelectionScreen.INSTANCE.open(prevScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTitle(context);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 70, 16777215);
    }

}
