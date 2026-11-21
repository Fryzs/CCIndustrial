package com.ccindustrial.ic2;

import com.ccindustrial.CCIndustrial;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.tile.IEnergyStorage;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Headless checks for CC: Industrial ({@code ./gradlew runGameTestServer}): each
 * test places an IC2 block, asks CC: Tweaked for the peripheral exactly as a computer would, and
 * checks its type, its functions and a few values. Only run when game tests are enabled.
 */
@GameTestHolder(CCIndustrial.MOD_ID)
@PrefixGameTestTemplate(false)
public final class Ic2ComputerGameTests {
    private static final BlockPos AT = new BlockPos(2, 1, 2);

    @GameTest(template = "empty")
    public static void storageIsPeripheral(GameTestHelper helper) {
        BlockEntity be = place(helper, "ic2:mfe", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        expectType(helper, peripheral, "ic2_storage");
        expectMethods(helper, peripheral, "getStored", "getCapacity", "getOutput", "getEU", "getEUCapacity");
        helper.assertTrue(be instanceof IEnergyStorage, "MFE should be an IEnergyStorage");
        int capacity = new Ic2StorageMethods().getCapacity((IEnergyStorage) be);
        helper.assertTrue(capacity > 0, "MFE capacity should be positive, was " + capacity);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void machineIsPeripheral(GameTestHelper helper) {
        BlockEntity be = place(helper, "ic2:macerator", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        expectType(helper, peripheral, "ic2_machine");
        expectMethods(helper, peripheral, "getProgress", "getOperationLength", "getEnergyPerTick", "getEU", "isActive");
        helper.assertTrue(be instanceof TileEntityStandardMachine<?, ?, ?>, "macerator should be a standard machine");
        double progress = new Ic2MachineMethods().getProgress((TileEntityStandardMachine<?, ?, ?>) be);
        helper.assertTrue(progress == 0.0, "idle macerator progress should be 0, was " + progress);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void generatorIsPeripheral(GameTestHelper helper) {
        BlockEntity be = place(helper, "ic2:generator", AT);
        IPeripheral peripheral = peripheralAt(helper, AT);
        expectType(helper, peripheral, "ic2_generator");
        expectMethods(helper, peripheral, "getFuel", "isGenerating", "getEU");
        int fuel = new Ic2GeneratorMethods().getFuel((TileEntityBaseGenerator) be);
        helper.assertTrue(fuel == 0, "new generator should have no fuel, was " + fuel);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void reactorAndChamberArePeripherals(GameTestHelper helper) {
        BlockEntity core = place(helper, "ic2:nuclear_reactor", AT);
        BlockPos chamberPos = AT.east();
        BlockEntity chamber = place(helper, "ic2:reactor_chamber", chamberPos);

        IPeripheral reactorPeripheral = peripheralAt(helper, AT);
        expectType(helper, reactorPeripheral, "ic2_reactor");
        expectMethods(helper, reactorPeripheral, "getHeat", "getMaxHeat", "getHeatFill", "getEUOutput",
                "isRunning", "getComponents");

        helper.assertTrue(core instanceof IReactor, "nuclear_reactor should be an IReactor");
        IReactor reactor = (IReactor) core;
        Ic2ReactorMethods methods = new Ic2ReactorMethods();
        helper.assertTrue(methods.getHeat(reactor) == 0, "a new reactor should be cold");
        helper.assertTrue(methods.getMaxHeat(reactor) > 0, "max heat should be positive");
        helper.assertTrue(!methods.isRunning(reactor), "no redstone, so it should not be running");
        helper.assertTrue(methods.getComponents(reactor).isEmpty(), "a new reactor has no components");

        // The chamber answers for the same reactor once IC2 has linked it (after a tick or two).
        helper.runAfterDelay(5, () -> {
            IPeripheral chamberPeripheral = peripheralAt(helper, chamberPos);
            expectType(helper, chamberPeripheral, "ic2_reactor");
            helper.assertTrue(chamber instanceof IReactorChamber, "reactor_chamber should be an IReactorChamber");
            try {
                int viaChamber = methods.chamberMaxHeat((IReactorChamber) chamber);
                helper.assertTrue(viaChamber == methods.getMaxHeat(reactor),
                        "chamber should report the reactor's max heat, got " + viaChamber);
            } catch (Exception e) {
                helper.fail("chamber lookup failed: " + e.getMessage());
            }
            helper.succeed();
        });
    }

    // --- helpers ---------------------------------------------------------------------------

    private static BlockEntity place(GameTestHelper helper, String blockId, BlockPos pos) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        helper.assertTrue(block != null && BuiltInRegistries.BLOCK.getKey(block).toString().equals(blockId),
                "block " + blockId + " is not registered");
        helper.setBlock(pos, block);
        BlockEntity be = helper.getBlockEntity(pos);
        helper.assertTrue(be != null, blockId + " has no block entity");
        return be;
    }

    /** The peripheral a computer would see on this block: CC's own generic peripheral lookup. */
    private static IPeripheral peripheralAt(GameTestHelper helper, BlockPos relative) {
        BlockPos pos = helper.absolutePos(relative);
        try {
            Class<?> peripherals = Class.forName("dan200.computercraft.impl.Peripherals");
            Method lookup = peripherals.getMethod("getGenericPeripheral",
                    net.minecraft.server.level.ServerLevel.class, BlockPos.class, Direction.class, BlockEntity.class);
            IPeripheral peripheral = (IPeripheral) lookup.invoke(null, helper.getLevel(), pos, Direction.UP,
                    helper.getLevel().getBlockEntity(pos));
            helper.assertTrue(peripheral != null, "CC: Tweaked sees no peripheral at " + relative);
            return peripheral;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach CC: Tweaked's peripheral lookup", e);
        }
    }

    private static void expectType(GameTestHelper helper, IPeripheral peripheral, String type) {
        helper.assertTrue(type.equals(peripheral.getType()),
                "expected peripheral type " + type + ", got " + peripheral.getType()
                        + " (extra types " + peripheral.getAdditionalTypes() + ")");
    }

    private static void expectMethods(GameTestHelper helper, IPeripheral peripheral, String... names) {
        helper.assertTrue(peripheral instanceof IDynamicPeripheral, "not a dynamic peripheral");
        List<String> methods = Arrays.asList(((IDynamicPeripheral) peripheral).getMethodNames());
        for (String name : names) {
            helper.assertTrue(methods.contains(name), "missing Lua function " + name + "; has " + methods);
        }
    }
}
