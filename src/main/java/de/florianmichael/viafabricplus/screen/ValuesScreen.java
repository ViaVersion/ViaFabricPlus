package de.florianmichael.viafabricplus.screen;

import de.florianmichael.viafabricplus.value.AbstractValue;
import de.florianmichael.viafabricplus.value.ValueHolder;
import de.florianmichael.viafabricplus.value.impl.BooleanValue;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@SuppressWarnings("DataFlowIssue")
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
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredText(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, -1);
        matrices.pop();
        drawCenteredText(matrices, textRenderer, "https://github.com/FlorianMichael/ViaFabricPlus", width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
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
            if (value instanceof BooleanValue booleanValue) booleanValue.setValue(!booleanValue.getValue());
            if (value instanceof ProtocolSyncBooleanValue protocolSyncBooleanValue) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) protocolSyncBooleanValue.setValue(!protocolSyncBooleanValue.getValue());
                if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) protocolSyncBooleanValue.setSyncWithProtocol(!protocolSyncBooleanValue.isSyncWithProtocol());
            }
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            matrices.push();
            matrices.translate(x, y, 0);
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            if (value instanceof BooleanValue booleanValue) {
                final boolean isEnabled = booleanValue.getValue();
                drawCenteredText(matrices, textRenderer, booleanValue.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB());
            } else if (value instanceof ProtocolSyncBooleanValue protocolSyncBooleanValue) {
                final boolean isEnabled = protocolSyncBooleanValue.getValue();
                drawCenteredText(matrices, textRenderer, protocolSyncBooleanValue.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, protocolSyncBooleanValue.isSyncWithProtocol() ? Color.ORANGE.getRGB() : isEnabled ? Color.GREEN.getRGB() : Color.RED.getRGB());
            }
            matrices.pop();
        }
    }
}
