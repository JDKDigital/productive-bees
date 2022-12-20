package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SpawnEgg extends ForgeSpawnEggItem
{
    public SpawnEgg(Supplier<EntityType<? extends Mob>> entityType, int primaryColor, int secondaryColor, Item.Properties properties) {
        super(entityType, primaryColor, secondaryColor, properties);
    }

    @Nonnull
    @Override
    public EntityType<?> getType(CompoundTag compound) {
        if (compound != null && compound.contains("EntityTag", 10)) {
            CompoundTag entityTag = compound.getCompound("EntityTag");

            if (entityTag.contains("id", 8)) {
                return EntityType.byString(entityTag.getString("id")).orElse(getDefaultType());
            }
        }
        return getDefaultType();
    }

    @Override
    public Optional<Mob> spawnOffspringFromSpawnEgg(Player pPlayer, Mob pMob, EntityType<? extends Mob> pEntityType, ServerLevel pServerLevel, Vec3 pPos, ItemStack pStack) {
        Optional<Mob> result = super.spawnOffspringFromSpawnEgg(pPlayer, pMob, pEntityType, pServerLevel, pPos, pStack);

        if (result.isEmpty() && pMob instanceof ConfigurableBee) {
            EntityType<?> entityType = this.getType(pStack.getTag());
            return Optional.of((Mob) entityType.create(pServerLevel));
        }

        return result;
    }

    public int getColor(int tintIndex, ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return tintIndex == 0 ? nbt.getInt("primaryColor") : nbt.getInt("secondaryColor");
            }
        }
        return super.getColor(tintIndex);
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                String name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(tag.getString("type")) + "_bee").getString();
                return Component.translatable("item.productivebees.spawn_egg_configurable", name);
            }
        }
        return super.getName(stack);
    }
}
