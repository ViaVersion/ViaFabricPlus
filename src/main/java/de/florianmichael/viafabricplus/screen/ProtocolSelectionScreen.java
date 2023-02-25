package de.florianmichael.viafabricplus.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;

@SuppressWarnings({"DataFlowIssue", "DuplicatedCode"})
public class ProtocolSelectionScreen extends Screen {
    private final static ProtocolSelectionScreen INSTANCE = new ProtocolSelectionScreen();
    public Screen prevScreen;

    protected ProtocolSelectionScreen() {
        super(Text.literal("Protocol selection"));
    }

    public static void open(final Screen current) {
        INSTANCE.prevScreen = current;
        MinecraftClient.getInstance().setScreen(INSTANCE);
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, textRenderer.fontHeight + 2));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(0, height - 20).size(20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Values"), button -> ValuesScreen.open(this)).position(0, 0).size(98, 20).build());
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

    public static class SlotList extends AlwaysSelectedEntryListWidget<ProtocolSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            InternalProtocolList.getProtocols().stream().map(ProtocolSlot::new).forEach(this::addEntry);
        }
    }

    public static class ProtocolSlot extends AlwaysSelectedEntryListWidget.Entry<ProtocolSlot> {
        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.protocolVersion.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ViaLoadingBase.getClassWrapper().reload(this.protocolVersion);
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final boolean isSelected = ViaLoadingBase.getTargetVersion().getVersion() == protocolVersion.getVersion();

            matrices.push();
            matrices.translate(x, y - 1, 0);
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredText(matrices, textRenderer, (isSelected ? Formatting.UNDERLINE : "") + this.protocolVersion.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isSelected ? Color.GREEN.getRGB() : Color.RED.getRGB());
            matrices.pop();
        }
    }
}
