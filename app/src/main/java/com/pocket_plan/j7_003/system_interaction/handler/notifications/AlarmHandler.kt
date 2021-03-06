package com.pocket_plan.j7_003.system_interaction.handler.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.pocket_plan.j7_003.system_interaction.receiver.NotificationReceiver
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class AlarmHandler {
    companion object {
        fun setBirthdayAlarms(hour: Int = 12, minute: Int = 0, context: Context) {
            try {
                val intent = Intent(context, NotificationReceiver::class.java)
                intent.putExtra("Notification", "Birthday")

                val pendingIntent =
                    PendingIntent.getBroadcast(
                        context,
                        100,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                val alarmManager =
                    context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

                var notificationTime = LocalDateTime.now()

                if (notificationTime.isAfter(
                        LocalDateTime.now()
                            .withHour(hour).withMinute(minute).withSecond(0)
                    )
                ) {
                    notificationTime = notificationTime.plusDays(1)
                }

                notificationTime = notificationTime
                    .withHour(hour).withMinute(minute)
                    .withSecond(0).withNano(0)

                // debug
//            val debugTime = LocalDateTime.now().plusSeconds(30)
//            notificationTime = debugTime

                val epochTimeToReminder =
                    notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

//                alarmManager.setExact(
//                    AlarmManager.RTC,
//                    notificationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//                    pendingIntent
//                )

                // Schedule alarm
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        epochTimeToReminder,
                        pendingIntent
                    );
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, epochTimeToReminder, pendingIntent);
                }

            } catch (_: Exception) {
            }
        }

        fun setNewSleepReminderAlarm(
            context: Context, dayOfWeek: DayOfWeek,
            reminderTime: LocalDateTime, requestCode: Int, isSet: Boolean
        ) {
            val intent = Intent(context, NotificationReceiver::class.java)

            intent.putExtra("Notification", "SReminder")
            intent.putExtra("weekday", dayOfWeek.toString())
            intent.putExtra("requestCode", requestCode)

            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager: AlarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (isSet) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                    pendingIntent
                )
            } else {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}