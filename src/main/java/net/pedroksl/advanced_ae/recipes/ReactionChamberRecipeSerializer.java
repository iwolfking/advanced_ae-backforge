package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.stacks.GenericStack;

public class ReactionChamberRecipeSerializer implements RecipeSerializer<ReactionChamberRecipe> {

    public static final ReactionChamberRecipeSerializer INSTANCE = new ReactionChamberRecipeSerializer();

    private ReactionChamberRecipeSerializer() {}

    private ResourceLocation registryName;
    @Override
    public ResourceLocation getRegistryName() {
        return this.registryName;
    }
    @Override
    public ReactionChamberRecipeSerializer setRegistryName(ResourceLocation name) {
        this.registryName = name;
        return this;
    }
    public static final Codec<GenericStack> GENERIC_STACK_CODEC = RecordCodecBuilder.create(
            builder -> builder.group(CompoundTag.CODEC.fieldOf("output").forGetter(GenericStack::writeTag))
                    .apply(builder, GenericStack::readTag));

    @Override
    public ReactionChamberRecipe fromJson(ResourceLocation id, JsonObject json) {

        GenericStack output =
                GENERIC_STACK_CODEC.parse(JsonOps.INSTANCE, json).result().get();

        JsonArray array = GsonHelper.getAsJsonArray(json, "input_items");
        List<IngredientStack.Item> inputs = new ArrayList<>();
        for (var element : array) {
            inputs.add(IngredientStack.Item.fromJson(element));
        }

        IngredientStack.Fluid fluid = IngredientStack.Fluid.fromJson(json.get("fluid"));

        int energy = json.get("energy").getAsInt();

        return new ReactionChamberRecipe(id, output, inputs, fluid, energy);
    }

    @Override
    public Class<RecipeSerializer<?>> getRegistryType() {
        return (Class<RecipeSerializer<?>>) (Class<?>) RecipeSerializer.class;
    }

    @Override
    public @Nullable ReactionChamberRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        var output = GenericStack.readBuffer(buffer);
        var inputSize = buffer.readInt();
        List<IngredientStack.Item> inputs = new ArrayList<>();
        for (int i = 0; i < inputSize; i++) {
            inputs.add(IngredientStack.Item.fromNetwork(buffer));
        }
        var fluid = IngredientStack.Fluid.fromNetwork(buffer);
        var energy = buffer.readInt();

        return new ReactionChamberRecipe(id, output, inputs, fluid, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ReactionChamberRecipe recipe) {
        GenericStack.writeBuffer(recipe.output, buffer);
        buffer.writeInt(recipe.inputs.size());
        for (var input : recipe.inputs) {
            input.toNetwork(buffer);
        }
        recipe.fluid.toNetwork(buffer);
        buffer.writeInt(recipe.energy);
    }
}
