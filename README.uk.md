<div align="center">

# ⚡ CC: Industrial

**Машини, накопичувачі й ядерний реактор Industrial Craft 2 як периферія CC: Tweaked.**

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.x-E67E22)
![Requires](https://img.shields.io/badge/requires-IC2%20Nostalgia%20%2B%20CC%3A%20Tweaked-C0392B)
![Version](https://img.shields.io/badge/version-0.1.0-2E86C1)
![License](https://img.shields.io/badge/license-MIT-27AE60)

[English](README.md) · **Українська**

</div>

---

Постав комп'ютер поруч із блоком IC2 (або підключи дротовим модемом) — і блок стане периферією. Читай прогрес
машин, заряд накопичувачів, паливо генераторів і кожну комірку ядерного реактора, а далі будуй панелі, автоматику
та **аварійне вимкнення** на Lua. Нових блоків немає, сам IC2 не змінюється.

## ✨ Периферія

| Тип | Блоки | Функції |
| --- | --- | --- |
| `ic2_energy` *(додатковий тип)* | майже всі блоки IC2 з EU-буфером | `getEU()`, `getEUCapacity()`, `getEUFill()` (0–1), `getInputTier()`, `getOutputTier()`, `isActive()` |
| `ic2_storage` | BatBox, CESU, MFE, MFSU | `getStored()`, `getCapacity()`, `getOutput()` (розмір пакета), `getOutputPerTick()` |
| `ic2_machine` | мацератор, екстрактор, компресор, електропіч, переробник, … | `getProgress()` (0–1), `getOperationLength()`, `getEnergyPerTick()`, `getOperationsPerTick()` |
| `ic2_generator` | генератор, геотермальний, водяний, вітряний, сонячний, … | `getFuel()`, `isGenerating()` |
| `ic2_reactor` | ядерний реактор **і будь-яка його камера** | `getHeat()`, `getMaxHeat()`, `getHeatFill()` (0–1), `getEUOutput()`, `isRunning()`, `isFluidCooled()`, `getComponents()` |

`getComponents()` повертає всі компоненти в сітці реактора:
`{ x, y, name, count, damage, maxDamage, heat, maxHeat }` (x і y від 1; `heat` — для тих, що накопичують тепло).

Реактор **лише читається**: вмикати й вимикати його треба редстоуном, який комп'ютер подає сам.

## 🚀 Приклади

### Аварійне вимкнення реактора

```lua
local reactor = peripheral.find("ic2_reactor")
local side = "back"            -- з якого боку комп'ютера редстоун іде до реактора

redstone.setOutput(side, true) -- запустити
while true do
  local fill = reactor.getHeatFill()
  print(("Нагрів %.0f%%, %d EU/t"):format(fill * 100, reactor.getEUOutput()))
  if fill > 0.5 then
    redstone.setOutput(side, false)
    print("SCRAM! Реактор вимкнено")
    break
  end
  sleep(1)
end
```

### Заряд MFSU на моніторі

```lua
local box = peripheral.find("ic2_storage")
local mon = peripheral.find("monitor")
while true do
  mon.clear(); mon.setCursorPos(1, 1)
  mon.write(("%d / %d EU"):format(box.getStored(), box.getCapacity()))
  sleep(2)
end
```

> 💡 З [CC: CSharp](https://github.com/Fryzs/CCCSharp) ці самі програми можна писати на C#:
> `Peripheral.Find("ic2_reactor").GetHeatFill()`.

## 📦 Встановлення

| | |
| --- | --- |
| Minecraft | 1.21.1 |
| Завантажувач | NeoForge 21.1.x |
| Обов'язково | Industrial Craft 2: Nostalgia 21.1, CC: Tweaked ≥ 1.110 |
| Сторона | **сервер** (і одиночна гра); гравцям на сервері не потрібен |

Поклади `ccindustrial-0.1.0.jar` у папку `mods`.

## 🛠️ Збірка й тести

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` запускає сервер **без вікна**: ставить блоки IC2 у тестовому світі, перевіряє, що CC: Tweaked
бачить кожен як правильну периферію з потрібними функціями й значеннями, і сам завершується.

Для компіляції потрібен jar IC2 Nostalgia у `libs/` (лише локально: не комітиться й не входить у мод). API
CC: Tweaked береться з `maven.squiddev.cc`. Для тестів у `run/mods`: IC2 Nostalgia, CC: Tweaked, Curios.

## 🧩 Як це влаштовано

Використано офіційний механізм CC: Tweaked — generic peripherals (`ComputerCraftAPI.registerGenericSource`).
Дані беруться з публічних інтерфейсів IC2 (`IReactor`, `IReactorChamber`, `IEnergyStorage`) і його компонента
`Energy`. Код інших модів не копіюється.

## 📄 Ліцензія

Код CC: Industrial — [MIT](LICENSE). Industrial Craft 2: Nostalgia (AGPL-3.0) і CC: Tweaked — окремі проєкти під
своїми ліцензіями; жодна їхня частина сюди не входить.
