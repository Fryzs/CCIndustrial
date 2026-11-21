<div align="center">

# ⚡ CC: Industrial

**Industrial Craft 2 machines, energy storage and the nuclear reactor as CC: Tweaked peripherals.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-E67E22)
![Requires](https://img.shields.io/badge/requires-IC2%20Nostalgia%20%2B%20CC%3A%20Tweaked-C0392B)
![Version](https://img.shields.io/badge/version-0.1.0-2E86C1)
![License](https://img.shields.io/badge/license-MIT-27AE60)

**English** · [Українська](README.uk.md)

</div>

---

Put a computer next to an IC2 block — or connect it with a wired modem — and the block becomes a peripheral.
Read machine progress, battery charge, generator fuel, and every cell of a nuclear reactor, then build
dashboards, automation and **emergency shutdowns** in Lua. No new blocks, and IC2 itself is not changed.

## ✨ Peripherals

| Type | Blocks | Functions |
| --- | --- | --- |
| `ic2_energy` *(extra type)* | almost every IC2 block with an EU buffer | `getEU()`, `getEUCapacity()`, `getEUFill()` (0–1), `getInputTier()`, `getOutputTier()`, `isActive()` |
| `ic2_storage` | BatBox, CESU, MFE, MFSU | `getStored()`, `getCapacity()`, `getOutput()` (packet size), `getOutputPerTick()` |
| `ic2_machine` | macerator, extractor, compressor, electric furnace, recycler, … | `getProgress()` (0–1), `getOperationLength()`, `getEnergyPerTick()`, `getOperationsPerTick()` |
| `ic2_generator` | generator, geothermal, water, wind, solar, … | `getFuel()`, `isGenerating()` |
| `ic2_reactor` | the nuclear reactor **and any of its chambers** | `getHeat()`, `getMaxHeat()`, `getHeatFill()` (0–1), `getEUOutput()`, `isRunning()`, `isFluidCooled()`, `getComponents()` |

`getComponents()` returns every component in the reactor grid as
`{ x, y, name, count, damage, maxDamage, heat, maxHeat }` (x and y start at 1; `heat` only for parts that store heat).

The reactor is **read-only**: switch it on and off with redstone, which the computer can output itself.

## 🚀 Examples

### Reactor SCRAM

```lua
local reactor = peripheral.find("ic2_reactor")
local side = "back"            -- the side of the computer that powers the reactor

redstone.setOutput(side, true) -- start
while true do
  local fill = reactor.getHeatFill()
  print(("Heat %.0f%%, %d EU/t"):format(fill * 100, reactor.getEUOutput()))
  if fill > 0.5 then
    redstone.setOutput(side, false)
    print("SCRAM! Reactor stopped")
    break
  end
  sleep(1)
end
```

### MFSU charge on a monitor

```lua
local box = peripheral.find("ic2_storage")
local mon = peripheral.find("monitor")
while true do
  mon.clear(); mon.setCursorPos(1, 1)
  mon.write(("%d / %d EU"):format(box.getStored(), box.getCapacity()))
  sleep(2)
end
```

> 💡 With [CC: CSharp](https://github.com/Fryzs/CCCSharp) you can write the same programs in C#:
> `Peripheral.Find("ic2_reactor").GetHeatFill()`.

## 📦 Installation

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| Loader | NeoForge 21.1.x |
| Required | Industrial Craft 2: Nostalgia 21.1, CC: Tweaked ≥ 1.110 |
| Side | **server** (and single player); clients joining a server do not need it |

Drop `ccindustrial-0.1.0.jar` into the `mods` folder.

## 🛠️ Building and tests

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` starts a **headless** server (no window): it places IC2 blocks in a test world, checks that
CC: Tweaked sees each one as the right peripheral with the expected functions and values, and exits.

Compiling needs the IC2 Nostalgia jar in `libs/` (local only: never committed, never shipped). The CC: Tweaked
API comes from `maven.squiddev.cc`. For the tests, `run/mods` holds IC2 Nostalgia, CC: Tweaked and Curios.

## 🧩 How it works

It uses CC: Tweaked's official generic peripheral mechanism (`ComputerCraftAPI.registerGenericSource`) and reads
IC2's public interfaces (`IReactor`, `IReactorChamber`, `IEnergyStorage`) and its `Energy` component. No code from
other mods is copied.

## 📄 License

The CC: Industrial code is [MIT](LICENSE). Industrial Craft 2: Nostalgia (AGPL-3.0) and CC: Tweaked are separate
projects under their own licenses; no part of them is included here.
