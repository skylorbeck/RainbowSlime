package crimsonfluff.crimsonslimes.items;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrimsonSpawnEggItem extends net.minecraft.world.item.SpawnEggItem {
    protected static final List<CrimsonSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Lazy<? extends EntityType<?>> entityTypeSupplier;

    public CrimsonSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int primaryColorIn, final int secondaryColorIn, final Item.Properties properties) {
        super(null, primaryColorIn, secondaryColorIn, properties);
        this.entityTypeSupplier = Lazy.of(entityTypeSupplier::get);
        UNADDED_EGGS.add(this);
    }

    public static void init() {
        final Map<EntityType<?>, net.minecraft.world.item.SpawnEggItem> EGGS = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.world.item.SpawnEggItem.class,null, "f_43201_");
        DefaultDispenseItemBehavior dispenseBehaviour = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                EntityType<?> type = ((net.minecraft.world.item.SpawnEggItem) stack.getItem()).getType(stack.getTag());

                type.spawn(source.getLevel(), stack, null, source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
                stack.shrink(1);

                return stack;
            }
        };

        for (final net.minecraft.world.item.SpawnEggItem spawnEgg : UNADDED_EGGS) {
            EGGS.put(spawnEgg.getType(null), spawnEgg);
            DispenserBlock.registerBehavior(spawnEgg, dispenseBehaviour);
        }
        UNADDED_EGGS.clear();
    }

    @Override
    public EntityType<?> getType(CompoundTag nbt) {
        return this.entityTypeSupplier.get();
    }
}
