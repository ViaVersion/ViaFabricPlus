/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.screen.settings;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.BooleanSettingRenderer;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.ModeSettingRenderer;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.ProtocolSyncBooleanSettingRenderer;
import de.florianmichael.viafabricplus.screen.settings.settingrenderer.meta.TitleRenderer;
import de.florianmichael.viafabricplus.settings.AbstractSetting;
import de.florianmichael.viafabricplus.settings.SettingGroup;
import de.florianmichael.viafabricplus.settings.impl.BooleanSetting;
import de.florianmichael.viafabricplus.settings.impl.ModeSetting;
import de.florianmichael.viafabricplus.settings.impl.ProtocolSyncBooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"DataFlowIssue", "DuplicatedCode"})
public class SettingsScreen extends Screen {
    public final static SettingsScreen INSTANCE = new SettingsScreen();
    public Screen prevScreen;

    protected SettingsScreen() {
        super(Text.literal("Values"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, (textRenderer.fontHeight + 2) * 2));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(0, height - 20).size(20, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredText(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();
        drawCenteredText(matrices, textRenderer, "https://github.com/FlorianMichael/ViaFabricPlus", width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    @Override
    public void close() {
        client.setScreen(prevScreen);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<AbstractSettingRenderer> {

        public static final Map<Class<? extends AbstractSetting<?>>, Class<? extends AbstractSettingRenderer>> RENDERER = new HashMap<>() {
            {
                put(BooleanSetting.class, BooleanSettingRenderer.class);
                put(ProtocolSyncBooleanSetting.class, ProtocolSyncBooleanSettingRenderer.class);
                put(ModeSetting.class, ModeSettingRenderer.class);
            }
        };

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (SettingGroup group : ViaFabricPlus.getClassWrapper().getSettingGroups()) {
                this.addEntry(new TitleRenderer(group.getName()));
                for (AbstractSetting<?> setting : group.getSettings()) {
                    try {
                        this.addEntry(RENDERER.get(setting.getClass()).getConstructor(setting.getClass()).newInstance(setting));
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 5;
        }
    }
}
