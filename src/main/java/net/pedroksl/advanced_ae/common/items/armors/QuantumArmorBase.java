package net.pedroksl.advanced_ae.common.items.armors;
import net.pedroksl.advanced_ae.common.Addons;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.pedroksl.advanced_ae.client.AAEHotkeys;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.ae2addonlib.registry.helpers.ICreativeTabItem;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationFactory;


public class QuantumArmorBase extends PoweredItem implements IMenuItem, IUpgradeableItem, ICreativeTabItem, IAnimatable {

    private final AnimationFactory factory =
            new AnimationFactory(this);

    protected static final int DEFAULT_TINT_COLOR = AAEColor.PURPLE.argb();

    protected final List<UpgradeType> possibleUpgrades = new ArrayList<>();

    protected static final UUID ATTACK_DAMAGE_MOD = UUID.fromString("7c305230-fd62-4f14-8364-934bfc86ce74");
    protected static final UUID ATTACK_SPEED_MOD = UUID.fromString("47e9e666-e9a4-4bd7-8cce-ea2774b3fbc5");
    protected static final UUID LUCK_MOD = UUID.fromString("64e83581-dcdb-4045-a749-ce2f45d47e48");
    protected static final UUID REACH_MOD = UUID.fromString("2083e57d-4744-4d2b-bad5-5517c13a1734");
    protected static final UUID HP_BUFFER_MOD = UUID.fromString("d610c37e-3223-4e85-80dc-cb6fd58bfc81");
    protected static final UUID STEP_ASSIST_MOD = UUID.fromString("c1a4d757-50dc-49e9-a8a3-8e1b3fc91686");

    protected static final String MENU_TYPE = "aae$menutype";

    protected enum MenuId {
        STANDARD(0),
        WORKBENCH(1),
        CRAFTING(2);

        public final int id;

        MenuId(int id) {
            this.id = id;
        }
    }

    public QuantumArmorBase(ArmorMaterial material, EquipmentSlot slot, Properties properties, DoubleSupplier powerCapacity) {
        super(material, slot, properties.fireResistant().rarity(Rarity.EPIC).stacksTo(1), powerCapacity);
    }

    protected void registerUpgrades(UpgradeType... upgrades) {
        this.possibleUpgrades.addAll(Arrays.asList(upgrades));
    }

