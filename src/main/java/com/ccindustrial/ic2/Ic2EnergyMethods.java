package com.ccindustrial.ic2;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import ic2.core.block.comp.Energy;
import ic2.core.block.tileentity.Ic2TileEntity;

/**
 * The EU buffer that most IC2 blocks (machines, generators, storage) have. Added as an extra
 * {@code ic2_energy} type, so it combines with the block's main type.
 *
 * <pre>{@code
 * local m = peripheral.find("ic2_energy")
 * print(m.getEU(), "/", m.getEUCapacity())
 * }</pre>
 */
public final class Ic2EnergyMethods implements GenericPeripheral {
    @Override
    public String id() {
        return "ccindustrial:ic2_energy";
    }

    @Override
    public PeripheralType getType() {
        return PeripheralType.ofAdditional("ic2_energy");
    }

    /** EU currently stored. */
    @LuaFunction(mainThread = true)
    public final double getEU(Ic2TileEntity block) throws LuaException {
        return energy(block).getEnergy();
    }

    /** Most EU the block can hold. */
    @LuaFunction(mainThread = true)
    public final double getEUCapacity(Ic2TileEntity block) throws LuaException {
        return energy(block).getCapacity();
    }

    /** Stored EU as a fraction, 0 to 1. */
    @LuaFunction(mainThread = true)
    public final double getEUFill(Ic2TileEntity block) throws LuaException {
        return energy(block).getFillRatio();
    }

    /** Voltage tier the block accepts (1 = LV, 2 = MV, 3 = HV, 4 = EV), 0 if it accepts none. */
    @LuaFunction(mainThread = true)
    public final int getInputTier(Ic2TileEntity block) throws LuaException {
        return energy(block).getSinkTier();
    }

    /** Voltage tier the block emits, 0 if it emits none. */
    @LuaFunction(mainThread = true)
    public final int getOutputTier(Ic2TileEntity block) throws LuaException {
        return energy(block).getSourceTier();
    }

    /** Whether the block is working right now (the "active" texture). */
    @LuaFunction(mainThread = true)
    public final boolean isActive(Ic2TileEntity block) {
        return block.getActive();
    }

    static Energy energy(Ic2TileEntity block) throws LuaException {
        Energy energy = block.hasComponent(Energy.class) ? block.getComponent(Energy.class) : null;
        if (energy == null) {
            throw new LuaException("this IC2 block has no EU buffer");
        }
        return energy;
    }
}
