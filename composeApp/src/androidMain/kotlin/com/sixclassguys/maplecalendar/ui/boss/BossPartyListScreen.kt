package com.sixclassguys.maplecalendar.ui.boss

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.InsertInvitation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.presentation.boss.BossIntent
import com.sixclassguys.maplecalendar.presentation.boss.BossViewModel
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.ui.component.BossPartyCard
import com.sixclassguys.maplecalendar.ui.component.BossPartyInvitationDialog
import com.sixclassguys.maplecalendar.utils.MapleWorld
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossPartyListScreen(
    viewModel: BossViewModel,
    onBack: () -> Unit,
    onPartyClick: (Long) -> Unit,
    onAddParty: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val eventBus = getKoin().get<NotificationEventBus>()

    LaunchedEffect(Unit) {
        eventBus.kickedPartyId.collect { kickedId ->
            if (kickedId != null) {
                viewModel.onIntent(BossIntent.FetchBossParties)
                Toast.makeText(context, "파티에서 추방되었어요.", Toast.LENGTH_SHORT).show()
                eventBus.emitKickedPartyId(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(BossIntent.FetchBossParties)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "보스방 목록",
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = MapleWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .background(MapleWhite) // 최하단 바닥 배경
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MapleStatBackground)
            ) {
                // 헤더 영역
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BOSS PARTY",
                        color = MapleStatTitle,
                        style = Typography.titleMedium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onIntent(BossIntent.ShowBossPartyInvitationDialog)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertInvitation,
                                contentDescription = null,
                                tint = MapleWhite
                            )
                        }
                        IconButton(
                            onClick = {
                                val allWorlds = MapleWorld.entries.map { it.worldName }
                                viewModel.onIntent(BossIntent.FetchCharacters(allWorlds))
                                onAddParty()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MapleWhite
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                        .background(MapleWhite, shape = RoundedCornerShape(24.dp))
                        .padding(12.dp) // 카드들과 흰 컨테이너 사이 여백
                ) {
                    // 🚀 파티 카드 리스트
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(uiState.bossParties) { party ->
                            BossPartyCard(bossParty = party, onPartyClick = { onPartyClick(party.id) })
                        }
                    }
                }
            }
        }
    }
    
    if (uiState.showBossInvitationDialog) {
        BossPartyInvitationDialog(
            invitations = uiState.bossPartiesInvited,
            onAccept = { bossPartyId ->
                viewModel.onIntent(BossIntent.AcceptBossPartyInvitation(bossPartyId))
            },
            onReject = { bossPartyId ->
                viewModel.onIntent(BossIntent.DeclineBossPartyInvitation(bossPartyId))
            },
            onDismiss = { viewModel.onIntent(BossIntent.DismissBossPartyInvitationDialog) }
        )
    }
}