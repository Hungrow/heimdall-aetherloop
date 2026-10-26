# Heimdall: AetherLoop 🌌

> **Módulo Oficial del Ecosistema Heimdall**  
> *Resonancia Armónica del Bifröst • Videojuego de Física Granular y Puzzles Rítmicos*

[![Versión](https://img.shields.io/badge/version-v1.0.0-00E5FF.svg)](https://github.com/Hungrow/heimdall-aetherloop/releases)
[![Ecosistema](https://img.shields.io/badge/Ecosystem-Heimdall-FFD700.svg)](#ecosistema-heimdall)
[![Plataforma](https://img.shields.io/badge/Platform-Android%208.0%2B-00E676.svg)](#requisitos-e-instalación)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](https://developer.android.com/jetpack/compose)

---

## 📖 Visión General

**Heimdall: AetherLoop** reinventa la jugabilidad adictiva de los juegos de recolección en bucle (*loop conveyor & granular sorting*, como el clásico *Sand Loop*) trasladándola a la cosmología nórdico-futurista del **Ecosistema Heimdall**.

En lugar de arena convencional sobre tableros estáticos, el jugador asume el papel de **Heimdall**, el centinela omnisciente de Asgard. A través de fracturas dimensionales en el Árbol del Mundo (Yggdrasil), corrientes de **éter estelar armónico** caen en cascada hacia el puente Bifröst. El jugador debe calcular el momento exacto para despachar **vasijas de resonancia rúnica** a lo largo del circuito gravitatorio cerrado, interceptando las frecuencias de color correctas, estabilizando los Nueve Reinos y sellando las fisuras del vacío.

---

## ⚡ Características Principales

### 🌀 1. Jugabilidad y Mecánicas de Bucle (*Loop Physics*)
* **Circuito Paramétrico Cerrado del Bifröst**: Pista continua en forma de estadio calculada matemáticamente en tiempo real con posiciones, tangentes y ángulos de rotación de las naves.
* **Física Granular a 60 FPS**: Motor de partículas fluido con aceleración gravitacional, dispersión estocástica, detección de colisión volumétrica con amortiguamiento y rebote en caso de discordancia de color.
* **Sistema de Despacho Rítmico y Cola Táctica**:
  * Vista previa en tiempo real de las siguientes 3 vasijas rúnicas.
  * **Casilla de Reserva / Cambio Táctico (*Hold Slot*)**: Permite reservar o alternar vasijas para planificar secuencias complejas.
  * **Acelerador de Bucle (*Warp 1.6x*)**: Para jugadores avanzados que dominan el tempo y desean completar el nivel a máxima velocidad.
* **Nivel de Llenado Dinámico**: Simulación de superficie líquida/granular dentro de cada vasija con oleaje sinusoidal y destello de resonancia al completarse.

### 🎵 2. Audio ASMR Pentatónico Procedural y Háptica
* **Generación Procedural en Código**: Cero dependencias de archivos de audio externos (MP3/WAV). Se sintetizan ondas senoidales puras y armónicos mediante `AudioTrack` en tiempo real.
* **Escala Pentatónica Celestial**: Cada partícula recolectada activa una nota armoniosa (C5, D5, E5, G5, A5, C6), transformando las cascadas rápidas de éter en una melodía relajante tipo arpa cósmica.
* **Campana Resonante**: Sonido dual amortiguado al completar una vasija al 100%.
* **Respuesta Háptica Dinámica**: Vibraciones sutiles sincronizadas con las capturas de partículas y pulsos firmes en momentos de sellado rúnico.

### 📲 3. Actualizador Integrado en el Dispositivo (GitHub Releases OTA)
* La aplicación incluye un sistema de auto-actualización **Over-The-Air (OTA)** directo desde el repositorio oficial de GitHub:
  * Consulta la API pública de GitHub Releases (`https://api.github.com/repos/Hungrow/heimdall-aetherloop/releases/latest`).
  * Compara el `tag_name` semántico con la versión instalada.
  * Muestra notas de la versión (*changelog*) y botón de descarga directa.
  * Descarga el archivo `.apk` mostrando una barra de progreso en MB y porcentaje.
  * Lanza de forma nativa el instalador de paquetes de Android usando `FileProvider` con soporte completo para Android 8.0 hasta Android 15+.

---

## 🌌 Cosmología de los Reinos (Niveles)

| Nivel | Reino | Frecuencias Celestiales | Desafío |
| :---: | :--- | :--- | :--- |
| **1** | **Idavoll** | Sol Dorado (ᛊ) | Iniciación rítmica, flujo único constante. |
| **2** | **Bifröst** | Sol Dorado (ᛊ), Cian Bifröst (ᚱ) | Sincronización dual con alternancia de vasijas. |
| **3** | **Alfheim** | Sol, Cian, Violeta Valquiria (ᛗ) | Tres conductos activos con intervalos desfasados. |
| **4** | **Muspelheim** | Sol, Cian, Violeta, Llama Muspel (ᚲ) | Cuatro conductos, flujo acelerado y velocidad de bucle incrementada. |
| **5** | **Yggdrasil** | Las 5 Frecuencias + Verde Yggdrasil (ᛒ) | Gran convergencia armónica de los Nueve Mundos. |

---

## 🏛️ Arquitectura del Proyecto

El proyecto sigue una arquitectura limpia y modular desacoplada:

```
com.heimdall.aetherloop/
├── core/
│   ├── HeimdallEcosystem.kt      # Constantes, manifiesto y metadatos del ecosistema
│   ├── SoundSynthesizer.kt       # Síntesis procedural de audio en tiempo real (AudioTrack)
│   └── HapticManager.kt          # Gestión de vibración táctil
│
├── game/
│   ├── model/
│   │   ├── AetherColor.kt        # Definición de energías (colores, runas, frecuencias)
│   │   ├── Particle.kt           # Estado de partículas y pool de memoria reutilizable
│   │   ├── Vessel.kt             # Vasijas de resonancia, capacidades y llenado
│   │   ├── Conduit.kt            # Conductos celestiales de emisión
│   │   └── RealmLevel.kt         # Definición de niveles y parámetros de equilibrio
│   │
│   ├── physics/
│   │   ├── BifrostTrack.kt       # Geometría paramétrica y cálculo del circuito en bucle
│   │   └── ParticleEngine.kt     # Motor físico de partículas y colisión
│   │
│   └── engine/
│       └── GameLoopEngine.kt     # Máquina de estados del juego y orquestación
│
├── updater/
│   ├── GitHubRelease.kt          # Parseador del modelo de GitHub Releases
│   ├── GitHubUpdateManager.kt    # Cliente HTTP, gestor de descargas e instalador FileProvider
│   └── UpdateState.kt            # Estados reactivos (Checking, Available, Downloading, etc.)
│
└── ui/
    ├── theme/                    # Paleta cósmica Heimdall, tipografía y tema Compose
    ├── components/
    │   ├── GameCanvasView.kt     # Renderizado Canvas hardware-accelerated a 60 FPS
    │   ├── HudOverlay.kt         # HUD interactivo de cristal con cola y controles
    │   ├── UpdateDialog.kt       # Diálogo modal de actualización OTA desde el móvil
    │   ├── RealmCompleteDialog.kt# Pantalla de victoria con estadísticas de reino
    │   └── SettingsDialog.kt     # Ajustes de audio, selector de reinos y check de updates
    └── MainActivity.kt           # Activity principal con ciclo de renderizado withFrameNanos
```

---

## 🛠️ Requisitos e Instalación

### Dispositivos Android:
* Android 8.0 (API nivel 26) o superior.
* Descarga el APK firmado desde la pestaña [Releases](https://github.com/Hungrow/heimdall-aetherloop/releases).
* También puedes presionar **"Buscar Actualizaciones en GitHub"** desde los ajustes internos del juego en cualquier momento.

### Entorno de Desarrollo y Compilación:
* **JDK**: Java 21 LTS
* **Android SDK**: Build-Tools 34.0.0 / 35.0.0, Compile SDK 35
* **Gradle**: 8.7 (Gradle Wrapper incluido)

Para compilar el APK de lanzamiento firmado:
```powershell
./gradlew assembleRelease
```
El instalador resultante se ubicará en:
`app/build/outputs/apk/release/app-release.apk`

---

## 🌐 Ecosistema Heimdall

* **Nombre Provisional**: `Heimdall: AetherLoop`
* **Identificador de Módulo**: `heimdall.ecosystem.aetherloop`
* **Licencia**: MIT / Propiedad del Ecosistema Heimdall
* **Autor / Maintainer**: [Hungrow](https://github.com/Hungrow)
