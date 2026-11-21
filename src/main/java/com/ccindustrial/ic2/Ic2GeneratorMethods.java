package com.ccindustrial.ic2;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;

/**
 * Fuel-based generators: generator, geothermal, water, wind, solar and the rest (type
 * {@code ic2_generator}).
 */
public final class Ic2GeneratorMethods implements GenericPeripheral {
    @Override
    public String id() {
        return "ccindustrial:ic2_generator";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofType("ic2_generator");
    }

    /** Fuel left in the generator (its own units; 0 means it needs fuel). */
    @LuaFunction(mainThread = true)
    public final int getFuel(TileEntityBaseGenerator generator) {
        return generator.fuel;
    }

    /** Whether it is turning fuel into EU right now (false when out of fuel or full). */
    @LuaFunction(mainThread = true)
    public final boolean isGenerating(TileEntityBaseGenerator generator) {
        return generator.isConverting();
    }
}
