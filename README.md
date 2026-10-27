# AetherLoop ⚔️🌌

> **Videojuego Oficial del Ecosistema Heimdall**  
> *Defensa Naval en el Río Sagrado Gjöll • Combate Arkanoid Vikingo a 60 FPS*

[![Versión](https://img.shields.io/badge/version-v2.2.0-00E5FF.svg)](https://github.com/Hungrow/heimdall-aetherloop/releases)
[![Ecosistema](https://img.shields.io/badge/Ecosystem-Heimdall-FFD700.svg)](#ecosistema-heimdall)
[![Plataforma](https://img.shields.io/badge/Platform-Android%208.0%2B-00E676.svg)](#requisitos-e-instalación)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](https://developer.android.com/jetpack/compose)

---

## 📖 Visión General

**AetherLoop v2.2.0** introduce una expansión masiva de contenidos: **Pantalla de Bienvenida y Menú Principal**, **Mapa de Progresión estilo Río Gjöll con 100 Niveles**, **Jefes Colosales cada 25 niveles**, **Modo Oleadas Infinitas**, **Evolución Visual del Barco del Jugador (Tiers 1 a 4)**, **Movimiento y Cañoneo de Flotas Enemigas**, **Hilos Musicales Procedurales (BGM de Menú y Combate)**, y **Personalización de Perfil con 6 Retratos Artísticos**.

---

## ⚡ Novedades Principales (v2.2.0)

### 🌊 1. El Río Sagrado Gjöll: Mapa de 100 Niveles
* **Ruta Serpenteante y Nodos Sagrados**: Mapa vertical fluido con 100 niveles organizados a lo largo del cauce celestial.
* **Pin de Avatar del Jugador**: El retrato artístico de tu guerrero aparece enmarcado en oro directamente sobre el nodo de tu nivel actual, con estrellas obtenidas (1 a 3 ★).
* **Grandes Jefes Mitológicos cada 25 Niveles**:
  * **Nivel 25**: 🐉 *Dromon Jörmungandr* (La Serpiente de Midgard, 30 PV y cañones dobles).
  * **Nivel 50**: 🔥 *Leviatán de Surtr* (Fortaleza volcánica de Muspelheim, 50 PV y proyectiles de magma).
  * **Nivel 75**: ❄️ *Dragón Níðhöggr* (Acorazado de las raíces de Yggdrasil, 75 PV y blindaje de escarcha).
  * **Nivel 100**: 💀 *Naglfar Ragnarök* (El colosal barco de los muertos, 100 PV, batalla final por el cosmos).

### 🛡️ 2. Evolución y Daño del Barco Insignia (Tiers 1 a 4)
* **Tier 1 (Karve Dañado)**: Barcaza de madera agrietada tras recibir impactos directos (anchura reducida).
* **Tier 2 (Drakkar Guardián)**: Buque reforzado con mascarón de bronce y escudos estándar.
* **Tier 3 (Buque Insignia de Heimdall)**: Mascarón de dragón dorado, runas del Bifröst y torretas de balistas activas.
* **Tier 4 (Acorazado del Valhalla)**: Alas doradas de valquiria, máxima envergadura, doble artillería y aura celestial.
* *¡Las mejoras de runas suben de nivel a tu nave, mientras que los cañonazos enemigos degradan su aspecto y atributos!*

### 💣 3. Flotas Enemigas Dinámicas y Balas de Cañón
* **Patrulla y Descenso**: Los drakkars enemigos oscilan horizontalmente y avanzan lentamente hacia tu posición. Si alcanzan tu línea defensiva, causan daño crítico.
* **Artillería de Cañones**: Los navíos enemigos disparan bolas de cañón incandescentes hacia abajo. Desvíalas o esquívalas con tu escudo para evitar perder tiers o vidas.

### 🎵 4. Hilo Musical Procedural Dual (Menú y Combate)
* **BGM del Menú ("El Arpa del Bifröst")**: Melodía ambiental nórdica con arpegios modales de laúd/arpa en Re menor y bordón de violonchelo.
* **BGM de Batalla ("Tambores de Guerra de Asgard")**: Ritmo bélico percusivo a 60 FPS con redobles graves, ostinatos enérgicos y llamadas de cuerno de guerra.
* Controles dedicados en el menú para alternar música, efectos de sonido y háptica.

### 👤 5. Retratos Artísticos y Perfil de Guerrero
Elige tu retrato e introduce tu nombre de Jarl:
1. **Ragnar**: Jarl vikingo con barba trenzada, manto de piel y yelmo alado.
2. **Lagertha**: Doncella escudera con pintura de guerra azul y diadema de plata.
3. **Heimdall**: Guardián celestial de ojos dorados omniscientes y corona de Asgard.
4. **Björn**: Berserker con capucha de piel de oso y tatuajes tribales.
5. **Freya**: Vidente rúnica con velo violeta, plumas de cuervo y runas en la frente.
6. **Brunhild**: Valquiria serena de ojos esmeralda y alas de cisne.

### ⚔️ 6. Dos Modos de Juego
* **Campaña de la Historia**: 100 niveles por el río sagrado, historia de la traición de Loki y Surtr, y 4 grandes jefes.
* **Supervivencia del Valhalla (Oleadas Infinitas)**: Supervivencia extrema contra armadas que escalan en velocidad y frecuencia de disparo sin fin.

---

## 🛠️ Compilación e Instalación

Para compilar el APK firmado de producción:
```powershell
./gradlew assembleRelease
```
El instalador se genera en:
`app/build/outputs/apk/release/app-release.apk`
