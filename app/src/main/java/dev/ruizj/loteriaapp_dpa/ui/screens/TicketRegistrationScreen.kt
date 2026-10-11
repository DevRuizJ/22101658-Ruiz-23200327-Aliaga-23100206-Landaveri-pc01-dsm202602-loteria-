package dev.ruizj.loteriaapp_dpa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.ruizj.loteriaapp_dpa.model.DrawStatus
import dev.ruizj.loteriaapp_dpa.ui.components.BallItem
import dev.ruizj.loteriaapp_dpa.ui.components.BallType
import dev.ruizj.loteriaapp_dpa.viewmodel.AuthViewModel
import dev.ruizj.loteriaapp_dpa.viewmodel.LotteryViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TicketRegistrationScreen(
    userEmail: String,
    userId: String,
    authViewModel: AuthViewModel,
    lotteryViewModel: LotteryViewModel,
    onNavigateToLiveDraw: () -> Unit,
    onLogout: () -> Unit
) {
    val lotteryState by lotteryViewModel.lotteryState.collectAsState()

    LaunchedEffect(userId) {
        lotteryViewModel.initLottery(userId = userId)
    }

    val isDrawOpen = lotteryState.currentDraw.status == DrawStatus.OPEN
    val selectedNumbers = lotteryState.selectedNumbers
    val hasRegisteredTicket = lotteryState.userTicket != null

    Scaffold(
        topBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Usuario Conectado:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = {
                            authViewModel.signOut()
                            onLogout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Draw Info Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = lotteryState.currentDraw.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Fecha: ${lotteryState.currentDraw.date}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val statusBg = when (lotteryState.currentDraw.status) {
                        DrawStatus.OPEN -> Color(0xFFE8F5E9)
                        DrawStatus.IN_PROGRESS -> Color(0xFFFFF3E0)
                        DrawStatus.FINISHED -> Color(0xFFFFEBEE)
                    }
                    val statusColor = when (lotteryState.currentDraw.status) {
                        DrawStatus.OPEN -> Color(0xFF2E7D32)
                        DrawStatus.IN_PROGRESS -> Color(0xFFE65100)
                        DrawStatus.FINISHED -> Color(0xFFC62828)
                    }
                    val statusText = when (lotteryState.currentDraw.status) {
                        DrawStatus.OPEN -> "ABIERTO - Registro disponible"
                        DrawStatus.IN_PROGRESS -> "EN CURSO - Registro cerrado"
                        DrawStatus.FINISHED -> "FINALIZADO"
                    }

                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Estado: $statusText",
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Warning if Draw is NOT OPEN
            if (!isDrawOpen) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "El sorteo ya no está abierto. No se permite registrar ni modificar jugadas.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Error / Success message banners
            lotteryState.errorMessage?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            lotteryState.successMessage?.let { successMsg ->
                Text(
                    text = successMsg,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Selected Numbers Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (hasRegisteredTicket) "Tu Jugada Registrada (6 Números)" else "Números Seleccionados (${selectedNumbers.size}/6)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayNumbers = if (hasRegisteredTicket) {
                        lotteryState.userTicket?.numbers ?: emptyList()
                    } else {
                        selectedNumbers.toList().sorted()
                    }

                    if (displayNumbers.isEmpty()) {
                        Text(
                            text = "Selecciona 6 números abajo (1 a 45)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 6
                        ) {
                            displayNumbers.forEach { number ->
                                Box(modifier = Modifier.padding(4.dp)) {
                                    BallItem(
                                        number = number,
                                        type = BallType.SELECTED,
                                        size = 42.dp
                                    )
                                }
                            }
                        }
                    }

                    if (hasRegisteredTicket) {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Guardado",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Jugada guardada en Firestore",
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Controls & Selection Grid (Only if Draw is OPEN)
            if (isDrawOpen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = { lotteryViewModel.quickPick() },
                        enabled = isDrawOpen
                    ) {
                        Icon(imageVector = Icons.Default.Casino, contentDescription = "Quick Pick")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Azar")
                    }

                    OutlinedButton(
                        onClick = { lotteryViewModel.clearSelectedNumbers() },
                        enabled = isDrawOpen && selectedNumbers.isNotEmpty()
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Limpiar")
                    }
                }

                Text(
                    text = "Toca para seleccionar 6 números (1 al 45):",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                // Grid 1..45
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ) {
                        items((1..45).toList()) { num ->
                            val isSelected = selectedNumbers.contains(num)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(3.dp)
                            ) {
                                BallItem(
                                    number = num,
                                    type = if (isSelected) BallType.SELECTED else BallType.NORMAL,
                                    size = 38.dp,
                                    onClick = {
                                        if (isDrawOpen) {
                                            lotteryViewModel.toggleNumberSelection(num)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        lotteryViewModel.registerTicket(
                            userId = userId,
                            userEmail = userEmail
                        )
                    },
                    enabled = isDrawOpen && selectedNumbers.size == 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (hasRegisteredTicket) "Actualizar Jugada" else "Guardar / Registrar Jugada",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation button to Live Draw tracking
            Button(
                onClick = onNavigateToLiveDraw,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Live Draw")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ir al Seguimiento del Sorteo en Vivo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
