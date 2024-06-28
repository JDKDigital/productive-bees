package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Supplier;

public class SpawnEgg extends DeferredSpawnEggItem
{
    public SpawnEgg(Supplier<EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor, Item.Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Override
    public Optional<Mob> spawnOffspringFromSpawnEgg(Player pPlayer, Mob pMob, EntityType<? extends Mob> pEntityType, ServerLevel pServerLevel, Vec3 pPos, ItemStack pStack) {
        // Called when spawn egg is used on mob
        Optional<Mob> result = super.spawnOffspringFromSpawnEgg(pPlayer, pMob, pEntityType, pServerLevel, pPos, pStack);

        if (result.isPresent() && pStack.has(ModDataComponents.BEE_TYPE) && result.get() instanceof ConfigurableBee cBee) {
            cBee.setBeeType(pStack.get(ModDataComponents.BEE_TYPE).toString());
            return Optional.of(cBee);
        }

        return result;
    }

    public int getColor(int tintIndex, ItemStack stack) {
        var data = stack.get(DataComponents.ENTITY_DATA);
        if (data != null) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(ResourceLocation.parse(data.getUnsafe().getString("type")));
            if (nbt != null) {
                return tintIndex == 0 ? nbt.getInt("primaryColor") : nbt.getInt("secondaryColor");
            }
        }
        return super.getColor(tintIndex);
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        var data = stack.get(DataComponents.ENTITY_DATA);
        if (data != null) {
            var beeType = ResourceLocation.parse(data.getUnsafe().getString("type"));
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(beeType);
            if (nbt != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeType) + "_bee").getString();
                return Component.translatable("item.productivebees.spawn_egg_configurable", name);
            }
        }
        return super.getName(stack);
    }
}
