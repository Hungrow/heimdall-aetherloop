# AetherLoop ⚔️🌌

> **Videojuego Oficial del Ecosistema Heimdall**  
> *Defensa Naval en los Fiordos de Asgard • Combate Arkanoid Vikingo a 60 FPS*

[![Versión](https://img.shields.io/badge/version-v2.1.0-00E5FF.svg)](https://github.com/Hungrow/heimdall-aetherloop/releases)
[![Ecosistema](https://img.shields.io/badge/Ecosystem-Heimdall-FFD700.svg)](#ecosistema-heimdall)
[![Plataforma](https://img.shields.io/badge/Platform-Android%208.0%2B-00E676.svg)](#requisitos-e-instalación)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10-4285F4.svg)](https://developer.android.com/jetpack/compose)

---

## 📖 Visión General

**AetherLoop v2.1.0** eleva drásticamente el listón gráfico y la inmersión del juego con **sprites vectoriales y pixel art de alta resolución**, **guerreros vikingos animados en cubierta y al abordaje**, y **mapas procedurals de múltiples biomas nórdicos** con oleaje dinámico, clima de partículas y sacudidas de pantalla (*screen shake*).

El jugador comanda el **Buque Insignia de Asgard**, tripulado por el **Jarl Comandante** y artilleros de balistas, desviando orbes rúnicos para quebrar las formaciones de drakkars invasores y enviando a sus guerreros Einherjar a saltar sobre los barcos enemigos en encarnizados combates de abordaje.

---

## 🎨 Gran Salto Gráfico: Sprites, Biomas y Guerreros

### 🚢 1. Sprites Personalizados de Alta Definición
Cada nave y elemento ha sido diseñado con sprites dedicados de alta fidelidad:
* **Karve de Exploración**: Madera de roble claro, mascarón de dragón dorado, vela a franjas rojas y blancas, y escudos de bronce.
* **Drakkar Acorazado**: Casco de roble fósil reforzado con bandas de hierro, mascarón de cuernos de oro, vela azul real con el Cuervo de Odín y 6 escudos solapados.
* **Bergantín de Escarcha (Niflheim)**: Maderas heladas con estalactitas de escarcha, cuernos de hielo, runa Algiz resplandeciente en cian y escudos escarchados.
* **Acorazado de Fuego (Muspelheim)**: Maderas quemadas con vetas de lava incandescente, espolón de bronce, vela de lenguas de fuego ardiente y escudos ígneos.
* **Naglfar (Jefe Legendario)**: El colosal buque fúnebre forjado con huesos y uñas de los muertos, costillares esqueléticos, tres mástiles con velas rasgadas y faroles de almas en verde espectral.
* **Buque Insignia de Asgard (Paddle)**: Pala de guerra regia con tallas de dragón en oro, runa central de valquiria, fila de escudos dorados y remos rúnicos.
* **Runas de Poder Grabadas**: Tablillas de piedra rúnica hexagonal con glifos grabados de Mjölnir, Fuego de Surtr, Trinidad de Bifröst, Balistas, Muro de Escudos y Curación.

---

### 🪓 2. Guerreros Vikingos en Acción
* **El Jarl Comandante**: Estacionado en el centro del buque insignia del jugador con su manto de piel, armadura de cota de oro y hacha de guerra en alto, celebrando y arengando en cada impacto.
* **Abordaje Einherjar**: Al impactar naves enemigas o encadenar golpes, guerreros vikingos saltan hacia las cubiertas enemigas, asestando tajos con sus hachas (`¡CORTE VIKINGO! 🪓`, daño crítico y esquirlas de madera), para luego zambullirse heroicamente en el fiordo.
* **Tripulación Enemiga**: Asaltantes vikingos visibles en las cubiertas de las naves invasoras con lanzas y cascos de hierro que caen al mar cuando sus buques son hundidos.

---

### 🌊 3. Mapas Procedurales y Biomas Nórdicos
Cada batalla naval se ambienta en un bioma específico con paletas acuáticas personalizadas, oleaje dinámico sinusoidal y partículas meteorológicas ambientales:
1. **Fiordo de Vigrid (Costas Sagradas)**: Aguas zafiro profundo, reflejos celestiales, bruma marina y arcos de auroras boreales.
2. **Glaciar de Niflheim (Mar de Hielo)**: Aguas glaciares con témpanos de hielo flotantes a la deriva, nieblas gélidas y ventiscas de copos de nieve.
3. **Abismo de Muspelheim (Mar de Fuego)**: Océano volcánico oscuro con fisuras de magma, rocas basálticas flotantes, resplandor rojizo y chispas de ceniza ascendentes.
4. **Aguas del Helheim (Fiordo Sombrío)**: Aguas violetas espectrales con restos de naufragios sumergidos, luz de luna fría y fuegos fatuos flotantes.
5. **Vórtice del Bifröst (Circuito Cósmico)**: Fondos cósmicos con polvo estelar, nebulosas centelleantes y corrientes de arcoíris estelar.

---

### ⚡ 4. Efectos Visuales e Impacto (*Juice*)
* **Hundimiento Realista**: Al ser destruidas, las naves no desaparecen de golpe: se escoran $18^\circ$, se fisuran, se llenan de burbujas y se hunden paulatinamente en el agua.
* **Estelas y Ondas de Agua (*Water Ripples*)**: Cada impacto sobre el agua o rebote genera ondas concéntricas de espuma luminosa que se expanden y disipan.
* **Sacudida de Pantalla (*Screen Shake*)**: Las detonaciones de naves y el impacto del jefe Naglfar sacuden la cámara dinámicamente con decaimiento suave.

---

## 🛠️ Compilación e Instalación

Para compilar el APK firmado de producción:
```powershell
./gradlew assembleRelease
```
El instalador se genera en:
`app/build/outputs/apk/release/app-release.apk`
