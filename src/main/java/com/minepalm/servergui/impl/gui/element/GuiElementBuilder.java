package com.minepalm.servergui.impl.gui.element;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

@SuppressWarnings("unused")
public class GuiElementBuilder implements GuiElementBuilderInterface<GuiElementBuilder> {
    protected final Map<Enchantment, Integer> enchantments = new HashMap<>();
    protected Material item = Material.STONE;
    protected static ItemMeta tag;
    protected int count = 1;
    protected Component name = null;
    protected List<Component> lore = new ArrayList<>();
    protected int damage = -1;
    protected GuiElement.ClickCallback callback = GuiElementInterface.EMPTY_CALLBACK;
    protected Set<ItemFlag> flags;

    /**
     * Constructs a GuiElementBuilder with the default options
     */
    public GuiElementBuilder() {
    }

    /**
     * Constructs a GuiElementBuilder with the specified Item.
     *
     * @param item the item to use
     */
    public GuiElementBuilder(Material item) {
        this.item = item;
        this.tag = new ItemStack(item).getItemMeta();
    }

    /**
     * Constructs a GuiElementBuilder with the specified Item
     * and number of items.
     *
     * @param item  the item to use
     * @param count the number of items
     */
    public GuiElementBuilder(Material item, int count) {
        this.item = item;
        this.tag = new ItemStack(item).getItemMeta();
        this.count = count;
    }

    /**
     * Constructs a GuiElementBuilder based on the supplied stack.
     *
     * @param stack the stack to base the builder of
     * @return the constructed builder
     */
    public static GuiElementBuilder from(ItemStack stack) {
        GuiElementBuilder builder = new GuiElementBuilder(stack.getType(), stack.getAmount());
        ItemMeta tag = stack.getItemMeta().clone();

        if (tag.hasDisplayName()) {
            builder.setName(tag.displayName());
        }

        if (tag.hasLore()) {
            builder.setLore(tag.lore());
        }

        if (tag instanceof Damageable damageable && damageable.hasDamage()) {
            builder.setDamage(damageable.getDamage());
        }

        if (tag.hasEnchants()) {
            tag.getEnchants().forEach(builder::enchant);
        }

        builder.tag = tag;

        return builder;
    }

    public static List<Component> getLore(ItemStack stack) {
        return stack.lore();
    }

    /**
     * Sets the type of Item of the element.
     *
     * @param item the item to use
     * @return this element builder
     */
    public GuiElementBuilder setItem(Material item) {
        this.item = item;
        this.tag = new ItemStack(item).getItemMeta();
        return this;
    }

    /**
     * Sets the name of the element.
     *
     * @param name the name to use
     * @return this element builder
     */
    public GuiElementBuilder setName(Component name) {
        this.tag.displayName(name);
        return this;
    }

    /**
     * Sets the number of items in the element.
     *
     * @param count the number of items
     * @return this element builder
     */
    public GuiElementBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * Sets the lore lines of the element.
     *
     * @param lore a list of all the lore lines
     * @return this element builder
     */
    public GuiElementBuilder setLore(List<Component> lore) {
        this.tag.lore(lore);
        return this;
    }

    /**
     * Adds a line of lore to the element.
     *
     * @param lore the line to add
     * @return this element builder
     */
    public GuiElementBuilder addLoreLine(Component lore) {
        List<Component> modifiedLore = this.tag.lore() == null ? new ArrayList<>() : this.tag.lore();
        modifiedLore.add(lore);
        this.tag.lore(modifiedLore);
        return this;
    }

    /**
     * Set the damage of the element. This will only be
     * visible if the item supports has durability.
     *
     * @param damage the amount of durability the item is missing
     * @return this element builder
     */
    public GuiElementBuilder setDamage(int damage) {
        this.damage = damage;
        return this;
    }


    public GuiElementBuilder setFlags(Set<ItemFlag> flags) {
        this.flags = flags;
        return this;
    }

    /**
     * Give the element the specified enchantment.
     *
     * @param enchantment the enchantment to apply
     * @param level       the level of the specified enchantment
     * @return this element builder
     */
    public GuiElementBuilder enchant(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Sets the element to have an enchantment glint.
     *
     * @return this element builder
     */
    public GuiElementBuilder glow() {
        if (!this.tag.hasEnchants()) {
            tag.addEnchant(Enchantment.LUCK, 1, true);
            tag.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    /**
     * Sets the custom model data of the element.
     *
     * @param value the value used for custom model data
     * @return this element builder
     */
    public GuiElementBuilder setCustomModelData(int value) {
        this.tag.setCustomModelData(value);
        return this;
    }

    /**
     * Sets the element to be unbreakable, also hides the durability bar.
     *
     * @return this element builder
     */
    public GuiElementBuilder unbreakable() {
        this.tag.setUnbreakable(true);
        return this;
    }

    /**
     * Sets the skull owner tag of a player head.
     * If the server parameter is not supplied it may lag the client while it loads the texture,
     * otherwise if the server is provided and the {@link GameProfile} contains a UUID then the
     * textures will be loaded by the server. This can take some time the first load,
     * however the skins are cached for later uses so its often less noticeable to let the
     * server load the textures.
     *
     * @param profile the {@link GameProfile} of the owner
     * @return this element builder
     */
    public GuiElementBuilder setSkullOwner(PlayerProfile profile) {
        if (tag instanceof SkullMeta skullMeta) {
            skullMeta.setPlayerProfile(profile);
        }
        return this;
    }

    public GuiElementBuilder setSkullOwner(String texture) {
        if (!(tag instanceof SkullMeta skullMeta)) {
            return this;
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new com.mojang.authlib.properties.Property("textures", texture));

        skullMeta.setPlayerProfile(new CraftPlayerProfile(profile));

        return this;
    }

    @Override
    public GuiElementBuilder setCallback(GuiElement.ClickCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public GuiElementBuilder setCallback(GuiElementInterface.ItemClickCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Constructs an ItemStack using the current builder options.
     * Note that this ignores the callback as it is stored in
     * the {@link GuiElement}.
     *
     * @return this builder as a stack
     * @see GuiElementBuilder#build()
     */
    public ItemStack asStack() {
        ItemStack itemStack = new ItemStack(this.item, this.count);

        if (this.tag != null) {
            itemStack.setItemMeta(tag);
        }

        if (tag instanceof Damageable damageable && this.damage != -1) {
            damageable.setDamage(this.damage);
        }

        for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            itemStack.addEnchantment(entry.getKey(), entry.getValue());
        }

        if (this.lore.size() > 0) {
            tag.lore(this.lore);
        }

        return itemStack;
    }

    public ItemMeta getOrCreateNbt() {
        if (this.tag == null) {
            this.tag = new ItemStack(this.item).getItemMeta();
        }
        return this.tag;
    }

    @Override
    public GuiElement build() {
        return new GuiElement(asStack(), this.callback);
    }
}
