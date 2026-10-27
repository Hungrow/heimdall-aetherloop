package com.heimdall.aetherloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.engine.GameState
import com.heimdall.aetherloop.game.engine.VikingArkanoidEngine
import com.heimdall.aetherloop.ui.components.*
import com.heimdall.aetherloop.ui.theme.HeimdallAetherLoopTheme
import com.heimdall.aetherloop.updater.GitHubUpdateManager
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    WELCOME_SPLASH,
    MAIN_MENU,
    GAMEPLAY
}

/**
 * Main Activity of AetherLoop: Viking Combat.
 *
 * Implements Welcome Screen, Main Menu with River Gjöll Map, and 60 FPS Arkanoid Combat.
 */
class MainActivity : ComponentActivity() {

    private lateinit var soundSynthesizer: SoundSynthesizer
    private lateinit var hapticManager: HapticManager
    private lateinit var progressManager: PlayerProgressManager
    private lateinit var engine: VikingArkanoidEngine
    private lateinit var updateManager: GitHubUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemBars()

        soundSynthesizer = SoundSynthesizer()
        hapticManager = HapticManager(this)
        progressManager = PlayerProgressManager(this)

        soundSynthesizer.isMuted = !progressManager.isSoundEnabled()
        soundSynthesizer.isMusicMuted = !progressManager.isMusicEnabled()
        hapticManager.isHapticsEnabled = progressManager.isHapticsEnabled()

        engine = VikingArkanoidEngine(soundSynthesizer, hapticManager, progressManager)
        updateManager = GitHubUpdateManager(this)

        setContent {
            HeimdallAetherLoopTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.WELCOME_SPLASH) }
                var showSettings by remember { mutableStateOf(false) }

                val updateState by updateManager.updateState.collectAsState()
                val gameState by engine.gameState.collectAsState()

                var canvasWidth by remember { mutableFloatStateOf(1080f) }
                var canvasHeight by remember { mutableFloatStateOf(1920f) }
                var frameTick by remember { mutableLongStateOf(0L) }

                // Start Menu Music on startup if enabled
                LaunchedEffect(Unit) {
                    if (progressManager.isMusicEnabled()) {
                        soundSynthesizer.startMenuMusic()
                    }
                }

                // Main Gameplay Physics Loop
                LaunchedEffect(currentScreen) {
                    var lastNanos = 0L
                    while (isActive) {
                        withFrameNanos { nowNanos ->
                            if (lastNanos != 0L && currentScreen == AppScreen.GAMEPLAY) {
                                val dt = ((nowNanos - lastNanos) / 1_000_000_000.0).toFloat()
                                engine.update(dt, canvasWidth, canvasHeight)
                            }
                            lastNanos = nowNanos
                            frameTick = nowNanos
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { size ->
                            canvasWidth = size.width.toFloat()
                            canvasHeight = size.height.toFloat()
                        }
                ) {
                    when (currentScreen) {
                        AppScreen.WELCOME_SPLASH -> {
                            WelcomeSplashScreen(
                                onEnter = {
                                    currentScreen = AppScreen.MAIN_MENU
                                    if (progressManager.isMusicEnabled()) {
                                        soundSynthesizer.startMenuMusic()
                                    }
                                }
                            )
                        }

                        AppScreen.MAIN_MENU -> {
                            MainMenuScreen(
                                progressManager = progressManager,
                                soundSynthesizer = soundSynthesizer,
                                onStartCampaignLevel = { level ->
                                    engine.loadCampaignLevel(level)
                                    currentScreen = AppScreen.GAMEPLAY
                                    if (progressManager.isMusicEnabled()) {
                                        soundSynthesizer.startBattleMusic()
                                    }
                                },
                                onStartEndlessMode = {
                                    engine.loadEndlessWave(1)
                                    currentScreen = AppScreen.GAMEPLAY
                                    if (progressManager.isMusicEnabled()) {
                                        soundSynthesizer.startBattleMusic()
                                    }
                                },
                                onOpenSettings = { showSettings = true }
                            )
                        }

                        AppScreen.GAMEPLAY -> {
                            // 1. Arkanoid Hardware-Accelerated Canvas
                            ArkanoidCanvasView(
                                engine = engine,
                                frameTick = frameTick
                            )

                            // 2. In-Game HUD Overlay
                            ArkanoidHudOverlay(
                                engine = engine,
                                onOpenSettings = { showSettings = true }
                            )

                            // 3. Mission Briefing Modal
                            if (gameState == GameState.MISSION_BRIEFING) {
                                MissionBriefingDialog(
                                    wave = engine.currentWave.value,
                                    onStart = { engine.startWaveAction() }
                                )
                            }

                            // 4. Wave Victory Modal
                            if (gameState == GameState.REALM_STABILIZED) {
                                RealmCompleteDialog(
                                    wave = engine.currentWave.value,
                                    score = engine.score.intValue,
                                    stars = engine.earnedStars.intValue,
                                    isNewHighScore = engine.isNewHighScore.value,
                                    onNextLevel = { engine.nextWave() },
                                    onReplay = { engine.restartCurrentWave() },
                                    onReturnToMenu = {
                                        currentScreen = AppScreen.MAIN_MENU
                                        if (progressManager.isMusicEnabled()) {
                                            soundSynthesizer.startMenuMusic()
                                        }
                                    }
                                )
                            }

                            // 5. Defeat Modal
                            if (gameState == GameState.VOID_OVERLOAD) {
                                DefeatDialog(
                                    wave = engine.currentWave.value,
                                    onRetry = { engine.restartCurrentWave() },
                                    onReturnToMenu = {
                                        currentScreen = AppScreen.MAIN_MENU
                                        if (progressManager.isMusicEnabled()) {
                                            soundSynthesizer.startMenuMusic()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Global Settings Dialog
                    if (showSettings) {
                        SettingsDialog(
                            engine = engine,
                            onDismiss = { showSettings = false },
                            onReturnToMenu = {
                                currentScreen = AppScreen.MAIN_MENU
                                if (progressManager.isMusicEnabled()) {
                                    soundSynthesizer.startMenuMusic()
                                }
                            },
                            onCheckUpdate = {
                                lifecycleScope.launch {
                                    updateManager.checkForUpdates()
                                }
                            }
                        )
                    }

                    // In-Device GitHub Releases Updater Modal
                    UpdateDialog(
                        state = updateState,
                        onDismiss = { updateManager.dismiss() },
                        onDownload = { release ->
                            lifecycleScope.launch {
                                updateManager.downloadUpdate(release)
                            }
                        },
                        onInstall = { apkFile ->
                            updateManager.installApk(apkFile)
                        },
                        onCheckAgain = {
                            lifecycleScope.launch {
                                updateManager.checkForUpdates()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        if (::soundSynthesizer.isInitialized && ::progressManager.isInitialized) {
            if (progressManager.isMusicEnabled()) {
                soundSynthesizer.startMenuMusic()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::soundSynthesizer.isInitialized) {
            soundSynthesizer.stopMusic()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
