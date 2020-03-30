package cy.jdkdigital.productivebees.entity.bee;

import com.electronwill.nightconfig.core.Config;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductiveBeeEntity extends BeeEntity implements IBeeEntity {
	public Tag<Block> nestBlockTag;

	protected Predicate<PointOfInterestType> isInterestedIn = (poiType) -> poiType == PointOfInterestType.field_226356_s_ || poiType == PointOfInterestType.field_226357_t_;

	public ProductiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = BlockTags.BEEHIVES;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.399999976158142D, true));
		this.goalSelector.addGoal(1, new ProductiveBeeEntity.EnterBeehiveGoal());
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));

		// Item to make entity follow player
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.fromTag(ItemTags.FLOWERS), false));

		this.pollinateGoal = new ProductiveBeeEntity.PollinateGoal();
		this.pollinateGoal.field_226492_c_ = this.pollinatePredicate();
		this.goalSelector.addGoal(4, this.pollinateGoal);
		this.goalSelector.addGoal(5, new ProductiveBeeEntity.UpdateNestGoal());

		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));

//		this.goalSelector.addGoal(5, new BeeEntity.UpdateBeehiveGoal());

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

	public String getBeeType() {
		return this.getEntityString().split("[:_]")[1];
	}

	public boolean isAngry() {
		return super.func_226427_ez_();
	}

	public boolean hasNectar() {
		return super.func_226411_eD_();
	}

	public boolean hasHivePos() {
		return super.func_226409_eA_();
	}

	public boolean failedToFindNectar() {
		return super.func_226414_eH_();
	}

	public boolean hasStung() {
		return super.func_226412_eE_();
	}

	public boolean isBreedingItem(ItemStack itemStack) {
		return
			(itemStack.getItem() == Items.REDSTONE && this.getBeeType().equals("redstone")) ||
			(itemStack.getItem() == Items.DIAMOND && this.getBeeType().equals("diamond")) ||
			(itemStack.getItem() == Items.LAPIS_LAZULI && this.getBeeType().equals("lapis")) ||
			(itemStack.getItem() == Items.EMERALD && this.getBeeType().equals("emerald")) ||
			(itemStack.getItem() == Items.GOLD_INGOT && this.getBeeType().equals("gold")) ||
			(itemStack.getItem() == Items.IRON_INGOT && this.getBeeType().equals("iron")) ||
			(itemStack.getItem() == Items.GUNPOWDER && this.getBeeType().equals("creeper"))
		;
	}

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

	public Config getProductionList() {
		return getProductionList(this.getEntityString());
	}

	public static Config getProductionList(String beeId) {
		return ProductiveBeesConfig.BEES.itemProductionRules.get().get(beeId);
	}

	protected Predicate<BlockState> pollinatePredicate() {
		Predicate<BlockState> predicate = (blockState) -> {
			if (blockState.isIn(BlockTags.TALL_FLOWERS)) {
				if (blockState.getBlock() == Blocks.SUNFLOWER) {
					return blockState.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
				} else {
					return true;
				}
			} else {
				return blockState.isIn(BlockTags.SMALL_FLOWERS);
			}
		};
		return predicate;
	}

	public boolean tileAtPosHasRoom(BlockPos pos) {
		TileEntity tileEntity = this.world.getTileEntity(pos);
		if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
			return !((AdvancedBeehiveTileEntityAbstract)tileEntity).isHiveFull();
		} else {
			return false;
		}
	}

	public boolean func_226415_eI_() {
		if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.func_226503_k_() && !this.hasStung()) {
			boolean needsToReturnHome = this.failedToFindNectar() || this.world.isRaining() || this.world.isNightTime() || this.hasNectar();
			return needsToReturnHome && !this.hasHome();
		} else {
			return false;
		}
	}
	private boolean hasHome() {
		if (this.hivePos == null) {
			return false;
		} else {
			TileEntity tileEntity = this.world.getTileEntity(this.hivePos);
			return  tileEntity instanceof BeehiveTileEntity && ((BeehiveTileEntity)tileEntity).func_226968_d_();
		}
	}

	abstract class PassiveGoal extends Goal {
		private PassiveGoal() {
		}

		public abstract boolean func_225506_g_();

		public abstract boolean func_225507_h_();

		public boolean shouldExecute() {
			return this.func_225506_g_() && !ProductiveBeeEntity.this.isAngry();
		}

		public boolean shouldContinueExecuting() {
			return this.func_225507_h_() && !ProductiveBeeEntity.this.isAngry();
		}
	}

	public class PollinateGoal extends BeeEntity.PollinateGoal {
		public PollinateGoal() {
			super();
		}

		public boolean func_225506_g_() {
			if (ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0) {
				return false;
			} else if (ProductiveBeeEntity.this.func_226411_eD_()) {
				return false;
			} else if (ProductiveBeeEntity.this.world.isRaining()) {
				return false;
			} else if (ProductiveBeeEntity.this.rand.nextFloat() < 0.7F) {
				return false;
			} else {
				Optional<BlockPos> optional = this.func_226507_o_();
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

		public boolean func_225506_g_() {
			boolean hasHivePos = ProductiveBeeEntity.this.hasHivePos();
			if (!hasHivePos) {
				return false;
			}

			return  !ProductiveBeeEntity.this.detachHome() &&
					ProductiveBeeEntity.this.func_226415_eI_() &&
					!this.isAtNest(ProductiveBeeEntity.this.hivePos) &&
					ProductiveBeeEntity.this.world.getBlockState(ProductiveBeeEntity.this.hivePos).isIn(ProductiveBeeEntity.this.nestBlockTag);
		}

		private boolean isAtNest(BlockPos pos) {
			if (ProductiveBeeEntity.this.func_226401_b_(pos, 2)) {
				return true;
			} else {
				Path path = ProductiveBeeEntity.this.navigator.getPath();
				return path != null && path.func_224770_k().equals(pos) && path.func_224771_h() && path.isFinished();
			}
		}

		protected void func_226475_c_(BlockPos pos) {
			this.field_226469_d_.add(pos);

			TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(pos);
			int maxBees = 3;
			if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
				maxBees = ((AdvancedBeehiveTileEntityAbstract) tileEntity).MAX_BEES;
			}
			while(this.field_226469_d_.size() > maxBees) {
				this.field_226469_d_.remove(0);
			}
		}
	}

	class UpdateNestGoal extends Goal {
		private UpdateNestGoal() {
			super();
		}

		@Override
		public boolean shouldExecute() {
			return this.needsNewHome() && !ProductiveBeeEntity.this.isAngry();
		}

		public boolean shouldContinueExecuting() {
			return false;
		}

		public boolean needsNewHome() {
			return ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewHive == 0 &&
					!ProductiveBeeEntity.this.hasHivePos() &&
					ProductiveBeeEntity.this.func_226415_eI_();
		}

		public void startExecuting() {
			ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 20;
			List<BlockPos> nearbyNests = this.findNearbyNests();
			if (!nearbyNests.isEmpty()) {
				Iterator iterator = nearbyNests.iterator();
				BlockPos blockPos;
				do {
					if (!iterator.hasNext()) {
						ProductiveBeeEntity.this.findBeehiveGoal.func_226477_j_();
						ProductiveBeeEntity.this.hivePos = nearbyNests.get(0);
						return;
					}

					blockPos = (BlockPos)iterator.next();
				} while(ProductiveBeeEntity.this.findBeehiveGoal.func_226473_b_(blockPos));

				ProductiveBeeEntity.this.hivePos = blockPos;
			}
		}

		private List<BlockPos> findNearbyNests() {
			BlockPos pos = new BlockPos(ProductiveBeeEntity.this);

			PointOfInterestManager poiManager = ((ServerWorld)ProductiveBeeEntity.this.world).getPointOfInterestManager();
			Stream<PointOfInterest> stream = poiManager.func_219146_b(ProductiveBeeEntity.this.isInterestedIn, pos, 30, PointOfInterestManager.Status.ANY);

			return stream
					.map(PointOfInterest::getPos)
					.filter(ProductiveBeeEntity.this::tileAtPosHasRoom)
					.sorted(Comparator.comparingDouble((vec) -> vec.distanceSq(pos)))
					.collect(Collectors.toList());
		}
	}

	public class EnterBeehiveGoal extends ProductiveBeeEntity.PassiveGoal {
		public EnterBeehiveGoal() {
			super();
		}

		public boolean func_225506_g_() {
			if (ProductiveBeeEntity.this.hasHivePos() && ProductiveBeeEntity.this.func_226415_eI_() && ProductiveBeeEntity.this.hivePos.withinDistance(ProductiveBeeEntity.this.getPositionVec(), 2.0D)) {
				TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(ProductiveBeeEntity.this.hivePos);
				// Enter vanilla registered hives
				if (tileEntity instanceof BeehiveTileEntity) {
					BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity)tileEntity;
					if (!beehiveTileEntity.func_226970_h_()) {
						return true;
					}
					ProductiveBeeEntity.this.hivePos = null;
				}
				// Enter ProductiveBees hives
				if(tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
					AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract)tileEntity;
					if (!beehiveTileEntity.isHiveFull()) {
						return true;
					}
					ProductiveBeeEntity.this.hivePos = null;
				}
			}

			return false;
		}

		public boolean func_225507_h_() {
			return false;
		}

		public void startExecuting() {
			if (ProductiveBeeEntity.this.hasHivePos()) {
				TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(ProductiveBeeEntity.this.hivePos);
				if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
					AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
					beehiveTileEntity.insertBee(ProductiveBeeEntity.this, ProductiveBeeEntity.this.hasNectar(), 0);
				} else if (tileEntity instanceof BeehiveTileEntity) {
					BeehiveTileEntity beehiveTileEntity = (BeehiveTileEntity) tileEntity;
					beehiveTileEntity.func_226962_a_(ProductiveBeeEntity.this, ProductiveBeeEntity.this.func_226411_eD_(), 0);
				}
			}
		}
	}
}