    public int getTintColor(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null) {
            if (tag.contains(AAENbt.TINT_COLOR_TAG)) {
                return tag.getInt(AAENbt.TINT_COLOR_TAG);
            }
        }
        return DEFAULT_TINT_COLOR;
    }

    public void setTintColor(Player player, ItemStack stack, int color) {
        stack.getOrCreateTag().putInt(AAENbt.TINT_COLOR_TAG, color);

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);

        var hotkey = AAEHotkeys.INSTANCE.getHotkeyMapping(AAEHotkeysRegistry.Keys.ARMOR_CONFIG.getId());
        if (hotkey != null) {
            lines.add(AAEText.QuantumArmorHotkeyTooltip.text(
                            hotkey.mapping().getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        appendExtraHoverText(stack, context, lines, advancedTooltips);

        if (getLinkedPosition(stack) == null) {
            lines.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
        } else {
            lines.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
        }

        lines.add(new TextComponent(""));
        lines.add(AAEText.QuantumArmorTooltip.text().withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        for (var upgrade : possibleUpgrades) {
            var upgradeComponent =
                    new TranslatableComponent(upgrade.item().get().getDescriptionId());
            if (!hasUpgrade(stack, upgrade)) {
                upgradeComponent.append(AAEText.UpgradeNotInstalled.text());
                upgradeComponent.withStyle(Tooltips.MUTED_COLOR);
            } else {
                upgradeComponent.withStyle(Tooltips.GREEN);
            }
            lines.add(upgradeComponent);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
        if (!(stack.getItem() instanceof QuantumArmorBase armor)) {
            return modifiers;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(modifiers);

        switch (slot) {
            case HEAD -> {
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.LUCK)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.LUCK, 0);
                    var att = new AttributeModifier(LUCK_MOD, "aae_luck", value, AttributeModifier.Operation.ADDITION);
                    builder.put(Attributes.LUCK, att);
                }
            }
            case CHEST -> {
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.HP_BUFFER)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.HP_BUFFER, 0);
                    var att = new AttributeModifier(
                            HP_BUFFER_MOD, "aae_hp_buffer", value, AttributeModifier.Operation.ADDITION);
                    builder.put(Attributes.MAX_HEALTH, att);
                }
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.STRENGTH)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.STRENGTH, 0);
                    var att = new AttributeModifier(
                            ATTACK_DAMAGE_MOD, "aae_strength_boost", value, AttributeModifier.Operation.ADDITION);
                    builder.put(Attributes.ATTACK_DAMAGE, att);
                }
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.ATTACK_SPEED)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.ATTACK_SPEED, 0);
                    var att = new AttributeModifier(
                            ATTACK_SPEED_MOD, "aae_attack_speed", value, AttributeModifier.Operation.ADDITION);
                    builder.put(Attributes.ATTACK_SPEED, att);
                }
            }
            case LEGS -> {
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.REACH)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.REACH, 0);
                    var att = new AttributeModifier(
                            REACH_MOD, "aae_reach_boost", value, AttributeModifier.Operation.ADDITION);
                    builder.put(ForgeMod.REACH_DISTANCE.get(), att);
                    builder.put(ForgeMod.REACH_DISTANCE.get(), att);
                }
            }
            case FEET -> {
                if (armor.isUpgradeEnabledAndPowered(stack, UpgradeType.STEP_ASSIST)) {
                    int value = armor.getUpgradeValue(stack, UpgradeType.STEP_ASSIST, 0);
                    var att = new AttributeModifier(
                            STEP_ASSIST_MOD, "aae_step_assist", value, AttributeModifier.Operation.ADDITION);
                    builder.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), att);
                }
            }
        }
        return builder.build();
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    protected void appendExtraHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {}

    @Override
    public List<UpgradeType> getPossibleUpgrades() {
        return possibleUpgrades;
    }

    protected boolean checkPreconditions(ItemStack item) {
        return !item.isEmpty() && item.getItem() == this;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public boolean openFromEquipmentSlot(Player player, int inventorySlot, ItemStack stack) {
        return openFromEquipmentSlot(player, inventorySlot, stack, false);
    }

    public boolean openFromEquipmentSlot(
            Player player, int inventorySlot, ItemStack stack, boolean returningFromSubmenu) {
        if (!player.level.isClientSide() && checkPreconditions(stack)) {
            return MenuOpener.open(
                    AAEMenus.QUANTUM_ARMOR_CONFIG.get(),
                    player,
                    MenuLocators.forInventorySlot(inventorySlot),
                    returningFromSubmenu);
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(
            ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
        if (entity instanceof Player player) {
            consumeEnergy(player, stack, amount);
        }
        return 0;
    }

    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new QuantumArmorMenuHost(
                player, inventorySlot, stack, (p, subMenu) -> openFromEquipmentSlot(p, inventorySlot, stack, true));
    }

    public static List<QuantumArmorBase> upgradeAvailableFor(UpgradeType type) {
        List<QuantumArmorBase> list = new ArrayList<>();

        for (var equip : AAEItems.getQuantumArmor()) {
            var armor = ((QuantumArmorBase) equip.stack().getItem());
            if (armor.possibleUpgrades.contains(type)) {
                list.add(armor);
            }
        }

        return list;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return super.initCapabilities(stack, nbt);
    }
    @Override
    public void registerControllers(software.bernie.geckolib3.core.manager.AnimationData data) {
    }

    public AnimationFactory getFactory() { return this.factory; }
}
