# CC: Industrial

Аддон для Minecraft 1.21.1 (NeoForge), що з'єднує **Industrial Craft 2: Nostalgia** і **CC: Tweaked**:
комп'ютери читають машини, генератори, накопичувачі й ядерний реактор IC2. Потрібні обидва моди.

## Як користуватися

Блоки IC2 стають периферією самі: став комп'ютер поруч (або підключи дротовим модемом) і
`peripheral.wrap("сторона")` чи `peripheral.find("тип")`. Нових блоків немає, IC2 не змінюється.

| Тип | Блоки | Функції |
| --- | --- | --- |
| `ic2_energy` (додатковий тип) | майже всі блоки IC2 з EU-буфером | `getEU()`, `getEUCapacity()`, `getEUFill()` (0–1), `getInputTier()`, `getOutputTier()`, `isActive()` |
| `ic2_storage` | BatBox, CESU, MFE, MFSU | `getStored()`, `getCapacity()`, `getOutput()` (розмір пакета), `getOutputPerTick()` |
| `ic2_machine` | мацератор, екстрактор, компресор, електропіч, переробник, … | `getProgress()` (0–1), `getOperationLength()`, `getEnergyPerTick()`, `getOperationsPerTick()` |
| `ic2_generator` | генератор, геотермальний, водяний, вітряний, сонячний, … | `getFuel()`, `isGenerating()` |
| `ic2_reactor` | ядерний реактор **і будь-яка його камера** | `getHeat()`, `getMaxHeat()`, `getHeatFill()` (0–1), `getEUOutput()`, `isRunning()`, `isFluidCooled()`, `getComponents()` |

`getComponents()` повертає всі компоненти в сітці реактора:
`{ x, y, name, count, damage, maxDamage, heat, maxHeat }` (x і y від 1; `heat` — для тих, що накопичують тепло).

Реактор лише читається. Вмикати й вимикати його — редстоуном, який комп'ютер подає сам.

### Приклад: аварійне вимкнення реактора

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

### Приклад: заряд MFSU на монітор

```lua
local box = peripheral.find("ic2_storage")
local mon = peripheral.find("monitor")
while true do
  mon.clear(); mon.setCursorPos(1, 1)
  mon.write(("%d / %d EU"):format(box.getStored(), box.getCapacity()))
  sleep(2)
end
```

## Збірка й тести

```
./gradlew build
./gradlew runGameTestServer
```

`runGameTestServer` — сервер **без вікна**: ставить блоки IC2 у тестовому світі, перевіряє, що CC: Tweaked
бачить їх як периферію з потрібними функціями й значеннями, і сам завершується.

Для компіляції потрібен jar IC2 Nostalgia у `libs/` (локально; не комітиться й не входить у мод).
API CC: Tweaked береться з `maven.squiddev.cc`. Для тестів у `run/mods`: IC2 Nostalgia, CC: Tweaked, Curios.

Jar: `build/libs/ccindustrial-0.1.0.jar` — ставиться на сервер (і в одиночну гру); клієнтам на сервері
не обов'язковий.

## Як влаштовано

Офіційний механізм CC: Tweaked — generic peripherals (`ComputerCraftAPI.registerGenericSource`).
CC: Industrial бере дані з публічних інтерфейсів IC2 (`IReactor`, `IReactorChamber`, `IEnergyStorage`) і його
компонентів (`Energy`). Код інших модів не копіюється.

## Ліцензія

Код CC: Industrial — [MIT](LICENSE). Industrial Craft 2: Nostalgia (AGPL-3.0) і CC: Tweaked — окремі проєкти
під своїми ліцензіями; жодна їхня частина сюди не входить.
