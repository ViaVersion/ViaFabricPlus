package de.florianmichael.viafabricplus.screen.settings;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TitleSlot extends DummySlot {
    private final String name;

    public TitleSlot(String name) {
        this.name = name;
    }

    @Override
    public Text getNarration() {
        return Text.literal(this.name);
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.push();
        matrices.translate(x, y, 0);
        textRenderer.drawWithShadow(matrices, Formatting.BOLD + this.name, 3, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
        matrices.pop();
    }
}
