package com.heimdall.aetherloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.lifecycle.lifecycleScope
import com.heimdall.aetherloop.core.HapticManager
import com.heimdall.aetherloop.core.PlayerProgressManager
import com.heimdall.aetherloop.core.SoundSynthesizer
import com.heimdall.aetherloop.game.engine.GameLoopEngine
import com.heimdall.aetherloop.game.engine.GameState
import com.heimdall.aetherloop.ui.components.*
import com.heimdall.aetherloop.ui.theme.HeimdallAetherLoopTheme
import com.heimdall.aetherloop.updater.GitHubUpdateManager
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Main Activity of AetherLoop.
 *
 * Implements a continuous 60 FPS hardware-accelerated game loop with
 * full-screen tap-to-dispatch mechanics, relic powers, and GitHub OTA updates.
 */
class MainActivity : ComponentActivity() {

    private lateinit var soundSynthesizer: SoundSynthesizer
    private lateinit var hapticManager: HapticManager
    private lateinit var progressManager: PlayerProgressManager
    private lateinit var engine: GameLoopEngine
    private lateinit var updateManager: GitHubUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundSynthesizer = SoundSynthesizer()
        hapticManager = HapticManager(this)
        progressManager = PlayerProgressManager(this)

        // Apply saved preferences
        soundSynthesizer.isMuted = !progressManager.isSoundEnabled()
        hapticManager.isHapticsEnabled = progressManager.isHapticsEnabled()

        engine = GameLoopEngine(soundSynthesizer, hapticManager, progressManager)
        updateManager = GitHubUpdateManager(this)

        setContent {
            HeimdallAetherLoopTheme {
                var showSettings by remember { mutableStateOf(false) }
                val updateState by updateManager.updateState.collectAsState()
                val gameState by engine.gameState.collectAsState()

                var canvasWidth by remember { mutableFloatStateOf(1080f) }
                var canvasHeight by remember { mutableFloatStateOf(1920f) }

                // Continuous 60 FPS frame ticker
                var frameTick by remember { mutableLongStateOf(0L) }

                LaunchedEffect(Unit) {
                    var lastNanos = 0L
                    while (isActive) {
                        withFrameNanos { nowNanos ->
                            if (lastNanos != 0L) {
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
                    // 1. Core Physics & Graphics Canvas with full-screen tap dispatch
                    GameCanvasView(
                        engine = engine,
                        frameTick = frameTick,
                        onTapScreen = { engine.dispatchNextVessel() }
                    )

                    // 2. Interactive Glassmorphic HUD with Relic Powers & Combos
                    HudOverlay(
                        engine = engine,
                        onOpenSettings = { showSettings = true }
                    )

                    // 3. Level Complete Modal with Stars & Scores
                    if (gameState == GameState.REALM_STABILIZED) {
                        RealmCompleteDialog(
                            level = engine.currentLevel,
                            score = engine.currentScore.intValue,
                            stars = engine.earnedStars.intValue,
                            isNewHighScore = engine.isNewHighScore.value,
                            onNextLevel = { engine.nextLevel() },
                            onReplay = { engine.restartCurrentLevel() }
                        )
                    }

                    // 4. Settings & Level Selector Modal
                    if (showSettings) {
                        SettingsDialog(
                            engine = engine,
                            onDismiss = { showSettings = false },
                            onCheckUpdate = {
                                lifecycleScope.launch {
                                    updateManager.checkForUpdates()
                                }
                            }
                        )
                    }

                    // 5. In-Device GitHub Releases Updater Modal
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
}
