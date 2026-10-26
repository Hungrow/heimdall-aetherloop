package com.heimdall.aetherloop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.heimdall.aetherloop.core.HeimdallEcosystem
import com.heimdall.aetherloop.ui.theme.*
import com.heimdall.aetherloop.updater.GitHubRelease
import com.heimdall.aetherloop.updater.UpdateState

/**
 * In-Device GitHub OTA Update Dialog.
 *
 * Allows one-tap updates straight from GitHub Releases.
 */
@Composable
fun UpdateDialog(
    state: UpdateState,
    onDismiss: () -> Unit,
    onDownload: (GitHubRelease) -> Unit,
    onInstall: (java.io.File) -> Unit,
    onCheckAgain: () -> Unit
) {
    if (state is UpdateState.Idle) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CosmosSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, CosmosBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CosmosCard)
                        .border(1.dp, HeimdallGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = HeimdallGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Actualizaciones Heimdall",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                when (state) {
                    is UpdateState.Checking -> {
                        CircularProgressIndicator(
                            color = BifrostCyan,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Consultando repositorio GitHub...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    is UpdateState.UpToDate -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = YggdrasilGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Tienes la versión más reciente instalada (${state.version}).",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = CosmosCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceptar", color = TextPrimary)
                        }
                    }

                    is UpdateState.Available -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmosCard)
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Nueva versión: ${state.release.tagName}",
                                color = BifrostCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.release.releaseNotes,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { onDownload(state.release) },
                            colors = ButtonDefaults.buttonColors(containerColor = HeimdallGold),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = CosmosDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Descargar e Instalar", color = CosmosDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    is UpdateState.Downloading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LinearProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = BifrostCyan,
                                trackColor = CosmosCard
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${(state.progress * 100).toInt()}% • ${String.format("%.1f", state.downloadedMb)} MB / ${String.format("%.1f", state.totalMb)} MB",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    is UpdateState.ReadyToInstall -> {
                        Text(
                            text = "Descarga completada con éxito. Presiona para instalar la actualización en tu dispositivo.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Button(
                            onClick = { onInstall(state.apkFile) },
                            colors = ButtonDefaults.buttonColors(containerColor = YggdrasilGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Instalar Ahora", color = CosmosDark, fontWeight = FontWeight.Bold)
                        }
                    }

                    is UpdateState.Error -> {
                        Text(
                            text = state.message,
                            color = MuspelCrimson,
                            fontSize = 13.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cerrar", color = TextSecondary)
                            }
                            Button(
                                onClick = onCheckAgain,
                                colors = ButtonDefaults.buttonColors(containerColor = CosmosCard),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reintentar", color = TextPrimary)
                            }
                        }
                    }

                    is UpdateState.Idle -> {}
                }
            }
        }
    }
}
