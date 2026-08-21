package cy.jdkdigital.productivebees.datagen.recipe.provider;

import appeng.recipes.transform.TransformCircumstance;
import appeng.recipes.transform.TransformRecipe;
import com.blakebr0.mysticalagriculture.crafting.recipe.InfusionRecipe;
import com.enderio.enderio.content.machines.alloy.AlloySmeltingRecipe;
import com.glodblock.github.extendedae.recipe.CrystalAssemblerRecipeBuilder;
import com.klikli_dev.occultism.datagen.recipe.builders.SpiritFireRecipeBuilder;
import com.breakinblocks.neovitae.common.recipe.aravitae.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.mojang.datafixers.util.Either;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivemetalworks.datagen.recipe.ItemCastingRecipeBuilder;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.cyclops.evilcraft.core.recipe.type.RecipeBloodInfuser;
import owmii.powah.block.energizing.EnergizingRecipe;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static cy.jdkdigital.productivebees.datagen.recipe.BeeRecipeHelper.beeExists;
import static cy.jdkdigital.productivebees.datagen.recipe.BeeRecipeHelper.beeIngredient;
import static cy.jdkdigital.productivebees.datagen.recipe.BeeRecipeHelper.beeResult;

/**
 * Emits the other mods' machine recipes that upgrade a bee into that mod's bee, building each
 * mod's own recipe object or builder so the JSON shape can't drift from theirs. Every foreign
 * item and tag is resolved through the registry, so a renamed id fails datagen.
 */
public class CompatRecipeProvider extends RecipeProvider
{
    private final HolderGetter<Fluid> fluids;

