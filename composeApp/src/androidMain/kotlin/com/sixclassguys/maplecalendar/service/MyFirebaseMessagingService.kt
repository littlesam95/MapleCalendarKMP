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

            super.onMessageReceived(message)

            // 데이터에서 eventId 추출
            val eventIdStr = message.data["eventId"]
            val eventId = eventIdStr?.toLongOrNull()

            // 💡 EventBus에 eventId를 실어서 보냄
            eventBus.emitEvent(eventId ?: 0L)

            if (isEnabled) {
                super.onMessageReceived(message)

                val title = message.notification?.title ?: message.data["title"] ?: "알림"
                val body = message.notification?.body ?: message.data["body"] ?: "내용이 없습니다."

                showNotification(title, body)
            } else {
                Napier.d("알림이 꺼져 있습니다.")
            }
        }
    }

    private fun showNotification(title: String?, body: String?) {
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 구버전 대응
            .setDefaults(NotificationCompat.DEFAULT_ALL)   // 소리, 진동 필수
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}