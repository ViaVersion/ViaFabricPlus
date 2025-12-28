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

package com.viaversion.viafabricplus.visuals.injection.mixin.remove_newer_screen_features;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class MixinPauseScreen extends Screen {

    @Shadow
    @Final
    private static Component PLAYER_REPORTING;
    @Shadow
    @Final
    private static Component SHARE_TO_LAN;
    @Shadow
    @Final
    private static Component OPTIONS;
    @Shadow
    @Final
    private static Component RETURN_TO_GAME;
    @Shadow
    @Final
    private static Component ADVANCEMENTS;
    @Shadow
    @Final
    private static Component STATS;
    @Shadow
    @Final
    private static int BUTTON_WIDTH_HALF;

    @Unique
    private int viaFabricPlusVisuals$disconnectButtonWidth;

    @Unique
    private Button.OnPress viaFabricPlusVisuals$disconnectSupplier;

    protected MixinPauseScreen(Component title) {
        super(title);
    }

    @Shadow
    protected abstract Button openScreenButton(Component text, Supplier<Screen> screenSupplier);

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/PauseScreen;openScreenButton(Lnet/minecraft/network/chat/Component;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/components/Button;"), require = 0)
    private Button replaceButtons(PauseScreen instance, Component text, Supplier<Screen> screenSupplier, Operation<Button> original) {
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() == 0) {
            // Player reporting -> share to lan
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19) && text.equals(PLAYER_REPORTING)) {
                return Button.builder(SHARE_TO_LAN, buttonWidget -> new ShareToLanScreen(instance)).width(BUTTON_WIDTH_HALF).build();
            }
            // Advancements -> disconnect
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1) && text.equals(ADVANCEMENTS)) {
                return Button.builder(CommonComponents.GUI_DISCONNECT, viaFabricPlusVisuals$disconnectSupplier).width(viaFabricPlusVisuals$disconnectButtonWidth).build();
            }
        } else if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() == 1) {
            // Player reporting -> Social interactions
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19) && text.equals(PLAYER_REPORTING)) {
                return openScreenButton(SocialInteractionsScreen.TITLE, () -> new SocialInteractionsScreen(instance));
            }
        }
        return original.call(instance, text, screenSupplier);
    }

    @WrapWithCondition(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/PauseScreen;addFeedbackButtons(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;)V"), require = 0)
    private boolean removeFeedbackAndBugsButtons(Screen parentScreen, GridLayout.RowHelper gridAdder) {
        return VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() != 0 || ViaFabricPlus.getImpl().getTargetVersion().newerThan(ProtocolVersion.v1_13_2);
    }

    @Inject(method = "createPauseMenu", at = @At("RETURN"))
    private void moveButtonPositions(CallbackInfo ci) {
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() != 0) {
            return;
        }
        // Manually adjust positions in older versions since the grid system doesn't work for these layouts
        final Consumer<Button> moveDown = buttonWidget -> buttonWidget.setY(buttonWidget.getY() + Button.DEFAULT_HEIGHT);

        // Move all buttons below feebdack/bug down since they are removed
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            viaFabricPlusVisuals$applyTo(OPTIONS, moveDown);
            viaFabricPlusVisuals$applyTo(CommonComponents.GUI_DISCONNECT, moveDown);

            viaFabricPlusVisuals$applyTo(SHARE_TO_LAN, button -> {
                moveDown.accept(button);
                button.active = false;
            });
        }

        // Tracked for dimensions in case some mod changes them
        final Button returnToGame = viaFabricPlusVisuals$getButton(RETURN_TO_GAME);
        if (returnToGame == null) {
            return;
        }

        // Make options button wider since lan is removed
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            viaFabricPlusVisuals$applyTo(OPTIONS, buttonWidget -> {
                buttonWidget.setX(returnToGame.getX());
                buttonWidget.setWidth(returnToGame.getWidth());
            });
        }

        // Make space between return to game and options, put disconnect button below options
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1)) {
            viaFabricPlusVisuals$applyTo(OPTIONS, moveDown);
            viaFabricPlusVisuals$applyTo(CommonComponents.GUI_DISCONNECT, buttonWidget -> {
                // Magical offset which would be calculated by the grid system, nothing we can do about it
                buttonWidget.setY(returnToGame.getY() + Button.DEFAULT_HEIGHT + 3);
            });
        }
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;"), require = 0)
    private <T extends LayoutElement> T removeButtons(GridLayout.RowHelper instance, T layoutElement, Operation<T> original) {
        // Mods could add other widgets as well
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() == 0 && layoutElement instanceof Button button) {
            // Remove buttons and track their width/press action for later, mods might be injecting into them
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                if (button.getMessage().equals(SHARE_TO_LAN)) {
                    return null;
                }
            }
            if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1)) {
                if (button.getMessage().equals(STATS)) {
                    return null;
                } else if (button.getMessage().equals(CommonComponents.GUI_DISCONNECT)) {
                    viaFabricPlusVisuals$disconnectSupplier = buttonWidget -> button.onPress(null);
                    viaFabricPlusVisuals$disconnectButtonWidth = button.getWidth();
                    return null;
                }
            }
        }

        return original.call(instance, layoutElement);
    }

    @Unique
    private void viaFabricPlusVisuals$applyTo(final Component text, final Consumer<Button> action) {
        final Button button = viaFabricPlusVisuals$getButton(text);
        if (button != null) {
            action.accept(button);
        }
    }

    @Unique
    private Button viaFabricPlusVisuals$getButton(final Component text) {
        for (GuiEventListener child : children()) {
            if (child instanceof Button buttonWidget) {
                if (buttonWidget.getMessage().equals(text)) {
                    return buttonWidget;
                }
            }
        }
        return null;
    }

}
