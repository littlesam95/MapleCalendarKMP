package com.sixclassguys.maplecalendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sixclassguys.maplecalendar.domain.model.BossPartyBoard
import com.sixclassguys.maplecalendar.theme.MapleStatBackground
import com.sixclassguys.maplecalendar.theme.MapleStatTitle
import com.sixclassguys.maplecalendar.theme.MapleTheme
import com.sixclassguys.maplecalendar.theme.PretendardFamily
import com.sixclassguys.maplecalendar.theme.Typography
import com.sixclassguys.maplecalendar.utils.formatToYmd

@Composable
fun BossPartyAlbumContent(
    posts: List<BossPartyBoard>,
    isLastPage: Boolean,         // 더 가져올 데이터가 있는지 여부
    isLoading: Boolean,
    onLoadMore: () -> Unit,      // 추가 데이터 요청 콜백
    onSubmitBoard: () -> Unit,
    onLike: (Long) -> Unit,
    onDislike: (Long) -> Unit,
    onNavigateToImageDetail: (String) -> Unit,
    modifier: Modifier
) {
    LaunchedEffect(Unit) {
        if (posts.isEmpty()) {
            onLoadMore()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
            .background(MapleStatBackground, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // 타이틀 영역은 고정
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ALBUM",
                color = MapleStatTitle,
                style = Typography.titleMedium
            )
            IconButton(onClick = onSubmitBoard) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MapleTheme.colors.surface)
            }
        }

        // 🚀 3. 내부 영역을 LazyColumn으로 변경하여 자체 스크롤 가능하게 함
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .background(MapleTheme.colors.surface, shape = RoundedCornerShape(24.dp))
                .padding(12.dp)
        ) {
            if (posts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxHeight(), // 부모 높이만큼 채움
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyEventScreen("등록된 게시글이 없어요.")
                    }
                }
            } else {
                // 🚀 이제 내부에서 items를 사용하여 개별 스크롤을 지원합니다.
                itemsIndexed(posts, key = { _, post -> post.id }) { index, post ->
                    BossPartyAlbumItem(post, onLike, onDislike, onNavigateToImageDetail)

                    // 🚀 여기서 직접 체크!
                    // 마지막에서 1~2번째 아이템이 "그려지는 순간" 다음 페이지를 부릅니다.
                    if (index >= posts.size - 2 && !isLastPage && !isLoading) {
                        // 컴포지션 중에 이벤트를 발생시키기 위해 SideEffect 사용
                        SideEffect {
                            onLoadMore()
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 🚀 데이터 로딩 중임을 보여주는 하단 인디케이터
                if (!isLastPage) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MapleTheme.colors.primary, strokeWidth = 2.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BossPartyAlbumItem(
    post: BossPartyBoard,
    onLike: (Long) -> Unit,
    onDislike: (Long) -> Unit,
    onNavigateToImageDetail: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MapleTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 🚀 1. 메인 스크린샷 이미지
            AsyncImage(
                model = post.imageUrls[0],
                contentDescription = "Post Image",
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToImageDetail(post.imageUrls[0]) },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🚀 2. 작성자 정보 (프로필 이미지 + 이름/레벨/직업)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterProfileImage(
                    imageUrl = post.characterImage,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.characterName,
                            fontFamily = PretendardFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        // 월드 아이콘 등 추가 가능
                    }
                    Text(
                        text = "Lv.${post.characterLevel} ${post.characterClass}",
                        fontFamily = PretendardFamily,
                        fontSize = 12.sp,
                        color = MapleTheme.colors.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🚀 3. 본문 및 날짜
            Text(
                text = post.content,
                fontSize = 14.sp,
                fontFamily = PretendardFamily,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = formatToYmd(post.createdAt),
                fontSize = 11.sp,
                color = MapleTheme.colors.outline,
                fontFamily = PretendardFamily,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🚀 4. 리액션 (좋아요/싫어요)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onLike(post.id) }) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = " ${post.likeCount}",
                    fontSize = 13.sp,
                    fontFamily = PretendardFamily
                )
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onDislike(post.id) }) {
                    Icon(
                        imageVector = Icons.Default.ThumbDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = " ${post.dislikeCount}",
                    fontSize = 13.sp,
                    fontFamily = PretendardFamily
                )
            }
        }
    }
}