package cy.jdkdigital.productivebees.entity.bee;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.Path;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductiveBeeEntity extends BeeEntity implements IBeeEntity {
	protected Map<BeeAttribute<?>, Object> beeAttributes = Maps.newHashMap();
	public Tag<Block> nestBlockTag;

	protected Predicate<PointOfInterestType> beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE ||
			poiType == PointOfInterestType.BEE_NEST ||
			poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
			poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();

	public ProductiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = BlockTags.BEEHIVES;

		beeAttributes.put(BeeAttributes.PRODUCTIVITY, world.rand.nextInt(2));
		beeAttributes.put(BeeAttributes.TEMPER, 1);
		beeAttributes.put(BeeAttributes.BEHAVIOR, 0);
		beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 0);
		beeAttributes.put(BeeAttributes.TYPE, "hive");
		beeAttributes.put(BeeAttributes.FOOD_SOURCE, BlockTags.FLOWERS);
		beeAttributes.put(BeeAttributes.APHRODISIACS, ItemTags.FLOWERS);

		// Goal to make entity follow player, must be registered after init to use bee attributes
		this.goalSelector.addGoal(3, new ProductiveTemptGoal(this, 1.25D));
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.399999976158142D, true));
		// Resting goal!
		this.goalSelector.addGoal(1, new ProductiveBeeEntity.EnterBeehiveGoal());
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));

		this.pollinateGoal = new ProductiveBeeEntity.PollinateGoal();
		this.goalSelector.addGoal(4, this.pollinateGoal);
		this.goalSelector.addGoal(5, new ProductiveBeeEntity.UpdateNestGoal());

		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));

		this.findBeehiveGoal = new ProductiveBeeEntity.FindNestGoal();
		this.goalSelector.addGoal(5, this.findBeehiveGoal);
		this.findFlowerGoal = new BeeEntity.FindFlowerGoal();
		this.goalSelector.addGoal(6, this.findFlowerGoal);
		this.goalSelector.addGoal(7, new BeeEntity.FindPollinationTargetGoal());
		this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
		this.goalSelector.addGoal(9, new SwimGoal(this));

		this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp());
		this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
	}

	public boolean isAngry() {
		return super.isAngry() && getAttributeValue(BeeAttributes.TEMPER) > 0;
	}

	public boolean isFlowers(BlockPos pos) {
		return this.world.isBlockPresent(pos) && this.world.getBlockState(pos).getBlock().isIn(getAttributeValue(BeeAttributes.FOOD_SOURCE));
	}

	public String getBeeType() {
		return this.getEntityString().split("[:_]")[1];
	}

	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack.getItem().isIn(getAttributeValue(BeeAttributes.APHRODISIACS));
	}

	public <T> T getAttributeValue(BeeAttribute<T> parameter) {
		return (T) this.beeAttributes.get(parameter);
	}

	@Override
	public void writeAdditional(CompoundNBT tag) {
		super.writeAdditional(tag);

		tag.putInt("bee_productivity", this.getAttributeValue(BeeAttributes.PRODUCTIVITY));
		tag.putInt("bee_temper", this.getAttributeValue(BeeAttributes.TEMPER));
		tag.putInt("bee_behavior", this.getAttributeValue(BeeAttributes.BEHAVIOR));
		tag.putInt("bee_weather_tolerance", this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE));
		tag.putString("bee_type", this.getAttributeValue(BeeAttributes.TYPE));
		tag.putString("bee_food_source", this.getAttributeValue(BeeAttributes.FOOD_SOURCE).getId().toString());
		tag.putString("bee_aphrodisiac", this.getAttributeValue(BeeAttributes.APHRODISIACS).getId().toString());
	}

	@Override
	public void readAdditional(CompoundNBT tag) {
		super.readAdditional(tag);

		if (tag.contains("bee_productivity")) {
			beeAttributes.clear();
			beeAttributes.put(BeeAttributes.PRODUCTIVITY, tag.getInt("bee_productivity"));
			beeAttributes.put(BeeAttributes.TEMPER, tag.getInt("bee_temper"));
			beeAttributes.put(BeeAttributes.BEHAVIOR, tag.getInt("bee_behavior"));
			beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, tag.getInt("bee_weather_tolerance"));
			beeAttributes.put(BeeAttributes.TYPE, tag.getString("bee_type"));
			beeAttributes.put(BeeAttributes.FOOD_SOURCE, BlockTags.getCollection().getOrCreate(new ResourceLocation(tag.getString("bee_food_source"))));
			beeAttributes.put(BeeAttributes.APHRODISIACS, ItemTags.getCollection().getOrCreate(new ResourceLocation(tag.getString("bee_aphrodisiac"))));
		}
	}

	//	@Override
