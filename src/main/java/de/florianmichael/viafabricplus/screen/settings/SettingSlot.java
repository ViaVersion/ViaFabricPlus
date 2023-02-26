package de.florianmichael.viafabricplus.screen.settings;

import de.florianmichael.viafabricplus.setting.AbstractSetting;
import de.florianmichael.viafabricplus.setting.groups.GeneralSettings;
import de.florianmichael.viafabricplus.setting.impl.BooleanSetting;
import de.florianmichael.viafabricplus.setting.impl.ModeSetting;
import de.florianmichael.viafabricplus.setting.impl.ProtocolSyncBooleanSetting;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Arrays;

public class SettingSlot extends DummySlot {
    private final AbstractSetting<?> value;

    public SettingSlot(AbstractSetting<?> value) {
        this.value = value;
    }

    @Override
    public Text getNarration() {
        return Text.literal(this.value.getName());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (value instanceof BooleanSetting booleanValue) {
            booleanValue.setValue(!booleanValue.getValue());
            ScreenUtil.playClickSound();
        }
        if (value instanceof ModeSetting modeValue) {
            final int currentIndex = Arrays.stream(modeValue.getOptions()).toList().indexOf(modeValue.value) + 1;
            modeValue.setValue(currentIndex > modeValue.getOptions().length - 1 ? 0 : currentIndex);
            ScreenUtil.playClickSound();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        matrices.push();
        matrices.translate(x, y, 0);
        DrawableHelper.fill(matrices, 0, 0, entryWidth - 4 /* int i = this.left + (this.width - entryWidth) / 2; int j = this.left + (this.width + entryWidth) / 2; */, entryHeight, Integer.MIN_VALUE);
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (value instanceof BooleanSetting booleanValue) {
            final String text = booleanValue.getValue() ? "On" : "Off";
            Color color = booleanValue.getValue() ? Color.GREEN : Color.RED;

            final int length = textRenderer.drawWithShadow(matrices, Formatting.GRAY + booleanValue.getName(), 3, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
            if (value instanceof ProtocolSyncBooleanSetting protocolSyncBooleanValue) {
                textRenderer.drawWithShadow(matrices, "(" + protocolSyncBooleanValue.getProtocolRange().toString() + ")", length + 2, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
                if (GeneralSettings.getClassWrapper().automaticallyChangeValuesBasedOnTheCurrentVersion.getValue()) color = color.darker().darker();
            }

            textRenderer.drawWithShadow(matrices, text, entryWidth - textRenderer.getWidth(text) - 3 - 3, entryHeight / 2F - textRenderer.fontHeight / 2F, color.getRGB());
        } else if (value instanceof ModeSetting modeValue) {
            textRenderer.drawWithShadow(matrices, Formatting.GRAY + modeValue.getName(), 3, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
            textRenderer.drawWithShadow(matrices, modeValue.getValue(), entryWidth - textRenderer.getWidth(modeValue.getValue()) - 3 - 3, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
        }
        matrices.pop();
    }
}
