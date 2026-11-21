package com.ccindustrial.ic2;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import ic2.api.reactor.IReactorComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * The nuclear reactor (type {@code ic2_reactor}), reachable from the reactor itself and from any
 * reactor chamber attached to it. Read-only: turn the reactor on and off with redstone, which
 * the computer can output itself ({@code redstone.setOutput}).
 *
 * <pre>{@code
 * local r = peripheral.find("ic2_reactor")
 * if r.getHeatFill() > 0.5 then redstone.setOutput("back", false) end -- SCRAM
 * }</pre>
 */
public final class Ic2ReactorMethods implements GenericPeripheral {
    /** IC2 reactors are always 6 rows tall; the width grows with chambers (3 to 9). */
    private static final int ROWS = 6;
    private static final int MAX_COLUMNS = 9;

    @Override
    public String id() {
        return "ccindustrial:ic2_reactor";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("ic2_reactor");
    }

    // --- on the reactor block -------------------------------------------------------------

    /** Hull heat. */
    @LuaFunction(mainThread = true)
    public final int getHeat(IReactor reactor) {
        return reactor.getHeat();
    }

    /** Heat at which the reactor explodes. */
    @LuaFunction(mainThread = true)
    public final int getMaxHeat(IReactor reactor) {
        return reactor.getMaxHeat();
    }

    /** Hull heat as a fraction of the maximum, 0 to 1. */
    @LuaFunction(mainThread = true)
    public final double getHeatFill(IReactor reactor) {
        return heatFill(reactor);
    }

    /** EU per tick the reactor is producing. */
    @LuaFunction(mainThread = true)
    public final double getEUOutput(IReactor reactor) {
        return reactor.getReactorEUEnergyOutput();
    }

    /** Whether the reactor is running (it has a redstone signal). */
    @LuaFunction(mainThread = true)
    public final boolean isRunning(IReactor reactor) {
        return reactor.produceEnergy();
    }

    /** Whether it is a fluid-cooled reactor (built with a reactor vessel). */
    @LuaFunction(mainThread = true)
    public final boolean isFluidCooled(IReactor reactor) {
        return reactor.isFluidCooled();
    }

    /**
     * Every component in the grid: a list of {@code {x, y, name, count, damage, maxDamage,
     * heat, maxHeat}}, x and y counted from 1. Empty slots are left out.
     */
    @LuaFunction(mainThread = true)
    public final List<Map<String, Object>> getComponents(IReactor reactor) {
        return components(reactor);
    }

    // --- the same functions on a reactor chamber ------------------------------------------

    @LuaFunction(value = "getHeat", mainThread = true)
    public final int chamberHeat(IReactorChamber chamber) throws LuaException {
        return reactor(chamber).getHeat();
    }

    @LuaFunction(value = "getMaxHeat", mainThread = true)
    public final int chamberMaxHeat(IReactorChamber chamber) throws LuaException {
        return reactor(chamber).getMaxHeat();
    }

    @LuaFunction(value = "getHeatFill", mainThread = true)
    public final double chamberHeatFill(IReactorChamber chamber) throws LuaException {
        return heatFill(reactor(chamber));
    }

    @LuaFunction(value = "getEUOutput", mainThread = true)
    public final double chamberEUOutput(IReactorChamber chamber) throws LuaException {
        return reactor(chamber).getReactorEUEnergyOutput();
    }

    @LuaFunction(value = "isRunning", mainThread = true)
    public final boolean chamberRunning(IReactorChamber chamber) throws LuaException {
        return reactor(chamber).produceEnergy();
    }

    @LuaFunction(value = "isFluidCooled", mainThread = true)
    public final boolean chamberFluidCooled(IReactorChamber chamber) throws LuaException {
        return reactor(chamber).isFluidCooled();
    }

    @LuaFunction(value = "getComponents", mainThread = true)
    public final List<Map<String, Object>> chamberComponents(IReactorChamber chamber) throws LuaException {
        return components(reactor(chamber));
    }

    // --- helpers ---------------------------------------------------------------------------

    private static IReactor reactor(IReactorChamber chamber) throws LuaException {
        IReactor reactor = chamber.getReactorInstance();
        if (reactor == null) {
            throw new LuaException("this reactor chamber is not attached to a reactor");
        }
        return reactor;
    }

    static double heatFill(IReactor reactor) {
        int max = reactor.getMaxHeat();
        return max <= 0 ? 0.0 : (double) reactor.getHeat() / max;
    }

    static List<Map<String, Object>> components(IReactor reactor) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < MAX_COLUMNS; x++) {
                ItemStack stack = reactor.getItemAt(x, y);
                if (stack == null || stack.isEmpty()) {
                    continue;
                }
                Map<String, Object> slot = new HashMap<>();
                slot.put("x", x + 1);
                slot.put("y", y + 1);
                slot.put("name", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                slot.put("count", stack.getCount());
                if (stack.isDamageableItem()) {
                    slot.put("damage", stack.getDamageValue());
                    slot.put("maxDamage", stack.getMaxDamage());
                }
                if (stack.getItem() instanceof IReactorComponent component
                        && component.canStoreHeat(stack, reactor, x, y)) {
                    slot.put("heat", component.getCurrentHeat(stack, reactor, x, y));
                    slot.put("maxHeat", component.getMaxHeat(stack, reactor, x, y));
                }
                out.add(slot);
            }
        }
        return out;
    }

    /** The reactor's own block entity, for tests. */
    static BlockEntity core(IReactor reactor) {
        return reactor.getCoreTe();
    }
}
