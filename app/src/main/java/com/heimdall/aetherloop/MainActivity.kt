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

/**
 * Main Activity of AetherLoop: Viking Combat.
 *
 * Implements true fullscreen Arkanoid naval battle against invading Viking fleets.
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
        hapticManager.isHapticsEnabled = progressManager.isHapticsEnabled()

        engine = VikingArkanoidEngine(soundSynthesizer, hapticManager, progressManager)
        updateManager = GitHubUpdateManager(this)

        setContent {
            HeimdallAetherLoopTheme {
                var showSettings by remember { mutableStateOf(false) }
                val updateState by updateManager.updateState.collectAsState()
                val gameState by engine.gameState.collectAsState()

                var canvasWidth by remember { mutableFloatStateOf(1080f) }
                var canvasHeight by remember { mutableFloatStateOf(1920f) }

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
                    // 1. Arkanoid Canvas (Longships, Paddle, Energy Balls, Splinters, Runes)
                    ArkanoidCanvasView(
                        engine = engine,
                        frameTick = frameTick
                    )

                    // 2. Safe HUD Overlay (Score, Lives, Safe Settings Button)
                    ArkanoidHudOverlay(
                        engine = engine,
                        onOpenSettings = { showSettings = true }
                    )

                    // 3. Mission Briefing Modal (Battle Plan)
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
                            onReplay = { engine.restartCurrentWave() }
                        )
                    }

                    // 5. Defeat Modal
                    if (gameState == GameState.VOID_OVERLOAD) {
                        DefeatDialog(
                            wave = engine.currentWave.value,
                            onRetry = { engine.restartCurrentWave() }
                        )
                    }

                    // 6. Safe Settings & Wave Selector Modal
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

                    // 7. In-Device GitHub Releases Updater Modal
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
