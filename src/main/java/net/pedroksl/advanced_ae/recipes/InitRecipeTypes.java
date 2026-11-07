package net.pedroksl.advanced_ae.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.IForgeRegistry;

public class InitRecipeTypes {
    private record ToRegister(RecipeType<?> recipeType, ResourceLocation id) {}

    private static final List<ToRegister> toRegister = new ArrayList<>();

    public static <T extends Recipe<?>> RecipeType<T> register(String id) {
        RecipeType<T> type = new RecipeType<>() {};
        toRegister.add(new ToRegister(type, new ResourceLocation(id)));
        return type;
    }

    public static void init(net.minecraftforge.registries.IForgeRegistry<?> registry) {
        for (var toRegister : toRegister) {
            if (toRegister.recipeType instanceof net.minecraftforge.registries.IForgeRegistryEntry<?> entry) {
                entry.setRegistryName(toRegister.id);
            }
            ((net.minecraftforge.registries.IForgeRegistry) registry).register((net.minecraftforge.registries.IForgeRegistryEntry) toRegister.recipeType);
        }
    }
}
