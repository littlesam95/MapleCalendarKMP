package com.sixclassguys.maplecalendar.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sixclassguys.maplecalendar.MainActivity
import com.sixclassguys.maplecalendar.R
import com.sixclassguys.maplecalendar.data.local.AppPreferences
import com.sixclassguys.maplecalendar.domain.repository.NotificationEventBus
import com.sixclassguys.maplecalendar.domain.repository.NotificationRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private val notificationRepository: NotificationRepository by inject()
    private val eventBus: NotificationEventBus by inject()
    private val dataStore: AppPreferences by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 토큰이 갱신되면 즉시 백엔드에 등록
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepository.registerToken(token).first()
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            val isEnabled = dataStore.isNotificationMode.first()
            if (!isEnabled) {
                Napier.d("알림이 꺼져 있습니다.")
                return@launch
            }

            // 1. 공통 데이터 추출
            val title = message.notification?.title ?: message.data["title"] ?: "Maplendar"
            val body = message.notification?.body ?: message.data["body"] ?: "내용이 없습니다."
            val type = message.data["type"] // BOSS, EVENT 등
            val targetId = message.data["targetId"]?.toLongOrNull() ?: 0L
            val contentId = message.data["contentId"]?.toLongOrNull() ?: 0L

            // 2. 타입별 처리
            when (type) {
                "BOSS" -> {
                    // 보스 파티 전용 알림 표시
                    eventBus.emitBossPartyId(contentId)
                    showBossNotification(title, body, contentId)
                }

                "BOSSCHAT" -> {
                    showBossChatNotification(title, body, contentId)
                }

                "REFRESH_BOSS_ALARM" -> {
                    eventBus.emitBossPartyId(contentId)
                }

                else -> {
                    // 기존 이벤트 알림 로직 (eventId 기반)
                    eventBus.emitEvent(contentId)
                    showEventNotification(title, body, contentId)
                }
            }
        }
    }

    private fun showBossNotification(title: String, body: String, partyId: Long) {
        val channelId = "BOSS_PARTY_ALARM_V1" // 보스 전용 채널
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 채널 생성 (중복 호출되어도 안전함)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "보스 파티 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "보스 파티 예약 시간 알림"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 클릭 시 파티 상세 화면 등으로 보낼 정보 설정
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            // putExtra("navigate_to", "BOSS_DETAIL")
            // putExtra("partyId", partyId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            partyId.toInt(), // 알람마다 고유 ID 부여
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bottomnav_calendar) // 보스용 아이콘이 있다면 교체
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        notificationManager.notify(partyId.toInt(), builder.build())
    }

    private fun showBossChatNotification(title: String, body: String, partyId: Long) {
        val channelId = "BOSS_CHAT_ALARM_V1" // 보스 전용 채널
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 채널 생성 (중복 호출되어도 안전함)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "보스 파티 채팅 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "보스 파티 채팅 알림"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 클릭 시 파티 상세 화면 등으로 보낼 정보 설정
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            // putExtra("navigate_to", "BOSS_DETAIL")
            // putExtra("partyId", partyId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            partyId.toInt(), // 알람마다 고유 ID 부여
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bottomnav_calendar) // 보스용 아이콘이 있다면 교체
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        notificationManager.notify(partyId.toInt(), builder.build())
    }

    private fun showEventNotification(title: String?, body: String?, eventId: Long) {
        val channelId = "MAPLE_CALENDAR_HIGH_V3" // 채널명
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "이벤트 알림",
                NotificationManager.IMPORTANCE_HIGH // 팝업(헤드업)을 위한 설정
            ).apply {
                description = "이벤트 종료 및 중요 알림"
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
                // 잠금화면에서도 보이게 설정
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bottomnav_calendar)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 구버전 대응
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // 소리, 진동 필수
            .setContentIntent(pendingIntent)

        notificationManager.notify(eventId.toInt(), notificationBuilder.build())
    }
}