    public CompatRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.fluids = registries.lookupOrThrow(Registries.FLUID);
    }

    @Override
    protected void buildRecipes() {
        ae2Transform();
        enderIoAlloySmelting();
        enderIoEndergyAlloySmelting();
        extendedAeCrystalAssembler();
        mysticalAgricultureInfusion();
        powahEnergizing();
        occultismSpiritFire();
        neoVitaeAltarAndForge();
        ftbicCanning();
        evilcraftBloody();
        productiveMetalworksCasting();
    }

    // -- ae2 ------------------------------------------------------------------------------------

    private void ae2Transform() {
        transform("ae2/transform/redstone_crystal_bee", "ae2/redstone_crystal", FluidTags.WATER,
                List.of(item("ae2:fluix_crystal"), beeIngredient("dusts/redstone"), item("minecraft:glowstone_dust")),
                "dusts/redstone");
        transform("ae2/transform/sky_bronze_bee", "ae2/sky_bronze", FluidTags.LAVA,
                List.of(beeIngredient("raw_materials/copper"), item("ae2:charged_certus_quartz_crystal"), item("ae2:sky_stone_block")),
                "raw_materials/copper");
        transform("ae2/transform/sky_osmium_bee", "ae2/sky_osmium", FluidTags.LAVA,
                List.of(beeIngredient("raw_materials/osmium"), item("ae2:charged_certus_quartz_crystal"), item("ae2:sky_stone_block")),
                "raw_materials/osmium");
        transform("ae2/transform/sky_steel_bee", "ae2/sky_steel", FluidTags.LAVA,
                List.of(beeIngredient("ae2/spatial"), tag("c:storage_blocks/sky_steel")),
                "ae2/spatial");
    }

    private void transform(String path, String resultBee, TagKey<Fluid> fluid, List<Ingredient> ingredients, String inputBee) {
        save(path, new TransformRecipe(ingredients, beeResult(resultBee), TransformCircumstance.fluid(fluid)),
                new ModLoadedCondition("ae2"), beeExists(inputBee), beeExists(resultBee));
    }

    // -- enderio --------------------------------------------------------------------------------

    private void enderIoAlloySmelting() {
        alloy("conductive_alloy_bee", "enderio/conductive_alloy", 10000,
                List.of(beeIngredient("raw_materials/iron"), beeIngredient("raw_materials/copper")),
                "raw_materials/iron", "raw_materials/copper");
        alloy("dark_steel_bee", "enderio/dark_steel", 20000,
                List.of(beeIngredient("raw_materials/iron"), tag("c:dusts/coal"), beeIngredient("obsidian")),
                "raw_materials/iron", "obsidian");
        alloy("end_steel_bee", "enderio/end_steel", 20000,
                List.of(tag("c:end_stones"), beeIngredient("enderio/dark_steel"), beeIngredient("obsidian")),
                "enderio/dark_steel", "obsidian");
        alloy("energetic_alloy_bee", "enderio/energetic_alloy", 10000,
                List.of(beeIngredient("dusts/redstone"), beeIngredient("raw_materials/gold"), beeIngredient("dusts/glowing")),
                "dusts/redstone", "raw_materials/gold", "dusts/glowing");
        alloy("pulsating_alloy_bee", "enderio/pulsating_alloy", 10000,
                List.of(beeIngredient("raw_materials/iron"), beeIngredient("ender")),
                "raw_materials/iron", "ender");
        alloy("redstone_alloy_bee", "enderio/redstone_alloy", 10000,
                List.of(beeIngredient("dusts/redstone"), beeIngredient("ae2/silicon")),
                "dusts/redstone", "ae2/silicon");
        alloy("soularium_bee", "enderio/soularium", 10000,
                List.of(beeIngredient("ghostly"), beeIngredient("raw_materials/gold")),
                "ghostly", "raw_materials/gold");
        alloy("vibrant_alloy_bee", "enderio/vibrant_alloy", 10000,
                List.of(beeIngredient("enderio/energetic_alloy"), beeIngredient("ender")),
                "enderio/energetic_alloy", "ender");
    }

    // -- enderio_endergy ------------------------------------------------------------------------

    /**
     * Endergy's alloys smelt in EnderIO's own machine, so they reuse the alloy recipe type.
     * Energy climbs with the chain: crude steel is an iron-bee upgrade, stellar sits above
     * melodic and costs a nether star.
     */
    private void enderIoEndergyAlloySmelting() {
        alloy("enderio_endergy", "crude_steel_bee", "enderio_endergy/crude_steel", 10000,
                List.of(beeIngredient("raw_materials/iron"), item("minecraft:clay_ball"), tag("c:gravels")),
                "raw_materials/iron");
        alloy("enderio_endergy", "crystalline_alloy_bee", "enderio_endergy/crystalline_alloy", 20000,
                List.of(beeIngredient("raw_materials/gold"), beeIngredient("prismarine"), tag("c:dusts/grains_of_pizeallity")),
                "raw_materials/gold", "prismarine");
        alloy("enderio_endergy", "melodic_alloy_bee", "enderio_endergy/melodic_alloy", 40000,
                List.of(beeIngredient("enderio/end_steel"), beeIngredient("gems/amethyst"), item("minecraft:popped_chorus_fruit")),
                "enderio/end_steel", "gems/amethyst");
        alloy("enderio_endergy", "stellar_alloy_bee", "enderio_endergy/stellar_alloy", 60000,
                List.of(beeIngredient("enderio_endergy/melodic_alloy"), item("minecraft:nether_star"), tag("c:dusts/grains_of_the_end")),
                "enderio_endergy/melodic_alloy");
        alloy("enderio_endergy", "vivid_alloy_bee", "enderio_endergy/vivid_alloy", 30000,
                List.of(beeIngredient("gems/lapis"), beeIngredient("raw_materials/gold"), tag("c:dusts/grains_of_vibrancy")),
                "gems/lapis", "raw_materials/gold");
    }

    private void alloy(String name, String resultBee, int energy, List<Ingredient> inputs, String... inputBees) {
        alloy("enderio", name, resultBee, energy, inputs, inputBees);
    }

    private void alloy(String modid, String name, String resultBee, int energy, List<Ingredient> inputs, String... inputBees) {
        List<SizedIngredient> sized = inputs.stream().map(i -> new SizedIngredient(i, 1)).toList();
        save(modid + "/" + name, new AlloySmeltingRecipe(sized, beeResult(resultBee), energy, 0.3f),
                conditions(modid, resultBee, inputBees));
    }

    // -- extendedae -----------------------------------------------------------------------------

    private void extendedAeCrystalAssembler() {
        RecipeOutput output = this.output.withConditions(
                new ModLoadedCondition("extendedae"), beeExists("ae2/fluix"), beeExists("ae2/entro"));
        CrystalAssemblerRecipeBuilder.assemble(beeResult("ae2/entro"), this.items, this.fluids)
                .input(beeIngredient("ae2/fluix"))
                .input(itemTag("c:dusts/entro"), 16)
                .fluid(Fluids.WATER, 1000)
                .power(8000)
                .save(output, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "extendedae/crystal_assembler/entro_bee"));
    }

    // -- mysticalagriculture --------------------------------------------------------------------

    private void mysticalAgricultureInfusion() {
        infusion("inferium_bee", "mysticalagriculture/prosperity", "mysticalagriculture/inferium",
                "mysticalagriculture:inferium_block", "mysticalagriculture:inferium_essence", "mysticalagriculture");
        infusion("prudentium_bee", "mysticalagriculture/inferium", "mysticalagriculture/prudentium",
                "mysticalagriculture:prudentium_block", "mysticalagriculture:prudentium_essence", "mysticalagriculture");
        infusion("tertium_bee", "mysticalagriculture/prudentium", "mysticalagriculture/tertium",
                "mysticalagriculture:tertium_block", "mysticalagriculture:tertium_essence", "mysticalagriculture");
        infusion("imperium_bee", "mysticalagriculture/tertium", "mysticalagriculture/imperium",
                "mysticalagriculture:imperium_block", "mysticalagriculture:imperium_essence", "mysticalagriculture");
        infusion("supremium_bee", "mysticalagriculture/imperium", "mysticalagriculture/supremium",
                "mysticalagriculture:supremium_block", "mysticalagriculture:supremium_essence", "mysticalagriculture");
        infusion("insanium_bee", "mysticalagriculture/supremium", "mysticalagriculture/insanium",
                "mysticalagradditions:insanium_block", "mysticalagradditions:insanium_essence", "mysticalagradditions");
    }

    /** The altar takes the bee; the eight pedestals alternate storage block and essence. */
    private void infusion(String name, String inputBee, String resultBee, String block, String essence, String modid) {
        List<Ingredient> pedestals = List.of(
                item(block), item(essence), item(block), item(essence),
                item(block), item(essence), item(block), item(essence));
        save("mysticalagriculture/" + name,
                new InfusionRecipe(beeIngredient(inputBee), pedestals, beeResult(resultBee), false),
                new ModLoadedCondition(modid), beeExists(inputBee), beeExists(resultBee));
    }

    // -- powah ----------------------------------------------------------------------------------

    private void powahEnergizing() {
        energizing("energized_steel_bee", "powah/energized_steel", 100_000L, List.of(
                item("powah:energized_steel_block"), beeIngredient("raw_materials/iron"),
                beeIngredient("raw_materials/gold"), beeIngredient("raw_materials/iron"),
                item("powah:energized_steel_block")),
                "raw_materials/gold", "raw_materials/iron");
        energizing("blazing_crystal_bee", "powah/blazing_crystal", 9_000_000L, List.of(
                item("powah:blazing_crystal_block"), beeIngredient("powah/energized_steel"),
                beeIngredient("dusts/blazing"), beeIngredient("powah/energized_steel"),
                item("powah:blazing_crystal_block")),
                "dusts/blazing", "powah/energized_steel");
        energizing("niotic_crystal_bee", "powah/niotic_crystal", 9_000_000L, List.of(
                item("powah:niotic_crystal_block"), beeIngredient("powah/blazing_crystal"),
                beeIngredient("gems/diamond"), beeIngredient("powah/blazing_crystal"),
                item("powah:niotic_crystal_block")),
                "gems/diamond", "powah/blazing_crystal");
        energizing("spirited_crystal_bee", "powah/spirited_crystal", 10_000_000L, List.of(
                item("powah:spirited_crystal_block"), beeIngredient("powah/niotic_crystal"),
                beeIngredient("gems/emerald"), beeIngredient("powah/niotic_crystal"),
                item("powah:spirited_crystal_block")),
                "gems/emerald", "powah/niotic_crystal");
        energizing("nitro_crystal_bee", "powah/nitro_crystal", 2_000_000_000L, List.of(
                beeIngredient("dusts/redstone"), beeIngredient("powah/spirited_crystal"),
                item("powah:nitro_crystal_block"), item("powah:nitro_crystal_block"),
                beeIngredient("powah/spirited_crystal"), beeIngredient("powah/blazing_crystal")),
                "dusts/redstone", "powah/spirited_crystal", "powah/blazing_crystal");
    }

    private void energizing(String name, String resultBee, long energy, List<Ingredient> ingredients, String... inputBees) {
        save("powah/" + name, new EnergizingRecipe(beeResult(resultBee), energy, ingredients),
                conditions("powah", resultBee, inputBees));
    }

    // -- occultism ------------------------------------------------------------------------------

    private void occultismSpiritFire() {
        RecipeOutput output = this.output.withConditions(
                new ModLoadedCondition("occultism"), beeExists("raw_materials/silver"), beeExists("occultism/iesnium"));
        SpiritFireRecipeBuilder.spiritFireRecipe(beeIngredient("raw_materials/silver"), beeResult("occultism/iesnium"))
                .unlockedBy("has_silver_bee", InventoryChangeTrigger.TriggerInstance.hasItems(
                        this.items.getOrThrow(itemKey("productivebees:spawn_egg_configurable_bee")).value()))
                .save(output, recipeKey("occultism/iesnium_bee"));
    }

    // -- neovitae (Blood Magic's rebrand) -------------------------------------------------------

    private void neoVitaeAltarAndForge() {
        araVitae("hematophagous_bee", item("productivebees:spawn_egg_nomad_bee"), "neovitae/hematophagous", 1, 5000);
        araVitae("regenerative_bee", beeIngredient("neovitae/hematophagous"), "neovitae/regenerative", 3, 10000,
                beeExists("neovitae/hematophagous"));

        Ingredient parts = item("neovitae:hellforged_parts");
        save("neovitae/hellfire_forge/hellfire_bee",
                new ForgeRecipe(1200.0, 100.0,
                        List.of(beeIngredient("neovitae/regenerative"), parts, parts, parts),
                        beeResult("neovitae/hellfire"), Optional.empty()),
                new ModLoadedCondition("neovitae"),
                beeExists("neovitae/regenerative"), beeExists("neovitae/hellfire"));
    }

    private void araVitae(String name, Ingredient input, String resultBee, int minTier, int bloodNeeded, ICondition... extra) {
        ICondition[] conditions = new ICondition[extra.length + 2];
        conditions[0] = new ModLoadedCondition("neovitae");
        conditions[1] = beeExists(resultBee);
        System.arraycopy(extra, 0, conditions, 2, extra.length);
        save("neovitae/" + name,
                new AraVitaeRecipe(input, beeResult(resultBee), minTier, bloodNeeded, 30, 50),
                conditions);
    }

    // -- ftbic ----------------------------------------------------------------------------------

    private void ftbicCanning() {
        save("ftbic/canning/iridium_bee",
                new MachineRecipe(FTBICRecipes.CANNING,
                        List.of(new IngredientWithCount(beeIngredient("gems/crystalline"), 1),
                                new IngredientWithCount(tag("c:raw_materials/iridium"), 1)),
                        List.of(),
                        List.of(new StackWithChance(beeResult("raw_materials/iridium"), 1.0)),
                        List.of(), 1.0, false),
                new ModLoadedCondition("ftbic"),
                new NotCondition(new TagEmptyCondition<>(itemTag("c:raw_materials/iridium"))),
                beeExists("gems/crystalline"), beeExists("raw_materials/iridium"));
    }

    // -- evilcraft ------------------------------------------------------------------------------

    private void evilcraftBloody() {
        // Kept disabled exactly as the hand-written recipe was, by a condition that never passes.
        save("evilcraft/bloody_bee",
                new RecipeBloodInfuser(
                        Optional.of(item("productivebees:spawn_egg_nomad_bee")),
                        Optional.of(new FluidStackTemplate(this.fluids.getOrThrow(fluidKey("evilcraft:blood")), 10000)),
                        Optional.empty(),
                        Either.left(beeResult("evilcraft/bloody")),
                        1000,
                        Optional.empty()),
                new ModLoadedCondition("evilcraft"), beeExists("evilcraft/bloody"),
                new NotCondition(new ModLoadedCondition(ProductiveBees.MODID)));

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, beeResult("evilcraft/bloody"))
                .pattern("BBB").pattern("BEB").pattern("BBB")
                .define('E', item("productivebees:spawn_egg_nomad_bee"))
                .define('B', item("evilcraft:blood_orb_filled"))
                .unlockedBy("has_nomad_bee", InventoryChangeTrigger.TriggerInstance.hasItems(
                        this.items.getOrThrow(itemKey("productivebees:spawn_egg_nomad_bee")).value()))
                .save(this.output.withConditions(new ModLoadedCondition("evilcraft"), beeExists("evilcraft/bloody")),
                        recipeKey("evilcraft/bloody_bee_2"));
    }

    // -- productivemetalworks -------------------------------------------------------------------

    private void productiveMetalworksCasting() {
        RecipeOutput output = this.output.withConditions(
                new ModLoadedCondition("productivemetalworks"), beeExists("productivemetalworks/butcher"));
        ItemCastingRecipeBuilder.of(
                        item("productivebees:spawn_egg_bumble_bee"),
                        fluidTagIngredient("c:meat", 1000),
                        beeResult("butcher"),
                        true)
                .save(output, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "productivemetalworks/butcher_bee"));
    }

    // -- plumbing -------------------------------------------------------------------------------

    private void save(String path, Recipe<?> recipe, ICondition... conditions) {
        this.output.withConditions(conditions).accept(recipeKey(path), recipe, null);
    }

    private ICondition[] conditions(String modid, String resultBee, String... inputBees) {
        ICondition[] out = new ICondition[inputBees.length + 2];
        out[0] = new ModLoadedCondition(modid);
        out[1] = beeExists(resultBee);
        for (int i = 0; i < inputBees.length; i++) {
            out[i + 2] = beeExists(inputBees[i]);
        }
        return out;
    }

    private Ingredient item(String id) {
        return Ingredient.of(this.items.getOrThrow(itemKey(id)).value());
    }

    private Ingredient tag(String id) {
        return Ingredient.of(this.items.getOrThrow(itemTag(id)));
    }

    private static TagKey<Item> itemTag(String id) {
        return TagKey.create(Registries.ITEM, Identifier.parse(id));
    }

    /** 26.1 dropped SizedFluidIngredient.of(TagKey), so build the tag-backed ingredient from the lookup. */
    private SizedFluidIngredient fluidTagIngredient(String id, int amount) {
        return new SizedFluidIngredient(FluidIngredient.of(this.fluids.getOrThrow(TagKey.create(Registries.FLUID, Identifier.parse(id)))), amount);
    }

    private static ResourceKey<Item> itemKey(String id) {
        return ResourceKey.create(Registries.ITEM, Identifier.parse(id));
    }

    private static ResourceKey<Fluid> fluidKey(String id) {
        return ResourceKey.create(Registries.FLUID, Identifier.parse(id));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, path));
    }

    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new CompatRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "PB Compat Machine Recipes";
        }
    }
}
