package net.pedroksl.advanced_ae.common.definitions;

import java.util.EnumMap;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.EquipmentSlot;

@SuppressWarnings("deprecation")
public enum AAEMaterials implements StringRepresentable, ArmorMaterial {
    QUANTUM_ALLOY(
            "quantum_alloy",
            10,
            Util.make(new EnumMap<>(EquipmentSlot.class), map -> {
                map.put(EquipmentSlot.FEET, 4);
                map.put(EquipmentSlot.LEGS, 6);
                map.put(EquipmentSlot.CHEST, 9);
                map.put(EquipmentSlot.HEAD, 4);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            10.0F,
            0.25F,
            () -> Ingredient.of(AAEItems.QUANTUM_ALLOY));

    public static final /*StringRepresentable*/ StringRepresentable CODEC = null;
    private static final EnumMap<EquipmentSlot, Integer> HEALTH_FUNCTION_FOR_TYPE =
            Util.make(new EnumMap<>(EquipmentSlot.class), (p_266653_) -> {
                p_266653_.put(EquipmentSlot.FEET, 13);
                p_266653_.put(EquipmentSlot.LEGS, 15);
                p_266653_.put(EquipmentSlot.CHEST, 16);
                p_266653_.put(EquipmentSlot.HEAD, 11);
            });
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<EquipmentSlot, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    AAEMaterials(
            String pName,
            int pDurabilityMultiplier,
            EnumMap<EquipmentSlot, Integer> pProtectionFunctionForType,
            int pEnchantmentValue,
            SoundEvent pSound,
            float pToughness,
            float pKnockbackResistance,
            Supplier<Ingredient> pRepairIngredient) {
        this.name = pName;
        this.durabilityMultiplier = pDurabilityMultiplier;
        this.protectionFunctionForType = pProtectionFunctionForType;
        this.enchantmentValue = pEnchantmentValue;
        this.sound = pSound;
        this.toughness = pToughness;
        this.knockbackResistance = pKnockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(pRepairIngredient);
    }

    public int getDurabilityForSlot(EquipmentSlot pType) {
        return HEALTH_FUNCTION_FOR_TYPE.get(pType) * this.durabilityMultiplier;
    }

    public int getDefenseForSlot(EquipmentSlot pType) {
        return this.protectionFunctionForType.get(pType);
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    /**
     * Gets the percentage of knockback resistance provided by armor of the material.
     */
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public String getSerializedName() {
        return this.name;
    }
}
