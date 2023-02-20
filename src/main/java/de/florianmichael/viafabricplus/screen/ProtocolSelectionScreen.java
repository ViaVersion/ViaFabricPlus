package de.florianmichael.viafabricplus.screen;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.api.version.InternalProtocolList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

public class ProtocolSelectionScreen extends Screen {
    public final static ProtocolSelectionScreen INSTANCE = new ProtocolSelectionScreen();

    protected ProtocolSelectionScreen() {
        super(Text.literal("Protocol selection"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 30, height - 20, textRenderer.fontHeight + 2));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Values"), button -> {
        }).position(3, 3).size(98, 20).build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredText(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, -1);
        matrices.pop();

        drawStringWithShadow(matrices, textRenderer, "by EnZaXD/FlorianMichael", 1, height - (textRenderer.fontHeight) * 2, -1);
        drawStringWithShadow(matrices, textRenderer, "https://github.com/FlorianMichael/ViaFabricPlus", 1, height - textRenderer.fontHeight, -1);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<ProtocolSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (ProtocolVersion protocol : InternalProtocolList.getProtocols()) {
                addEntry(new ProtocolSlot(this, protocol));
            }
        }
    }

    public static class ProtocolSlot extends AlwaysSelectedEntryListWidget.Entry<ProtocolSlot> {
        private final SlotList slotList;
        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final SlotList slotList, final ProtocolVersion protocolVersion) {
            this.slotList = slotList;
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
            matrices.translate(x, y, 0);
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredText(matrices, textRenderer, this.protocolVersion.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isSelected ? Color.GREEN.getRGB() : Color.RED.getRGB());
            matrices.pop();

            if (isSelected) this.slotList.setSelected(this);
        }
    }
}
