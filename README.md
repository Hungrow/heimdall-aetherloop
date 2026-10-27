# AetherLoop 🌌

> **Videojuego Oficial del Ecosistema Heimdall**  
> *Resonancia Armónica del Bifröst • Física Granular, Mosaicos de Arena y Puzzles Rítmicos*

[![Versión](https://img.shields.io/badge/version-v1.2.0-00E5FF.svg)](https://github.com/Hungrow/heimdall-aetherloop/releases)
[![Ecosistema](https://img.shields.io/badge/Ecosystem-Heimdall-FFD700.svg)](#ecosistema-heimdall)
[![Plataforma](https://img.shields.io/badge/Platform-Android%208.0%2B-00E676.svg)](#requisitos-e-instalación)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](https://developer.android.com/jetpack/compose)

---

## 📖 Visión General

**AetherLoop** es la evolución definitiva de los juegos de física granular y despacho rítmico en bucle (*loop conveyor*). Ambientado en la cosmología nórdico-futurista de Asgard y el puente **Bifröst**, el jugador canaliza corrientes de **éter estelar armónico** que fluyen desde el Árbol del Mundo (Yggdrasil).

En cada reino, un mosaico sagrado de cientos de granos de arena se deconstruye en tiempo real. Despacha vasijas rúnicas a lo largo del circuito gravitatorio cerrado, intercepta las frecuencias correctas, activa poderes ancestrales de reliquia y estabiliza el cosmos.

---

## ⚡ Características Principales

### 🌀 1. Jugabilidad y Deconstrucción de Arte en Bucle
* **Mosaicos de Arena Dinámicos**: Reliquias rúnicas preformadas en la mitad superior de la pantalla que se disuelven y colapsan físicamente grano a grano.
* **Física Granular a 60 FPS con Cero Stutter**: Motor de partículas fluido con amortiguamiento, rebotes por discordancia y salpicaduras microscópicas (*splash sparks*).
* **Guías Láser de Puntería**: Líneas de proyección luminosas que conectan los embudos de vertido con la cinta transportadora.
* **Despacho Rítmico Táctil en Toda la Pantalla**: Toca en cualquier lugar del tablero para lanzar la siguiente vasija a la pista.
* **Mecánicas Tácticas Avanzadas**:
  * **Casilla de Reserva (*Hold / Swap*)**: Guarda y alterna vasijas para secuencias exigentes.
  * **Velocidad Warp (1.65x)**: Acelerador del circuito para jugadores expertos.
  * **Multiplicador de Combos (x1 a x5)**: Concatena capturas limpias para multiplicar tu puntuación y acelerar la carga de reliquias.

### 🛡️ 2. Poderes de Reliquia Nórdicos
* **ᛞ Dilatación Temporal (*Chrono Stasis*)**: Ralentiza la cinta transportadora y las partículas al 40% durante 5 segundos para sincronizaciones críticas.
* **ᚦ Oleada del Gjallarhorn**: Desata una onda acústica magnética que atrae todo el polvo estelar directamente hacia las vasijas correspondientes.

### 🎵 3. Audio ASMR Pentatónico Procedural y Háptica
* Generación directa en tiempo real con `AudioTrack` (C5, D5, E5, G5, A5, C6) que convierte las cascadas rápidas de partículas en un arpa cósmica.
* Retroalimentación háptica vibratoria coordinada con la absorción y sellado de vasijas.

### 🌟 4. Sistema de Progreso, 3 Estrellas y Récords
* Calificación de **1 a 3 estrellas** por nivel basada en precisión y velocidad.
* Registro persistente de mejores puntuaciones históricas por reino y éter total acumulado.

### 📲 5. Actualizador Integrado en el Dispositivo (GitHub Releases OTA)
* Comprobación en vivo desde la API de GitHub Releases con descarga en segundo plano e instalación directa mediante `FileProvider`.

---

## 🌌 Los 8 Reinos de Asgard

| Nivel | Reino | Frecuencias Celestiales | Desafío |
| :---: | :--- | :--- | :--- |
| **1** | **Idavoll** | Sol Dorado (ᛊ) | La forja solar: iniciación en la cadencia del bucle. |
| **2** | **Bifröst** | Sol Dorado (ᛊ), Cian Bifröst (ᚱ) | Sincronización alternada del puente del arcoíris. |
| **3** | **Alfheim** | Sol, Cian, Violeta Valquiria (ᛗ) | Velo de aurora y tres corrientes desfasadas. |
| **4** | **Nidavellir** | Cian, Violeta, Sol | Engranajes y fundición de los enanos de Ivaldi. |
| **5** | **Muspelheim** | Sol, Cian, Violeta, Llama Muspel (ᚲ) | Crisol de Surtr a alta velocidad. |
| **6** | **Niflheim** | Cian, Verde Yggdrasil (ᛒ), Violeta, Llama | Cristal de hielo y escarcha cósmica. |
| **7** | **Yggdrasil** | Las 5 Frecuencias | La gran convergencia de los Nueve Mundos. |
| **8** | **Nexus Infinito** | Modo Procedural Continuo | Incursión del vacío que escala en velocidad sin fin. |

---

## 🛠️ Compilación e Instalación

Para compilar el APK firmado:
```powershell
./gradlew assembleRelease
```
El instalador se genera en:
`app/build/outputs/apk/release/app-release.apk`
