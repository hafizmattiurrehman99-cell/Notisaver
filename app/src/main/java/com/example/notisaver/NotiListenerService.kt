package com.example.notisaver

import android.app.Notification
import android.app.Person
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.service.notification.NotificationListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotiListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Skip the "group summary" notification (e.g. "2 new messages") -
        // the real per-message notifications arrive separately with full detail.
        if (sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) {
            return
        }

        val extras = sbn.notification.extras
        var title = extras.getCharSequence("android.title")?.toString() ?: ""
        var text = extras.getCharSequence("android.text")?.toString() ?: ""
        var isOutgoing = false

        // Apps like WhatsApp, Messages, Telegram use "MessagingStyle" notifications.
        // The real sender name and message text live in android.messages, not
        // in the plain title/text (which can be a vague summary like "2 messages").
        try {
            val messagesArray = extras.getParcelableArray("android.messages")
            if (messagesArray != null && messagesArray.isNotEmpty()) {
                val lastMessage = messagesArray.last() as? Bundle
                if (lastMessage != null) {
                    val messageText = lastMessage.getCharSequence("text")?.toString()

                    val senderPerson: Person? = try {
                        lastMessage.getParcelable("sender_person")
                    } catch (e: Exception) {
                        null
                    }
                    val senderName = senderPerson?.name?.toString()
                        ?: lastMessage.getCharSequence("sender")?.toString()

                    // "android.messagingUser" identifies the device owner (you) inside
                    // this conversation. If the message's sender matches that, it means
                    // this message was sent BY you - typically via a notification quick-reply.
                    val meUser: Person? = try {
                        extras.getParcelable("android.messagingUser")
                    } catch (e: Exception) {
                        null
                    }
                    if (meUser != null && senderPerson != null) {
                        val sameKey = meUser.key != null && meUser.key == senderPerson.key
                        val sameName = meUser.name != null && meUser.name == senderPerson.name
                        if (sameKey || sameName) {
                            isOutgoing = true
                        }
                    }

                    if (!messageText.isNullOrEmpty()) text = messageText
                    if (!senderName.isNullOrEmpty() && !isOutgoing) title = senderName
                }
            }
        } catch (e: Exception) {
            // Fall back silently to the plain title/text already read above
        }

        if (title.isEmpty() && text.isEmpty()) return

        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(sbn.packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            sbn.packageName
        }

        val entity = NotificationEntity(
            appName = appName,
            packageName = sbn.packageName,
            title = title,
            text = text,
            timestamp = sbn.postTime,
            isOutgoing = isOutgoing
        )

        scope.launch {
            AppDatabase.getInstance(applicationContext).notificationDao().insert(entity)
        }
    }
}