//	public boolean processInteract(PlayerEntity player, Hand hand) {
//		ItemStack itemStack = player.getHeldItem(hand);
//		return super.processInteract(player, hand);
//	}

	@Override
	public BeeEntity createChild(AgeableEntity targetEntity) {
		return (BeeEntity) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(ProductiveBees.MODID, this.getBeeType() + "_bee")).create(world);
	}

	@Override
	public boolean canMateWith(AnimalEntity nearbyEntity) {
		if (nearbyEntity == this) {
			return false;
		} else if (!(nearbyEntity instanceof BeeEntity)) {
			return false;
		} else {
			// Check specific breeding rules


			return this.isInLove() && nearbyEntity.isInLove();
		}
	}

	public static Double getProductionRate(String beeId) {
		return ProductiveBeesConfig.BEES.itemProductionRates.get().get(beeId);
	}

	public static LootTable getProductionLootTable(World world, String beeId) {
		ResourceLocation beeRes = new ResourceLocation(beeId);
		ResourceLocation resourcelocation = new ResourceLocation(beeRes.getNamespace(), "entities/" + beeRes.getPath());
		return world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
	}

	public boolean tileAtPosHasRoom(BlockPos pos) {
		TileEntity tileEntity = this.world.getTileEntity(pos);
		if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
			return !((AdvancedBeehiveTileEntityAbstract)tileEntity).isFullOfBees();
		}
		return false;
	}

	public class PollinateGoal extends BeeEntity.PollinateGoal {
		public PollinateGoal() {
			super();
			this.flowerPredicate = (blockState) -> {
				Tag<Block> interests = ProductiveBeeEntity.this.getAttributeValue(BeeAttributes.FOOD_SOURCE);
				if (blockState.isIn(interests) && blockState.isIn(BlockTags.TALL_FLOWERS)) {
					if (blockState.getBlock() == Blocks.SUNFLOWER) {
						return blockState.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
					} else {
						return true;
					}
				}
				return blockState.isIn(interests);
			};
		}

		public boolean canBeeStart() {
			if (ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0) {
				return false;
			} else if (ProductiveBeeEntity.this.hasNectar()) {
				return false;
			} else if (ProductiveBeeEntity.this.world.isRaining() && ProductiveBeeEntity.this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) == 0) {
				return false;
			} else if (ProductiveBeeEntity.this.world.isThundering() && ProductiveBeeEntity.this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) < 2) {
				return false;
			} else if (ProductiveBeeEntity.this.rand.nextFloat() < 0.7F) {
				return false;
			} else {
				Optional<BlockPos> optional = this.getFlower();
				if (optional.isPresent()) {
					ProductiveBeeEntity.this.savedFlowerPos = optional.get();
					return ProductiveBeeEntity.this.navigator.tryMoveToXYZ((double)ProductiveBeeEntity.this.savedFlowerPos.getX() + 0.5D, (double)ProductiveBeeEntity.this.savedFlowerPos.getY() + 0.5D, (double)ProductiveBeeEntity.this.savedFlowerPos.getZ() + 0.5D, 1.2F);
				} else {
					return false;
				}
			}
		}
	}

	public class FindNestGoal extends BeeEntity.FindBeehiveGoal {
		public FindNestGoal() {
			super();
		}

		public boolean canBeeStart() {
			boolean hasHivePos = ProductiveBeeEntity.this.hasHive();
			if (!hasHivePos) {
				return false;
			}

			return  !ProductiveBeeEntity.this.detachHome() &&
					ProductiveBeeEntity.this.canEnterHive() &&
					!this.isCloseEnough(ProductiveBeeEntity.this.hivePos) &&
					ProductiveBeeEntity.this.world.getBlockState(ProductiveBeeEntity.this.hivePos).isIn(ProductiveBeeEntity.this.nestBlockTag);
		}

		private boolean isCloseEnough(BlockPos pos) {
			if (ProductiveBeeEntity.this.isWithinDistance(pos, 2)) {
				return true;
			} else {
				Path path = ProductiveBeeEntity.this.navigator.getPath();
				return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
			}
		}

		protected void addPossibleHives(BlockPos pos) {
			this.possibleHives.add(pos);

			TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(pos);
			int maxBees = 3;
			if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
				maxBees = ((AdvancedBeehiveTileEntityAbstract) tileEntity).MAX_BEES;
			}
			while(this.possibleHives.size() > maxBees) {
				this.possibleHives.remove(0);
			}
		}
	}

	class UpdateNestGoal extends BeeEntity.UpdateBeehiveGoal {
		private UpdateNestGoal() {
			super();
		}

		public void startExecuting() {
			ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 20;
			List<BlockPos> nearbyNests = this.getNearbyFreeNests();

			if (!nearbyNests.isEmpty()) {
				Iterator iterator = nearbyNests.iterator();
				BlockPos blockPos;
				do {
					if (!iterator.hasNext()) {
						ProductiveBeeEntity.this.findBeehiveGoal.clearPossibleHives();
						ProductiveBeeEntity.this.hivePos = nearbyNests.get(0);
						return;
					}

					blockPos = (BlockPos)iterator.next();
				} while(ProductiveBeeEntity.this.findBeehiveGoal.isPossibleHive(blockPos));

				ProductiveBeeEntity.this.hivePos = blockPos;
			}
		}

		private List<BlockPos> getNearbyFreeNests() {
			BlockPos pos = new BlockPos(ProductiveBeeEntity.this);

			PointOfInterestManager poiManager = ((ServerWorld)ProductiveBeeEntity.this.world).getPointOfInterestManager();
			Stream<PointOfInterest> stream = poiManager.func_219146_b(ProductiveBeeEntity.this.beehiveInterests, pos, 30, PointOfInterestManager.Status.ANY);

			return stream
					.map(PointOfInterest::getPos)
					.filter(ProductiveBeeEntity.this::tileAtPosHasRoom)
					.sorted(Comparator.comparingDouble((vec) -> vec.distanceSq(pos)))
					.collect(Collectors.toList());
		}
	}

	public class EnterBeehiveGoal extends BeeEntity.EnterBeehiveGoal {
		public EnterBeehiveGoal() {
			super();
		}

		public boolean canBeeStart() {
			if (ProductiveBeeEntity.this.hasHive() && ProductiveBeeEntity.this.canEnterHive() && ProductiveBeeEntity.this.hivePos.withinDistance(ProductiveBeeEntity.this.getPositionVec(), 2.0D)) {
				TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(ProductiveBeeEntity.this.getHivePos());
				// Enter ProductiveBees hives
				if(tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
					AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract)tileEntity;
					if (!beehiveTileEntity.isFullOfBees()) {
						return true;
					}
					// Hive is full, reset and look for another spot
					ProductiveBeeEntity.this.hivePos = null;
				}
				// Enter vanilla registered hives
				else if (tileEntity instanceof BeehiveTileEntity) {
					BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity)tileEntity;
					if (!beehiveTileEntity.isFullOfBees()) {
						return true;
					}
					ProductiveBeeEntity.this.hivePos = null;
				}
			}

			return false;
		}

		public void startExecuting() {
			if (ProductiveBeeEntity.this.hasHive()) {
				TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(ProductiveBeeEntity.this.getHivePos());
				if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
					AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
					beehiveTileEntity.tryEnterHive(ProductiveBeeEntity.this, ProductiveBeeEntity.this.hasNectar(), 0);
				} else if (tileEntity instanceof BeehiveTileEntity) {
					BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity) tileEntity;
					beehiveTileEntity.tryEnterHive(ProductiveBeeEntity.this, ProductiveBeeEntity.this.hasNectar(), 0);
				}
			}
		}
	}

	public class ProductiveTemptGoal extends TemptGoal {
		public ProductiveTemptGoal(CreatureEntity entity, double speed) {
			super(entity, speed, false, Ingredient.fromTag(getAttributeValue(BeeAttributes.APHRODISIACS)));
		}
	}
}
