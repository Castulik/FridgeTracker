package com.example.fridgetracker_001.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fridgetracker_001.data.dao.CodeDao
import com.example.fridgetracker_001.data.dao.NakupDao
import com.example.fridgetracker_001.data.dao.PolozkyDao
import com.example.fridgetracker_001.data.dao.PotravinaDao
import com.example.fridgetracker_001.data.dao.SeznamDao
import com.example.fridgetracker_001.data.dao.SkladDao
import com.example.fridgetracker_001.data.entities.CodeEntity
import com.example.fridgetracker_001.data.entities.NakupEntity
import com.example.fridgetracker_001.data.entities.PolozkyEntity
import com.example.fridgetracker_001.data.entities.PotravinaEntity
import com.example.fridgetracker_001.data.entities.SeznamEntity
import com.example.fridgetracker_001.data.entities.SkladEntity

//Třída, která reprezentuje samotnou databázi Room. Je to centrální bod, který spojuje všechny tabulky a DAO.
//Slouží k inicializaci databáze a přístupu k DAO.

//Anotace @Database označuje třídu jako databázi Room.
//entities = databaze bude obsahovat tabulku reprezentovanou třídou SkladEntity.
@Database(entities = [SkladEntity::class, PotravinaEntity::class, CodeEntity::class, SeznamEntity::class, NakupEntity::class, PolozkyEntity::class], version = 37, exportSchema = false)

//Deklarace abstraktní třídy SkladDatabase, která dědí od RoomDatabase.
//Slouží jako hlavní přístupový bod k databázi. Room z této třídy vygeneruje konkrétní implementaci.
abstract class SkladDatabase : RoomDatabase() {

    //Abstraktní funkce, která vrací instanci SkladDao. Poskytuje metody pro přístup k datům v databázi prostřednictvím DAO
    abstract fun skladDao(): SkladDao
    abstract fun potravinaDao(): PotravinaDao
    abstract fun codeDao(): CodeDao
    abstract fun seznamDao(): SeznamDao
    abstract fun nakupDao(): NakupDao
    abstract fun polozkyDao(): PolozkyDao

    //Deklarace objektu společníka (companion object), což je ekvivalent statických členů v Javě.
    //Umožňuje definovat členy a metody, které jsou sdílené mezi všemi instancemi třídy.
    companion object {

        //@Volatile: Zajišťuje, že čtení a zápis této proměnné jsou viditelné pro všechna vlákna okamžitě
        //Deklarace proměnné INSTANCE typu SkladDatabase?, inicializované na null
        //Proměnná INSTANCE uchovává referenci na jedinou instanci databáze SkladDatabase - Singleton
        @Volatile
        private var INSTANCE: SkladDatabase? = null

        //Funkce, která vrací instanci SkladDatabase.
        //Implementuje Singleton pattern, aby byla zajištěna existence pouze jedné instance databáze v rámci aplikace.
        fun getDatabase(context: Context): SkladDatabase {

            //Když INSTANCE již existuje, vrací se. Když neexistuje přechází se do synchronizovaného bloku.
            //this v synchronized(this) odkazuje na objekt společníka (companion object) třídy SkladDatabase.
            //Synchronizovaný blok zajišťuje, že pouze jedno vlákno může vstoupit do tohoto bloku najednou. Zabraňuje vytvoření více než jedné instance databáze v multithreadovém prostředí.
            return INSTANCE ?: synchronized(this) {

                //Vytvoření nové instance databáze pomocí Room.databaseBuilder.
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    SkladDatabase::class.java,
                    "sklad_database"
                )
                .fallbackToDestructiveMigration()
                .build()

                //INSTANCE je nullable (SkladDatabase?), ale v tomto místě víme, že už není null, protože jsme ji právě inicializovali.
                //Operátor !! označuje, že se jedná o bezpečný přístup a kompilátor nebude upozorňovat na možnou null hodnotu.
                return INSTANCE!!
            }
        }
    }
}