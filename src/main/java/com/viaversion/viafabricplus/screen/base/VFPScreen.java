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

package com.viaversion.viafabricplus.screen.base;

import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public abstract class VFPScreen extends Screen {

    public static final int ACCENT_COLOR = 0xFF58A6FF;

    protected static final Component SEARCH_TITLE = Component.translatable("base.viafabricplus.search");

    protected static final int SEARCH_TOP = 36; // Below the title
    protected static final int SEARCH_HEIGHT = 20;
    protected static final int SEARCH_MARGIN = 4;

    // Everything a screen puts below the search bar starts here
    protected static final int CONTENT_TOP = SEARCH_TOP + SEARCH_HEIGHT + SEARCH_MARGIN;

    protected static final int FOOTER_HEIGHT = 30;

    private static final Component SEARCH_HINT = SEARCH_TITLE.copy().setStyle(EditBox.SEARCH_HINT_STYLE);
    private static final int SEARCH_WIDTH = 260;

    private static final Component TOAST_TITLE = Component.nullToEmpty("ViaFabricPlus");
    private static final SystemToast.SystemToastId TOAST_ID = new SystemToast.SystemToastId();

    private static final int TITLE_Y = 6; // Inside the doubled matrix of the title
    private static final int SCREEN_TITLE_Y = 70; // Below the doubled title, above the content of the plain screens
    private static final int BACK_BUTTON_WIDTH = 60;
    private static final int BUTTON_WIDTH = 98;
    private static final int BUTTON_MARGIN = 4;

    private final boolean backButton;
    public Screen prevScreen;

    public VFPScreen(final Component title, final boolean backButton) {
        super(title);
        this.backButton = backButton;
    }

    public void open(final Screen prevScreen) {
        this.prevScreen = prevScreen;
        setScreen(this);
    }

    public Screen get(final Screen prevScreen) {
        this.prevScreen = prevScreen;
        return this;
    }

    public static void setScreen(final Screen screen) {
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.setScreen(screen));
    }

    @Override
    protected void init() {
        if (this.backButton) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, _ -> this.onClose()).pos(5, 5).size(BACK_BUTTON_WIDTH, 20).build());
        }
    }

    protected EditBox addSearchBar(final Consumer<String> responder) {
        final int searchWidth = Math.min(SEARCH_WIDTH, this.width - 20);

        final EditBox searchBar = this.addRenderableWidget(new EditBox(this.font, (this.width - searchWidth) / 2, SEARCH_TOP, searchWidth, SEARCH_HEIGHT, SEARCH_TITLE));
        searchBar.setHint(SEARCH_HINT);
        searchBar.setResponder(query -> responder.accept(query.trim().toLowerCase(Locale.ROOT)));
        return searchBar;
    }

    protected void addFooter(final Button... buttons) {
        final int buttonWidth = Math.min(BUTTON_WIDTH, (this.width - (buttons.length + 1) * BUTTON_MARGIN) / buttons.length);

        final LinearLayout footer = LinearLayout.horizontal().spacing(BUTTON_MARGIN);
        for (final Button button : buttons) {
            button.setWidth(buttonWidth);
            footer.addChild(button);
        }

        footer.arrangeElements();
        FrameLayout.centerInRectangle(footer, 0, this.height - FOOTER_HEIGHT, this.width, FOOTER_HEIGHT);
        footer.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        this.renderTitle(graphics);
    }

    @Override
    public void onClose() {
        if (this.prevScreen instanceof VFPScreen vfpScreen) {
            vfpScreen.open(vfpScreen.prevScreen); // Support recursive opening
        } else {
            Minecraft.getInstance().gui.setScreen(this.prevScreen);
        }
    }

    public void renderTitle(final GuiGraphicsExtractor context) {
        final Matrix3x2fStack matrices = context.pose();

        matrices.pushMatrix();
        matrices.scale(2F, 2F);
        context.centeredText(font, "ViaFabricPlus", width / 4, TITLE_Y, ACCENT_COLOR);
        matrices.popMatrix();
    }

    public void renderScreenTitle(final GuiGraphicsExtractor context) {
        context.centeredText(this.font, this.title, this.width / 2, SCREEN_TITLE_Y, -1);
    }

    public static void showToast(final Component message) {
        Minecraft.getInstance().execute(() -> SystemToast.addOrUpdate(Minecraft.getInstance().gui.toastManager(), TOAST_ID, TOAST_TITLE, message));
    }

}
