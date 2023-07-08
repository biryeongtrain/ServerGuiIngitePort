package com.minepalm.servergui.impl.gui.element;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Animated Gui Element Builder
 * <br>
 * The {@link AnimatedGuiElementBuilder} is the best way of constructing
 * an {@link AnimatedGuiElement}.
 * It supplies all the methods needed to construct each frame and mesh
 * them together to create the full animation.
 *
 * @see GuiElementBuilderInterface
 */
@SuppressWarnings({"unused"})
public class AnimatedGuiElementBuilder implements GuiElementBuilderInterface<AnimatedGuiElementBuilder> {
    protected final Map<Enchantment, Integer> enchantments = new HashMap<>();
    protected final List<ItemStack> itemStacks = new ArrayList<>();
    protected Material item = Material.STONE;
    protected ItemMeta tag;
    protected int count = 1;
    protected int damage = -1;
    protected GuiElement.ClickCallback callback = GuiElement.EMPTY_CALLBACK;
    protected Set<ItemFlag> hideFlags = new HashSet<>();
    protected int interval = 1;
    protected boolean random = false;

    /**
     * Constructs a AnimatedGuiElementBuilder with the default options
     */
    public AnimatedGuiElementBuilder() {
    }

    /**
     * Constructs a AnimatedGuiElementBuilder with the supplied interval
     *
     * @param interval the time between frame changes
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    /**
     * Sets if the frames should be randomly chosen or more in order
     * of addition.
     *
     * @param value <code>true</code> to select random frames
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setRandom(boolean value) {
        this.random = value;
        return this;
    }

    /**
     * Saves the current stack that is being created.
     * This will add it to the animation and reset the
     * settings awaiting another creation.
     *
     * @return this element builder
     */
    public AnimatedGuiElementBuilder saveItemStack() {
        this.itemStacks.add(asStack());

        this.item = Material.STONE;
        this.tag = null;
        this.count = 1;
        this.damage = -1;
        this.hideFlags = new HashSet<>();
        this.enchantments.clear();

        return this;
    }

    /**
     * Sets the type of Item of the current element.
     *
     * @param item the item to use
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setItem(Material item) {
        this.item = item;
        this.tag = new ItemStack(item).getItemMeta();
        return this;
    }

    /**
     * Sets the name of the current element.
     *
     * @param name the name to use
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setName(Component name) {
        this.tag.displayName(name);
        return this;
    }

    /**
     * Sets the number of items in the current element.
     *
     * @param count the number of items
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * Sets the lore lines of the current element.
     *
     * @param lore a list of all the lore lines
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setLore(List<Component> lore) {
        this.tag.lore(lore);
        return this;
    }

    /**
     * Adds a line of lore to the current element.
     *
     * @param lore the line to add
     * @return this element builder
     */
    public AnimatedGuiElementBuilder addLoreLine(Component lore) {
        List<Component> modifiedLore = this.tag.lore();
        modifiedLore.add(lore);

        this.tag.lore(modifiedLore);
        return this;
    }

    /**
     * Set the damage of the current element. This will only be
     * visible if the item supports has durability.
     *
     * @param damage the amount of durability the item is missing
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setDamage(int damage) {
        this.damage = damage;
        return this;
    }

    /**
     * Hides all {@link net.minecraft.world.item.ItemStack.TooltipPart}s from the current element display
     *
     * @return this element builder
     */
    public AnimatedGuiElementBuilder hideFlags() {
        this.hideFlags.add(ItemFlag.HIDE_ITEM_SPECIFICS);
        return this;
    }

    /**
     * Hides a {@link net.minecraft.world.item.ItemStack.TooltipPart}
     * from the current elements display.
     *
     * @param section the section to hide
     * @return this element builder
     */
    public AnimatedGuiElementBuilder hideFlag(ItemFlag section) {
        this.hideFlags.add(section);
        return this;
    }

    public AnimatedGuiElementBuilder enchant(Enchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    /**
     * Sets the current element to have an enchantment glint.
     *
     * @return this element builder
     */
    public AnimatedGuiElementBuilder glow() {
        this.enchantments.put(Enchantment.LUCK, 1);
        return hideFlag(ItemFlag.HIDE_ENCHANTS);
    }

    /**
     * Sets the custom model data of the current element.
     *
     * @param value the value used for custom model data
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setCustomModelData(int value) {
        this.tag.setCustomModelData(value);
        return this;
    }

    /**
     * Sets the current element to be unbreakable, also hides the durability bar.
     *
     * @return this element builder
     */
    public AnimatedGuiElementBuilder unbreakable() {
        this.tag.setUnbreakable(true);
        return hideFlag(ItemFlag.HIDE_UNBREAKABLE);
    }

    /**
     * Sets the skull owner tag of a player head.
     * This method uses raw values required by client to display the skin
     * Ideal for textures generated with 3rd party websites like mineskin.org
     *
     * @param value     texture value used by client
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setSkullOwner(String value) {
        return this.setSkullOwner(value, null, null);
    }

    /**
     * Sets the skull owner tag of a player head.
     * If the server parameter is not supplied it may lag the client while it loads the texture,
     * otherwise if the server is provided and the {@link PlayerProfile} contains a UUID then the
     * textures will be loaded by the server. This can take some time the first load,
     * however the skins are cached for later uses so its often less noticeable to let the
     * server load the textures.
     *
     * @param profile the {@link PlayerProfile} of the owner
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setSkullOwner(PlayerProfile profile) {
        if (!(this.tag instanceof SkullMeta skullMeta)) {
            return this;
        }
        skullMeta.setPlayerProfile(profile);
        return this;
    }

    /**
     * Sets the skull owner tag of a player head.
     * This method uses raw values required by client to display the skin
     * Ideal for textures generated with 3rd party websites like mineskin.org
     *
     * @param value     texture value used by client
     * @param signature optional signature, will be ignored when set to null
     * @param uuid      UUID of skin owner, if null default will be used
     * @return this element builder
     */
    public AnimatedGuiElementBuilder setSkullOwner(String value, @Nullable String signature, @Nullable UUID uuid) {
        if (!(this.tag instanceof SkullMeta skullMeta)) return this;
        var test = new CraftPlayerProfile(UUID.randomUUID(), null);

        return this;
    }

    @Override
    public AnimatedGuiElementBuilder setCallback(GuiElement.ClickCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public AnimatedGuiElementBuilder setCallback(GuiElementInterface.ItemClickCallback callback) {
        this.callback = callback;
        return this;
    }

    /**
     * Constructs an ItemStack from the current builder options.
     * Note that this ignores the callback as it is stored in
     * the {@link GuiElement}.
     *
     * @return this builder as a stack
     * @see AnimatedGuiElementBuilder#build()
     */
    public ItemStack asStack() {
        ItemStack itemStack = new ItemStack(this.item, this.count);

        if (this.tag != null) {
            itemStack.setItemMeta(this.tag);
        }

        if (this.tag instanceof Damageable damageable && this.damage != -1) {
            damageable.setDamage(this.damage);
        }

        for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
            itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }

        if (this.hideFlags.size() != 0) {
            this.hideFlags.forEach(flag -> tag.addItemFlags(flag));
        }

        return itemStack;
    }


    public AnimatedGuiElement build() {
        return new AnimatedGuiElement(this.itemStacks.toArray(new ItemStack[0]), this.interval, this.random, this.callback);
    }
}
