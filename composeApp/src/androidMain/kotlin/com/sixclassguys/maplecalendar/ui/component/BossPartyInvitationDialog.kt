package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.BossParty
import com.sixclassguys.maplecalendar.theme.MapleBlack
import com.sixclassguys.maplecalendar.theme.MapleGray
import com.sixclassguys.maplecalendar.theme.MapleOrange
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleWhite
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.badgeBackground
import com.sixclassguys.maplecalendar.utils.badgeOutline
import com.sixclassguys.maplecalendar.utils.badgeText
import com.sixclassguys.maplecalendar.utils.entryBackgroundRes

@Composable
fun BossPartyInvitationDialog(
    invitations: List<BossParty>,
    onAccept: (Long) -> Unit,
    onReject: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MapleStatBackground
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "BOSS INVITATION",
                    style = Typography.titleMedium,
                    color = MapleStatTitle,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 흰색 메인 카드 섹션
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    if (invitations.isEmpty()) {
                        EmptyEventScreen("초대받은 보스 파티가 없어요.")
                    } else {
                        // 초대 목록 (최대 높이를 제한하여 리스트가 길어질 경우 스크롤 가능하게 처리)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.heightIn(max = 500.dp)
                        ) {
                            items(invitations) { invitation ->
                                InvitationCard(
                                    invitation = invitation,
                                    onAccept = { onAccept(invitation.id) },
                                    onReject = { onReject(invitation.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvitationCard(
    invitation: BossParty,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MapleWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            // 1. 상단 보스 이미지 영역
            AsyncImage(
                model = invitation.boss.entryBackgroundRes,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // 2. 정보 영역
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = invitation.title,
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    // 거절 버튼 (쓰레기통 아이콘)
                    IconButton(onClick = onReject) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "거절",
                            tint = MapleBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = invitation.difficulty.badgeBackground,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, invitation.difficulty.badgeOutline)
                    ) {
                        Text(
                            text = invitation.difficulty.displayName,
                            color = invitation.difficulty.badgeText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontFamily = PretendardFamily,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = invitation.boss.bossName,
                        fontFamily = PretendardFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MapleBlack
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MapleGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (invitation.memberCount == 1) invitation.leaderNickname else "${invitation.leaderNickname} 외 ${invitation.memberCount - 1}인",
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MapleGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. 초대 수락 버튼
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MapleOrange) // 주황색 버튼
                ) {
                    Text(
                        text = "초대 수락",
                        fontFamily = PretendardFamily,
                        fontWeight = FontWeight.Bold,
                        color = MapleWhite
                    )
                }
            }
        }
    }
}