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

package com.viaversion.viafabricplus.injection.mixin.features.ui.remove_newer_screen_features;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.settings.impl.VisualSettings;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OpenToLanScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(GameMenuScreen.class)
public abstract class MixinGameMenuScreen extends Screen {

    @Shadow
    protected abstract ButtonWidget createButton(Text text, Supplier<Screen> screenSupplier);

    @Shadow
    @Final
    private static Text PLAYER_REPORTING_TEXT;

    @Shadow
    @Final
    private static Text SHARE_TO_LAN_TEXT;

    @Shadow
    @Final
    private static Text OPTIONS_TEXT;

    @Shadow
    @Final
    private static Text RETURN_TO_GAME_TEXT;

    @Shadow
    @Final
    private static Text ADVANCEMENTS_TEXT;

    @Shadow
    @Final
    private static Text STATS_TEXT;

    @Shadow
    @Final
    private static int NORMAL_BUTTON_WIDTH;

    @Unique
    private int viaFabricPlus$disconnectButtonWidth;

    @Unique
    private ButtonWidget.PressAction viaFabricPlus$disconnectSupplier;

    protected MixinGameMenuScreen(Text title) {
        super(title);
    }

    @WrapOperation(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;createButton(Lnet/minecraft/text/Text;Ljava/util/function/Supplier;)Lnet/minecraft/client/gui/widget/ButtonWidget;"), require = 0)
    private ButtonWidget replaceButtons(GameMenuScreen instance, Text text, Supplier<Screen> screenSupplier, Operation<ButtonWidget> original) {
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() == 0) {
            // Player reporting -> share to lan
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19) && text.equals(PLAYER_REPORTING_TEXT)) {
                final ButtonWidget button = ButtonWidget.builder(SHARE_TO_LAN_TEXT, buttonWidget -> new OpenToLanScreen(instance)).width(NORMAL_BUTTON_WIDTH).build();
                button.active = false;
                return button;
            }
            // Advancements -> disconnect
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1) && text.equals(ADVANCEMENTS_TEXT)) {
                return ButtonWidget.builder(ScreenTexts.DISCONNECT, viaFabricPlus$disconnectSupplier).width(viaFabricPlus$disconnectButtonWidth).build();
            }
        } else if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() == 1) {
            // Player reporting -> Social interactions
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_19) && text.equals(PLAYER_REPORTING_TEXT)) {
                return createButton(SocialInteractionsScreen.TITLE, () -> new SocialInteractionsScreen(instance));
            }
        }
        return original.call(instance, text, screenSupplier);
    }

    @WrapWithCondition(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;addFeedbackAndBugsButtons(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/gui/widget/GridWidget$Adder;)V"), require = 0)
    private boolean removeFeedbackAndBugsButtons(Screen parentScreen, GridWidget.Adder gridAdder) {
        return VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() != 0 || ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_13_2);
    }

    @Inject(method = "initWidgets", at = @At("RETURN"))
    private void moveButtonPositions(CallbackInfo ci) {
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() != 0) {
            return;
        }
        // Manually adjust positions in older versions since the grid system doesn't work for these layouts
        final Consumer<ButtonWidget> moveDown = buttonWidget -> buttonWidget.setY(buttonWidget.getY() + ButtonWidget.DEFAULT_HEIGHT);

        // Move all buttons below feebdack/bug down since they are removed
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
            viaFabricPlus$applyTo(OPTIONS_TEXT, moveDown);
            viaFabricPlus$applyTo(SHARE_TO_LAN_TEXT, moveDown);
            viaFabricPlus$applyTo(ScreenTexts.DISCONNECT, moveDown);
        }

        // Tracked for dimensions in case some mod changes them
        final ButtonWidget returnToGame = viaFabricPlus$getButton(RETURN_TO_GAME_TEXT);
        if (returnToGame == null) {
            return;
        }

        // Make options button wider since lan is removed
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
            viaFabricPlus$applyTo(OPTIONS_TEXT, buttonWidget -> {
                buttonWidget.setX(returnToGame.getX());
                buttonWidget.setWidth(returnToGame.getWidth());
            });
        }

        // Make space between return to game and options, put disconnect button below options
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1)) {
            viaFabricPlus$applyTo(OPTIONS_TEXT, moveDown);
            viaFabricPlus$applyTo(ScreenTexts.DISCONNECT, buttonWidget -> {
                // Magical offset which would be calculated by the grid system, nothing we can do about it
                buttonWidget.setY(returnToGame.getY() + ButtonWidget.DEFAULT_HEIGHT + 3);
            });
        }
    }

    @WrapWithCondition(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"), require = 0)
    private boolean removeButtons(GridWidget.Adder instance, Widget widget) {
        if (VisualSettings.INSTANCE.changeGameMenuScreenLayout.getIndex() != 0) {
            return true;
        }
        // Mods could add other widgets as well
        if (widget instanceof ButtonWidget button) {
            // Remove buttons and track their width/press action for later, mods might be injecting into them
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.r1_2_4tor1_2_5)) {
                if (button.getMessage().equals(SHARE_TO_LAN_TEXT)) {
                    return false;
                }
            }
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_4tob1_4_1)) {
                if (button.getMessage().equals(STATS_TEXT)) {
                    return false;
                } else if (button.getMessage().equals(ScreenTexts.DISCONNECT)) {
                    viaFabricPlus$disconnectSupplier = buttonWidget -> button.onPress();
                    viaFabricPlus$disconnectButtonWidth = button.getWidth();
                    return false;
                }
            }
        }
        return true;
    }

    @Unique
    private void viaFabricPlus$applyTo(final Text text, final Consumer<ButtonWidget> action) {
        final ButtonWidget button = viaFabricPlus$getButton(text);
        if (button != null) {
            action.accept(button);
        }
    }

    @Unique
    private ButtonWidget viaFabricPlus$getButton(final Text text) {
        for (Element child : children()) {
            if (child instanceof ButtonWidget buttonWidget) {
                if (buttonWidget.getMessage().equals(text)) {
                    return buttonWidget;
                }
            }
        }
        return null;
    }

}
