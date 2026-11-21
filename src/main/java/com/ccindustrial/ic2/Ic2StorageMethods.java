package com.ccindustrial.ic2;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import ic2.api.tile.IEnergyStorage;

/**
 * BatBox, CESU, MFE and MFSU (type {@code ic2_storage}).
 *
 * <pre>{@code
 * local box = peripheral.find("ic2_storage")
 * print(("%d / %d EU"):format(box.getStored(), box.getCapacity()))
 * }</pre>
 */
public final class Ic2StorageMethods implements GenericPeripheral {
    @Override
    public String id() {
        return "ccindustrial:ic2_storage";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("ic2_storage");
    }

    /** EU stored. */
    @LuaFunction(mainThread = true)
    public final int getStored(IEnergyStorage storage) {
        return storage.getStored();
    }

    /** EU the block can hold. */
    @LuaFunction(mainThread = true)
    public final int getCapacity(IEnergyStorage storage) {
        return storage.getCapacity();
    }

    /** Packet size it sends out, in EU. */
    @LuaFunction(mainThread = true)
    public final int getOutput(IEnergyStorage storage) {
        return storage.getOutput();
    }

    /** EU per tick it can send out. */
    @LuaFunction(mainThread = true)
    public final double getOutputPerTick(IEnergyStorage storage) {
        return storage.getOutputEnergyUnitsPerTick();
    }
}
