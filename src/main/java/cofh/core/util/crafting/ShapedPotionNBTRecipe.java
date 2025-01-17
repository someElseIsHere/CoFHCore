package cofh.core.util.crafting;

import cofh.lib.util.crafting.ShapedRecipeInternal;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.Map;

import static cofh.core.init.CoreRecipeSerializers.SHAPED_POTION_RECIPE_SERIALIZER;

public class ShapedPotionNBTRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    private final ShapedRecipeInternal wrappedRecipe;

    public ShapedPotionNBTRecipe(ResourceLocation pId, String pGroup, CraftingBookCategory pCategory, int pWidth, int pHeight, NonNullList<Ingredient> pRecipeItems, ItemStack pResult) {

        wrappedRecipe = new ShapedRecipeInternal(pId, pGroup, pCategory, pWidth, pHeight, pRecipeItems, pResult);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {

        // boolean flag
        boolean potionItem = false;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == Items.POTION) {
                if (!PotionUtils.getMobEffects(stack).isEmpty()) {
                    potionItem = true;
                    break;
                }
            }
        }
        return potionItem && wrappedRecipe.matches(inv, worldIn);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {

        ItemStack result = wrappedRecipe.getResultItem(registryAccess).copy();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == Items.POTION && stack.getTag() != null) {
                result.setTag(stack.getTag().copy());
                break;
            }
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {

        return wrappedRecipe.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {

        return wrappedRecipe.getResultItem(registryAccess);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {

        return wrappedRecipe.getIngredients();
    }

    @Override
    public ResourceLocation getId() {

        return wrappedRecipe.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return SHAPED_POTION_RECIPE_SERIALIZER.get();
    }

    @Override
    public int getRecipeWidth() {

        return wrappedRecipe.getWidth();
    }

    @Override
    public int getRecipeHeight() {

        return wrappedRecipe.getHeight();
    }

    @Override
    public CraftingBookCategory category() {

        return wrappedRecipe.category;
    }

    // region SERIALIZER
    public static class Serializer implements RecipeSerializer<ShapedPotionNBTRecipe> {

        public ShapedPotionNBTRecipe fromJson(ResourceLocation recipeId, JsonObject json) {

            String s = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory craftingbookcategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String) null), CraftingBookCategory.MISC);
            Map<String, Ingredient> map = ShapedRecipeInternal.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = ShapedRecipeInternal.shrink(ShapedRecipeInternal.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = ShapedRecipeInternal.dissolvePattern(astring, map, i, j);
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new ShapedPotionNBTRecipe(recipeId, s, craftingbookcategory, i, j, nonnulllist, itemstack);
        }

        @Override
        public ShapedPotionNBTRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

            int i = buffer.readVarInt();
            int j = buffer.readVarInt();
            String s = buffer.readUtf(32767);
            CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.fromNetwork(buffer));
            }
            ItemStack itemstack = buffer.readItem();
            return new ShapedPotionNBTRecipe(recipeId, s, craftingbookcategory, i, j, nonnulllist, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedPotionNBTRecipe recipe) {

            buffer.writeVarInt(recipe.getRecipeWidth());
            buffer.writeVarInt(recipe.getRecipeHeight());
            buffer.writeUtf(recipe.getGroup());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.wrappedRecipe.result);
        }

    }
    // endregion
}
