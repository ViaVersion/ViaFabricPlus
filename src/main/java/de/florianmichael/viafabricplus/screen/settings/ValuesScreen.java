package de.florianmichael.viafabricplus.screen.settings;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.setting.AbstractSetting;
import de.florianmichael.viafabricplus.setting.SettingGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

@SuppressWarnings({"DataFlowIssue", "DuplicatedCode"})
public class ValuesScreen extends Screen {
    public final static ValuesScreen INSTANCE = new ValuesScreen();
    public Screen prevScreen;

    protected ValuesScreen() {
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

    public static class SlotList extends AlwaysSelectedEntryListWidget<DummySlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (SettingGroup group : ViaFabricPlus.getClassWrapper().getSettingGroups()) {
                this.addEntry(new TitleSlot(group.getName()));
                for (AbstractSetting<?> setting : group.getSettings()) {
                    this.addEntry(new SettingSlot(setting));
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
