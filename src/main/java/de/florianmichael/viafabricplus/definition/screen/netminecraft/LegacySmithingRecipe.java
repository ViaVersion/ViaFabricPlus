package de.florianmichael.viafabricplus.definition.screen.netminecraft;

import com.google.gson.JsonObject;
import java.util.stream.Stream;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class LegacySmithingRecipe implements SmithingRecipe {
    public final static Serializer SERIALIZER = new Serializer();
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;
    private final Identifier id;

    public LegacySmithingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        this.id = id;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public boolean matches(Inventory inventory, World world) {
        return this.base.test(inventory.getStack(0)) && this.addition.test(inventory.getStack(1));
    }

    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack itemStack = this.result.copy();
        NbtCompound nbtCompound = inventory.getStack(0).getNbt();
        if (nbtCompound != null) {
            itemStack.setNbt(nbtCompound.copy());
        }

        return itemStack;
    }

    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result;
    }

    public boolean testTemplate(ItemStack stack) {
        return false;
    }

    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    public Identifier getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public boolean isEmpty() {
        return Stream.of(this.base, this.addition).anyMatch((ingredient) -> ingredient.getMatchingStacks().length == 0);
    }

    public static class Serializer implements RecipeSerializer<LegacySmithingRecipe> {
        public Serializer() {
        }

        public LegacySmithingRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "base"));
            Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new LegacySmithingRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public LegacySmithingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new LegacySmithingRecipe(identifier, ingredient, ingredient2, itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, LegacySmithingRecipe legacySmithingRecipe) {
            legacySmithingRecipe.base.write(packetByteBuf);
            legacySmithingRecipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(legacySmithingRecipe.result);
        }
    }
}
