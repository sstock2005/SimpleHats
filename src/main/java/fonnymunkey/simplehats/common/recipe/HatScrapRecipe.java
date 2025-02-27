package fonnymunkey.simplehats.common.recipe;

import fonnymunkey.simplehats.SimpleHats;
import fonnymunkey.simplehats.common.init.ModRegistry;
import fonnymunkey.simplehats.common.item.HatItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class HatScrapRecipe extends SpecialCraftingRecipe {
    public HatScrapRecipe(Identifier location, CraftingRecipeCategory category) {
        super(location, category);
    }

    @Override
    public Identifier getId() {
        return new Identifier(SimpleHats.modId, "hatscraps");
    }

    @Override
    public boolean matches(RecipeInputInventory craftingInventory, World level) {
        int[] list = processInventory(craftingInventory);
        return list[0] != -1 && list[1] != -1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory craftingInventory, DynamicRegistryManager reg) {
        int[] list  = processInventory(craftingInventory);
        if(list[0] != -1 && list[1] != -1) {
            return switch(((HatItem)craftingInventory.getStack(list[0]).getItem()).getHatEntry().getHatSeason()) {
                case EASTER -> new ItemStack(ModRegistry.HATSCRAPS_EASTER);
                case SUMMER -> new ItemStack(ModRegistry.HATSCRAPS_SUMMER);
                case HALLOWEEN -> new ItemStack(ModRegistry.HATSCRAPS_HALLOWEEN);
                case FESTIVE -> new ItemStack(ModRegistry.HATSCRAPS_FESTIVE);
                case NONE -> switch (((HatItem)craftingInventory.getStack(list[0]).getItem()).getHatEntry().getHatRarity()) {
                    case COMMON -> new ItemStack(ModRegistry.HATSCRAPS_COMMON);
                    case UNCOMMON -> new ItemStack(ModRegistry.HATSCRAPS_UNCOMMON);
                    case RARE, EPIC -> new ItemStack(ModRegistry.HATSCRAPS_RARE);
                };
            };
        }
        return ItemStack.EMPTY;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory craftingInventory) {
        DefaultedList<ItemStack> remainList = DefaultedList.ofSize(craftingInventory.size(), ItemStack.EMPTY);

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack slot = craftingInventory.getStack(i);
            if(!slot.isEmpty() && slot.getItem() instanceof ShearsItem) {
                ItemStack slot1 = slot.copy();
                if(slot1.isDamageable()) {
                    slot1.setDamage(slot.getDamage() + 1);
                    if(slot1.getDamage() >= slot1.getMaxDamage()) {
                        slot1 = ItemStack.EMPTY;
                    }
                }
                remainList.set(i, slot1);
                break;
            }
        }
        return remainList;
    }

    private static int[] processInventory(RecipeInputInventory craftingInventory) {
        int totalItems = 0;
        int[] list = new int[]{-1, -1};
        for(int i =0; i < craftingInventory.size(); i++) {
            ItemStack slot = craftingInventory.getStack(i);
            if(!slot.isEmpty()) {
                totalItems++;
                if(slot.getItem() instanceof HatItem hat && hat!=ModRegistry.HATSPECIAL) list[0] = i;
                if(slot.getItem() instanceof ShearsItem) list[1] = i;
            }
        }
        if(totalItems == 2) return list;
        return new int[]{-1, -1};
    }

    @Override
    public boolean fits(int width, int height) {
        return width*height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistry.HATSCRAP_SERIALIZER;
    }
}