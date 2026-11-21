package com.ccindustrial.ic2;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;

/**
 * Processing machines: macerator, extractor, compressor, electric furnace, recycler, metal
 * former, ore washing plant and the rest (type {@code ic2_machine}).
 *
 * <pre>{@code
 * local mac = peripheral.find("ic2_machine")
 * print(("%.0f%%"):format(mac.getProgress() * 100))
 * }</pre>
 */
public final class Ic2MachineMethods implements GenericPeripheral {
    @Override
    public String id() {
        return "ccindustrial:ic2_machine";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("ic2_machine");
    }

    /** Progress of the current operation, 0 to 1. */
    @LuaFunction(mainThread = true)
    public final double getProgress(TileEntityStandardMachine<?, ?, ?> machine) {
        return machine.getProgress();
    }

    /** Ticks one operation takes, with upgrades applied. */
    @LuaFunction(mainThread = true)
    public final int getOperationLength(TileEntityStandardMachine<?, ?, ?> machine) {
        return machine.operationLength;
    }

    /** EU used per tick while working, with upgrades applied. */
    @LuaFunction(mainThread = true)
    public final int getEnergyPerTick(TileEntityStandardMachine<?, ?, ?> machine) {
        return machine.energyConsume;
    }

    /** Operations done per tick (overclocker upgrades raise it). */
    @LuaFunction(mainThread = true)
    public final int getOperationsPerTick(TileEntityStandardMachine<?, ?, ?> machine) {
        return machine.operationsPerTick;
    }
}
