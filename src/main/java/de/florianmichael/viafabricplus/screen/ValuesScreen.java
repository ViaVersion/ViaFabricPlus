package de.florianmichael.viafabricplus.screen;

import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.viafabricplus.value.ValueHolder;
import de.florianmichael.viafabricplus.value.impl.BooleanValue;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@SuppressWarnings({"DataFlowIssue", "DuplicatedCode"})
public class ValuesScreen extends Screen {
    private final static ValuesScreen INSTANCE = new ValuesScreen();
    public Screen prevScreen;

    protected ValuesScreen() {
        super(Text.literal("Values"));
    }

    public static void open(final Screen current) {
        INSTANCE.prevScreen = current;
        MinecraftClient.getInstance().setScreen(INSTANCE);
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

        drawCenteredText(matrices, textRenderer, "Press right mouse button for toggling protocol sync", width / 2, 3, -1);
        drawCenteredText(matrices, textRenderer, "Press left mouse button for normal toggling", width / 2, textRenderer.fontHeight + 2 + 3, -1);
        drawCenteredText(matrices, textRenderer, "Values that have sync enabled will be toggled depending on the target version.", width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    @Override
    public void close() {
        client.setScreen(prevScreen);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<ValueSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            ValueHolder.values.stream().map(ValueSlot::new).forEach(this::addEntry);
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

    public static class ValueSlot extends AlwaysSelectedEntryListWidget.Entry<ValueSlot> {
        private final AbstractValue<?> value;

        public ValueSlot(AbstractValue<?> value) {
            this.value = value;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.value.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (value instanceof BooleanValue booleanValue) {
                booleanValue.setValue(!booleanValue.getValue());
                ScreenUtil.playClickSound();
            }
            if (value instanceof ProtocolSyncBooleanValue protocolSyncBooleanValue) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    protocolSyncBooleanValue.setSyncWithProtocol(!protocolSyncBooleanValue.isSyncWithProtocol());
                    ScreenUtil.playClickSound();
                }
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && !protocolSyncBooleanValue.isSyncWithProtocol()) {
                    protocolSyncBooleanValue.setValue(!protocolSyncBooleanValue.getValue());
                    ScreenUtil.playClickSound();
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            matrices.push();
            matrices.translate(x, y, 0);
            fill(matrices, 0, 0, entryWidth, entryHeight, Integer.MIN_VALUE);
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            if (value instanceof BooleanValue booleanValue) {
                final boolean isEnabled = booleanValue.getValue();
                drawCenteredText(matrices, textRenderer, (isEnabled ? Formatting.UNDERLINE : "") + booleanValue.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB());
            } else if (value instanceof ProtocolSyncBooleanValue protocolSyncBooleanValue) {
                final boolean isEnabled = protocolSyncBooleanValue.value;
                Color color = isEnabled ? Color.GREEN : Color.RED;
                if (protocolSyncBooleanValue.isSyncWithProtocol()) {
                    drawStringWithShadow(matrices, textRenderer, "Sync", entryWidth - textRenderer.getWidth("Sync") - 3, entryHeight / 2 - textRenderer.fontHeight / 2, Color.ORANGE.getRGB());
                    color = color.darker().darker();
                }
                drawStringWithShadow(matrices, textRenderer, (isEnabled ? Formatting.UNDERLINE.toString() : "") + protocolSyncBooleanValue.getName(), 3, entryHeight / 2 - textRenderer.fontHeight / 2, color.getRGB());
            }
            matrices.pop();
        }
    }
}
