# AetherLoop ⚔️🌌

> **Videojuego Oficial del Ecosistema Heimdall**  
> *Defensa Naval en los Fiordos de Asgard • Combate Arkanoid Vikingo a 60 FPS*

[![Versión](https://img.shields.io/badge/version-v2.0.0-00E5FF.svg)](https://github.com/Hungrow/heimdall-aetherloop/releases)
[![Ecosistema](https://img.shields.io/badge/Ecosystem-Heimdall-FFD700.svg)](#ecosistema-heimdall)
[![Plataforma](https://img.shields.io/badge/Platform-Android%208.0%2B-00E676.svg)](#requisitos-e-instalación)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](https://developer.android.com/jetpack/compose)

---

## 📖 Visión General

**AetherLoop v2.0.0** evoluciona hacia una experiencia de **combate naval rúnico estilo Arkanoid** en tiempo real. 

Las armadas invasoras de drakkars y gigantes del caos han zarpado hacia los fiordos sagrados del Bifröst. Como centinela de vanguardia de Heimdall, tomas el mando directo del **Escudo Insignia de Asgard**, desviando esferas de energía rúnica para destruir las naves enemigas antes de que alcancen el umbral del reino.

El juego está diseñado con **pantalla completa inmersiva de borde a borde (*Edge-to-Edge Sticky Immersive*)**, ocultando las barras nativas de Android y garantizando que el acceso a los ajustes respete las áreas seguras (*safe insets*) de cualquier pantalla o recorte de cámara (notch/punch-hole).

---

## ⚡ Características Principales

### 🛡️ 1. Jugabilidad Arkanoid de Acción Táctica
* **Control Preciso del Escudo de Asgard**: Desliza el dedo por la pantalla para mover tu pala insignia. El ángulo de rebote se calcula dinámicamente según el punto de impacto: golpes en los extremos devuelven disparos rasantes y veloces para flanquear las armadas enemigas.
* **Física y Rebotes Rúnicos**: Esferas de éter puro con estela cometaria que aceleran gradualmente tras cada impacto consecutivo.
* **Disparo Activo de Balistas**: Toca la pantalla con proyectiles activos para desatar salvas gemelas directas al corazón de las flotas.

### ⛵ 2. Armadas Vikingas Destructibles
Cada nave enemiga posee su propio comportamiento, resistencia y estética nórdica dibujada en Canvas vectorial:
* **Karve de Exploración**: Naves ligeras y rápidas con 1 PV.
* **Drakkar Acorazado**: Buque de guerra con escudos redondos en la borda y 2 PV.
* **Bergantín de Escarcha**: Navío blindado de las nieblas de Niflheim con 3 PV.
* **Acorazado de Fuego**: Naves llameantes de Muspelheim con 4 PV.
* **Naglfar (Buque Insignia Jefe)**: La mítica nave de uñas y huesos con barra de vida de 10 PV que cubre gran parte del fiordo superior.

### ᚱ 3. Runas y Potenciadores Sagrados
Al impactar y astillar drakkars enemigos, se liberan orbes rúnicos celestiales:
* **⚡ Mjölnir (Rayo de Thor)**: La esfera se electrifica, perforando naves enemigas de un solo impacto durante 8 segundos sin rebotar hacia abajo.
* **✨ Multibola del Bifröst**: Triplica la esfera activa creando 2 esferas adicionales simultáneas en juego.
* **🔥 Fuego de Surtr**: Provoca explosiones de área al impactar, dañando los navíos adyacentes.
* **🏹 Balistas Gemelas**: Equipa la pala con torretas láser que disparan proyectiles hacia el cielo al tocar la pantalla.
* **🛡️ Expansión de Aegis**: Duplica el ancho del escudo protector durante 10 segundos.
* **❤️ Reparación de Valkiria**: Restaura 1 vida perdida del escudo.

### 🎨 4. Gráficos Vectoriales y Audio Procedural
* **Fiordo del Norte & Aurora Boreal**: Oleaje dinámico animado, montañas nevadas y cortinas de auroras polares generadas en tiempo real.
* **Partículas y Astillas de Madera**: Explosiones físicas de esquirlas, números de daño flotantes y efectos de impacto.
* **Audio Procedural con `AudioTrack`**: Síntesis pura en tiempo real sin archivos pesados: resonancia metálica del escudo, crujidos de madera, explosiones graves, láseres y fanfarria de victoria.

### 📲 5. Actualizador Integrado en el Dispositivo (GitHub Releases OTA)
* Comprobación en vivo desde la API oficial de GitHub Releases (`Hungrow/heimdall-aetherloop`).
* Descarga en segundo plano e instalación directa del nuevo APK mediante `FileProvider` con un solo toque desde el menú de Ajustes.

---

## 🌊 Fases de Batalla Naval

| Oleada | Nombre | Flota Enemiga | Recompensa Táctica |
| :---: | :--- | :--- | :--- |
| **1** | **Avanzada del Fiordo** | 6 Karves de exploración | Familiarización con el control del escudo |
| **2** | **Asalto del Drakkar** | Drakkars acorazados en doble fila | Desbloqueo de runas Mjölnir y Balistas |
| **3** | **Nieblas de Niflheim** | Bergantines de escarcha blindados | Rebotes angulares de precisión |
| **4** | **Furia de Muspelheim** | Acorazados llameantes reforzados | Caos de multibola y control de masas |
| **5** | **La Cólera de Naglfar** | El buque insignia boss Naglfar escoltado | Batalla de resistencia y reflejos |
| **∞** | **Fiordo Infinito** | Formaciones procedurales continuas | Supervivencia de alta dificultad |

---

## 🛠️ Compilación e Instalación

Para compilar el APK firmado de producción:
```powershell
./gradlew assembleRelease
```
El instalador se genera en:
`app/build/outputs/apk/release/app-release.apk`
