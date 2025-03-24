package com.example.fridgetracker_001.notifikace

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fridgetracker_001.R
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object NotificationUtils {

    private const val CHANNEL_ID = "expirace_channel_id"
    // Konstantní identifikátor kanálu pro notifikace (používá se při stavbě builderu).
    private const val CHANNEL_NAME = "Blížící se expirace"
    // Název kanálu notifikací.

    // 1) Funkce pro zobrazení notifikace
    fun showNotification(context: Context, title: String, message: String, iconId: Int) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        // Získáme službu NotificationManager (přes ContextCompat),
        // abychom mohli vytvářet a spravovat notifikace.

        // Nejdřív vytvoříme kanál notifikací (vyžaduje Android 8+)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Upozornění na blížící se expiraci potravin"
        }
        notificationManager.createNotificationChannel(channel)
        // Vytvoření/registrace kanálu v systému. Pokud kanál již existuje, nic se nestane.

        // Načteme bitmapu pro "large icon" (větší ikona v notifikaci)
        val largeIconBitmap = BitmapFactory.decodeResource(
            context.resources,
            iconId
        )

        // Vytvoříme samotnou notifikaci (NotificationCompat.Builder)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.fridgeapp)
            // Nastavujeme malou ikonu, která se zobrazuje v notifikační liště.

            .setLargeIcon(largeIconBitmap)
            // Nastavuje velkou ikonu, je vidět po rozbalení notifikace.

            .setContentTitle(title)
            // Titulek notifikace (velkým písmem).

            .setContentText(message)
            // Text notifikace.

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Priorita (ovlivňuje, jak vysoko se notifikace zobrazí,
            // jestli bude heads-up atd.)

            .setAutoCancel(true)
        // Po kliknutí na notifikaci zmizí (nezůstane v notifikační liště).

        // ID notifikace - generované náhodně, aby se notifikace neustále nepřepisovala
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    // 2) Funkce pro naplánování WorkManageru (1x za den)
    fun scheduleNext(context: Context) {
        // Zjistím, kolik milisekund zbývá do příštího 12:20 (nebo dnešního, pokud ještě nenastalo)
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(8, 0)
        var nextRun = LocalDateTime.of(LocalDate.now(), targetTime)

        // Pokud je "teď" už po 12:20, tak to posuneme o 1 den
        if (now.toLocalTime().isAfter(targetTime)) {
            nextRun = nextRun.plusDays(1)
        }

        // Vypočítáme, kolik ms zbývá od "teď" do "nextRun"
        val delay = Duration.between(now, nextRun).toMillis()

        // Vytvoříme OneTimeWorkRequest s tímto zpožděním
        val workRequest = OneTimeWorkRequestBuilder<ExpirationCheckWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        // OneTimeWorkRequestBuilder říká WorkManageru, že
        // chceme jednorázovou úlohu (po uplynutí 'delay').

        // Vložíme požadavek do WorkManager fronty, s unikátním názvem "DailyExpirationCheckWork"
        WorkManager.getInstance(context).enqueueUniqueWork(
            "DailyExpirationCheckWork",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        // "ExistingWorkPolicy.REPLACE" zajistí, že případný dřívější (nedokončený)
        // stejný job bude nahrazen tímto novým.

    }

    // 3) Příklad funkce pro výpočet daysLeft (může být i jinde)
    fun calculateDaysLeft(datumSpotreby: String): Long? {
        return try {
            // Definujeme si formátovač pro dd.MM.yyyy
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

            // Převedeme string (datumSpotreby) na LocalDate
            val expirationDate = LocalDate.parse(datumSpotreby, formatter)

            // Rozdíl v dnech mezi dneškem a 'expirationDate'
            ChronoUnit.DAYS.between(LocalDate.now(), expirationDate)
        } catch (e: Exception) {
            // Pokud dojde k chybě (např. špatný formát datumu), vrátí se null
            null
        }
    }
}
