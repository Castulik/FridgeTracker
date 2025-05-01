package com.example.fridgetracker_001.notifikace

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.SkladDatabase
import com.example.fridgetracker_001.notifikace.NotificationUtils.calculateDaysLeft
import com.example.fridgetracker_001.notifikace.NotificationUtils.scheduleNext
import com.example.fridgetracker_001.notifikace.NotificationUtils.showNotification
import com.example.fridgetracker_001.repository.PotravinaRepository
import com.example.fridgetracker_001.repository.SkladRepository

class ExpirationCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    // Hlavní funkce, která se spustí, když Worker začne pracovat
    override suspend fun doWork(): Result {
        return try {
            // 1) Vytvoření (nebo získání) instancí DB a repository
            val db = SkladDatabase.getDatabase(applicationContext)
            // Získání instance Room databáze (SkladDatabase), voláme "getDatabase",
            // která buď vytvoří novou nebo vrátí již existující instanci.

            val potravinaRepository = PotravinaRepository(db.potravinaDao())
            // Inicializuje se repository pro práci s tabulkou potravin (pomocí potravinaDao).

            val skladRepository = SkladRepository(db.skladDao())
            // Inicializuje se repository pro práci s tabulkou skladů (pomocí skladDao).

            // 2) Načti všechny potraviny
            val potraviny = potravinaRepository.getAllPotraviny()
            // Z potravinaRepository načteme seznam všech potravin uložených v DB.

            // 3) Pro každou potravinu zjisti sklad a expiraci
            potraviny.forEach { potravina ->
                // forEach projde každou potravinu v seznamu "potraviny"

                val sklad = skladRepository.ziskejSkladPodleId(potravina.skladId)
                // Pomocí skladRepository zjistíme konkrétní sklad,
                // k němuž tato potravina patří (skladId).

                val expirace1 = sklad?.expirace1?.toLongOrNull() ?: 0L
                val expirace2 = sklad?.expirace2?.toLongOrNull() ?: 0L
                // Pokud sklad není null, vezmeme hodnotu "expirace" (číslo dní),
                // jinak použijeme 0 (operátor "?: 0" je tzv. Elvis operator).

                val nazev = sklad?.nazev ?: "Neznámý sklad"
                // Pokud sklad není null, vezmeme z něj název, jinak text "Neznámý sklad".

                val iconId = potravina.potravinaIkona.resId
                // Z potraviny získáváme ID ikony, která se pak zobrazí v notifikaci.

                val daysLeft = calculateDaysLeft(potravina.datumSpotreby)
                // Vypočítáme, kolik dní zbývá do data spotřeby (datumSpotreby).

                if (daysLeft != null && (daysLeft == expirace1 || daysLeft == expirace2 || daysLeft == 0L)) {
                    // Pokud se nám podařilo spočítat daysLeft a
                    // zároveň je zbývající počet dnů menší nebo roven "expirace" ze skladu,
                    // znamená to, že potravina brzy expiruje -> zobrazíme notifikaci

                    showNotification(
                        context = applicationContext,
                        title = "Potravina v $nazev brzy expiruje!",
                        message = "${potravina.nazev} expiruje za $daysLeft dní.",
                        iconId = iconId
                    )
                }
            }
            // Po projití všech potravin zavoláme funkci pro znovu-naplánování notifikace
            scheduleNext(applicationContext)

            // Pokud vše proběhlo bez chyby, vrátíme "Result.success()",
            // čímž Worker signalizuje úspěšné dokončení
            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            // Pokud dojde k jakékoli chybě, tiskneme stacktrace a vracíme "Result.failure()"
            Result.failure()
        }
    }
}




