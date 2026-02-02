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

package com.viaversion.viafabricplus.screen;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.screens.Screen} class which provides some global
 * functions and features used in all screens which are added by ViaFabricPlus.
 * <p>
 * Features:
 * <ul>
 *     <li>Title and subtitle system, see:
 *     <ul>
 *         <li>{@link #setupDefaultSubtitle()}</li>
 *         <li>{@link #setupUrlSubtitle(String)}</li>
 *         <li>{@link #setupSubtitle(Component)}</li>
 *         <li>{@link #setupSubtitle(Component, Button.OnPress)}</li>
 *     </ul>
 *     </li>
 *     <li>Automatically adds a back button when set inside the constructor</li>
 *     <li>Helper functions:
 *     <ul>
 *         <li>{@link #showErrorScreen(Component, Throwable, Screen)}</li>
 *     </ul>
 *     </li>
 * </ul>
 * <p>
 * Terminology:
 * <p>
 *     Instead of creating the screen every time it needs to be opened, the screen is created once and hold by a static
 *     field and later opened by calling the {@link #open(Screen)} method.
 * </p>
 */
public class VFPScreen extends Screen {

    private static final String MOD_URL = "https://github.com/ViaVersion/ViaFabricPlus";

    private final boolean backButton;
    public Screen prevScreen;

    private Component subtitle;
    private Button.OnPress subtitlePressAction;

    private PlainTextButton subtitleWidget;

    public VFPScreen(final String title, final boolean backButton) {
        this(Component.nullToEmpty(title), backButton);
    }

    public VFPScreen(final Component title, final boolean backButton) {
        super(title);
        this.backButton = backButton;
    }

    /**
     * Sets the subtitle and the subtitle press action to the default values
     * The default value of the subtitle is the url to the GitHub repository of VFP
     * The default value of the subtitle press action is to open the url in a confirmation screen
     */
    public void setupDefaultSubtitle() {
        this.setupUrlSubtitle(MOD_URL);
    }

    /**
     * Sets the subtitle and the subtitle press action to the default values
     *
     * @param subtitle The subtitle which should be rendered
     */
    public void setupUrlSubtitle(final String subtitle) {
        this.setupSubtitle(Component.nullToEmpty(subtitle), ConfirmLinkScreen.confirmLink(this, subtitle));
    }

    /**
     * Sets the subtitle and the subtitle press action
     *
     * @param subtitle The subtitle which should be rendered
     */
    public void setupSubtitle(@Nullable final Component subtitle) {
        this.setupSubtitle(subtitle, null);
    }

    /**
     * Sets the subtitle and the subtitle press action
     *
     * @param subtitle            The subtitle which should be rendered
     * @param subtitlePressAction The press action which should be executed when the subtitle is clicked
     */
    public void setupSubtitle(@Nullable final Component subtitle, @Nullable final Button.OnPress subtitlePressAction) {
        this.subtitlePressAction = subtitlePressAction;

        if (subtitleWidget != null) { // Allows removing the subtitle when calling this method twice.
            removeWidget(subtitleWidget);
            subtitleWidget = null;
        }
        if (subtitlePressAction == null) {
            this.subtitle = subtitle;
        } else {
            this.subtitle = null;
            final int subtitleWidth = font.width(subtitle);
            this.addRenderableWidget(subtitleWidget = new PlainTextButton(width / 2 - (subtitleWidth / 2), (font.lineHeight + 2) * 2 + 3, subtitleWidth, font.lineHeight + 2, subtitle, subtitlePressAction, font));
        }
    }

    /**
     * Intended method to open a VFP screen
     *
     * @param prevScreen The current screen from which the VFP screen is opened
     */
    public void open(final Screen prevScreen) {
        this.prevScreen = prevScreen;
        setScreen(this);
    }

    /**
     * Returns this screen instance after setting the previous screen.
     *
     * @param prevScreen The screen to return to when this screen is closed
     * @return This screen instance
     */
    public Screen get(final Screen prevScreen) {
        this.prevScreen = prevScreen;
        return this;
    }

    public static void setScreen(final Screen screen) {
        final Minecraft client = Minecraft.getInstance();

        client.execute(() -> client.setScreen(screen));
    }

    @Override
    protected void init() {
        if (backButton) {
            this.addRenderableWidget(Button.builder(Component.nullToEmpty("<-"), button -> this.onClose()).pos(5, 5).size(20, 20).build());
        }
    }

    public void addRefreshButton(final Runnable click) {
        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.refresh"), button -> {
            click.run();
            minecraft.setScreen(this);
        }).pos(width - 60 - 5, 5).size(60, 20).build());
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.renderTitle(context);
    }

    @Override
    public void onClose() {
        if (prevScreen instanceof VFPScreen vfpScreen) {
            vfpScreen.open(vfpScreen.prevScreen); // Support recursive opening
        } else {
            Minecraft.getInstance().setScreen(prevScreen);
        }
    }

    /**
     * Renders the ViaFabricPlus title
     *
     * @param context The current draw context
     */
    public void renderTitle(final GuiGraphics context) {
        final Matrix3x2fStack matrices = context.pose();

        matrices.pushMatrix();
        matrices.scale(2F, 2F);
        context.drawCenteredString(font, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.popMatrix();

        renderSubtitle(context);
    }

    /**
     * Renders the subtitle that doesn't have a press action
     *
     * @param context The current draw context
     */
    public void renderSubtitle(final GuiGraphics context) {
        if (subtitle != null && subtitlePressAction == null) {
            final int startY = (font.lineHeight + 2) * 2 + 3;
            context.drawCenteredString(font, subtitle, width / 2, subtitleCentered() ? this.height / 2 - startY : startY, -1);
        }
    }

    protected boolean subtitleCentered() {
        // To be overridden
        return false;
    }

    public void renderScreenTitle(final GuiGraphics context) {
        context.drawCenteredString(this.font, this.title, this.width / 2, 70, 16777215);
    }

    public @Nullable Component getSubtitle() {
        return subtitle;
    }

    public @Nullable PlainTextButton getSubtitleWidget() {
        return subtitleWidget;
    }

    /**
     * Opens an error screen with a specific title and throws the given throwable
     *
     * @param title     The title of the error screen
     * @param throwable The throwable which should be thrown
     * @param next      The screen which should be opened after the error screen is closed
     */
    public static void showErrorScreen(final Component title, final Throwable throwable, final Screen next) {
        ViaFabricPlusImpl.INSTANCE.getLogger().error("Something went wrong!", throwable);

        final Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(new AlertScreen(() -> client.setScreen(next), title, Component.translatable("base.viafabricplus.something_went_wrong").append("\n" + throwable.getMessage()), Component.translatable("base.viafabricplus.cancel"), false)));
    }

}
