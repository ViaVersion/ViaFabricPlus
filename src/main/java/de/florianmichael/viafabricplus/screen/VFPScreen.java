/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.screen;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * This class is a wrapper for the {@link net.minecraft.client.gui.screen.Screen} class which provides some global
 * functions and features used in all screens which are added by ViaFabricPlus
 */
public class VFPScreen extends Screen {

    private static final String MOD_URL = "https://github.com/ViaVersion/ViaFabricPlus";

    private final boolean backButton;
    public Screen prevScreen;

    private Text subtitle;
    private ButtonWidget.PressAction subtitlePressAction;

    private PressableTextWidget subtitleWidget;

    public VFPScreen(final String title, final boolean backButton) {
        this(Text.of(title), backButton);
    }

    public VFPScreen(final Text title, final boolean backButton) {
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
        this.setupSubtitle(Text.of(subtitle), ConfirmLinkScreen.opening(this, subtitle));
    }


    /**
     * Sets the subtitle and the subtitle press action
     *
     * @param subtitle The subtitle which should be rendered
     */
    public void setupSubtitle(@Nullable final Text subtitle) {
        this.setupSubtitle(subtitle, null);
    }

    /**
     * Sets the subtitle and the subtitle press action
     *
     * @param subtitle The subtitle which should be rendered
     * @param subtitlePressAction The press action which should be executed when the subtitle is clicked
     */
    public void setupSubtitle(@Nullable final Text subtitle, @Nullable final ButtonWidget.PressAction subtitlePressAction) {
        this.subtitle = subtitle;
        this.subtitlePressAction = subtitlePressAction;

        if (subtitleWidget != null) { // Allows removing the subtitle when calling this method twice.
            remove(subtitleWidget);
            subtitleWidget = null;
        }
        if (subtitlePressAction != null) {
            final int subtitleWidth = textRenderer.getWidth(subtitle);
            this.addDrawableChild(subtitleWidget = new PressableTextWidget(width / 2 - (subtitleWidth / 2), (textRenderer.fontHeight + 2) * 2 + 3, subtitleWidth, textRenderer.fontHeight + 2, subtitle, subtitlePressAction, textRenderer));
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

    public static void setScreen(final Screen screen) {
        final MinecraftClient client = MinecraftClient.getInstance();

        client.execute(() -> client.setScreen(screen));
    }

    @Override
    protected void init() {
        if (backButton) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("<-"), button -> this.close()).position(5, 5).size(20, 20).build());
        }
    }

    @Override
    public void close() {
        if (prevScreen instanceof VFPScreen vfpScreen) {
            vfpScreen.open(vfpScreen.prevScreen); // Support recursive opening
        } else {
            MinecraftClient.getInstance().setScreen(prevScreen);
        }
    }

    /**
     * Renders the ViaFabricPlus title
     *
     * @param context The current draw context
     */
    public void renderTitle(final DrawContext context) {
        final MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        context.drawCenteredTextWithShadow(textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();

        renderSubtitle(context);
    }

    /**
     * Renders the subtitle that doesn't have a press action
     *
     * @param context The current draw context
     */
    public void renderSubtitle(final DrawContext context) {
        if (subtitle != null && subtitlePressAction == null) {
            final int startY = (textRenderer.fontHeight + 2) * 2 + 3;
            context.drawCenteredTextWithShadow(textRenderer, subtitle, width / 2, subtitleCentered() ? this.height / 2 - startY : startY, -1);
        }
    }

    protected boolean subtitleCentered() {
        // To be overriden
        return false;
    }

    /**
     * Plays Minecraft's button click sound
     */
    public static void playClickSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Opens an error screen with a specific title and throws the given throwable
     *
     * @param title     The title of the error screen
     * @param throwable The throwable which should be thrown
     * @param next      The screen which should be opened after the error screen is closed
     */
    public static void showErrorScreen(final Text title, final Throwable throwable, final Screen next) {
        ViaFabricPlus.global().getLogger().error("Something went wrong!", throwable);

        final MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(new NoticeScreen(() -> client.setScreen(next), title, Text.translatable("base.viafabricplus.something_went_wrong").append("\n" + throwable.getMessage()), Text.translatable("base.viafabricplus.cancel"), false)));
    }

}
