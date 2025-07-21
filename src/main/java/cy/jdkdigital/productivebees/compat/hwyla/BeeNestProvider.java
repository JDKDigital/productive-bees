package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeNestProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    static final BeeNestProvider INSTANCE = new BeeNestProvider();
    public static final ResourceLocation UID = new ResourceLocation(ProductiveBees.MODID, "bee_nest");


    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        var be = blockAccessor.getBlockEntity();
        if (be == null) return;
        if (be instanceof AdvancedBeehiveBlockEntityAbstract tileEntity) {
            tileEntity.loadPacketNBT(blockAccessor.getServerData());
            List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> bees = tileEntity.getBeeList();


            Map<String, Integer> beeCounts = new HashMap<>();
            Map<String, IElement> beeElements = new HashMap<>();

            if (!bees.isEmpty()) {

                for (AdvancedBeehiveBlockEntityAbstract.Inhabitant bee : bees) {
                    if (beeCounts.containsKey(bee.localizedName)) {
                        beeCounts.put(bee.localizedName, beeCounts.get(bee.localizedName) + 1);
                    } else {
                        beeCounts.put(bee.localizedName, 1);
                    }

                    if (!beeElements.containsKey(bee.localizedName)) {
                        CompoundTag entityData = bee.nbt.copy();
                        String type = entityData.getString("type");
                        if (type.isEmpty()) {
                            type = entityData.getString("id");
                        }
                        BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(type).get();
                        Entity beeEntity = beeIngredient.getCachedEntity(be.getLevel());
                        float scaledSize = 18.0F;
                        float offset = -3.0F;
                        if (beeEntity instanceof ProductiveBee pBee) {
                            if (pBee.getSizeModifier() >= 4.0F) {
                                offset = -1.0F;
                            } else {
                                scaledSize = scaledSize * pBee.getSizeModifier();
                                offset = offset - 18.0F / scaledSize;
                            }
                        }
                        IElement beeIcon = BeeEntityElement.of(beeIngredient, scaledSize / 18.0F).size(new Vec2(16, 16))
                                .translate(new Vec2(0, offset));
                        beeElements.put(bee.localizedName, beeIcon);
                    }
                }
            }
            if (!beeCounts.isEmpty()) {
                beeCounts.forEach((name, count) -> {
                    tooltip.add(beeElements.get(name));
                    MutableComponent text = Component.literal(count.toString())
                            .append(Component.literal("x "));
                    tooltip.append(text.append(Component.translatable(name)));
                });
            }
        }


    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be != null) {
            var cap = be.getCapability(CapabilityBee.BEE).resolve().orElse(null);
            if (cap != null && cap.countInhabitants() > 0 && be instanceof AdvancedBeehiveBlockEntityAbstract nest) {
                nest.savePacketNBT(compoundTag);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
