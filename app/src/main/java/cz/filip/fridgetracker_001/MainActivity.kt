package cz.filip.fridgetracker_001

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import cz.filip.fridgetracker_001.repository.SkladRepository
import cz.filip.fridgetracker_001.data.SkladDatabase
import cz.filip.fridgetracker_001.notifikace.NotificationUtils
import cz.filip.fridgetracker_001.repository.CodeRepository
import cz.filip.fridgetracker_001.repository.NakupRepository
import cz.filip.fridgetracker_001.repository.PolozkyRepository
import cz.filip.fridgetracker_001.repository.PotravinaRepository
import cz.filip.fridgetracker_001.repository.SeznamRepository
import cz.filip.fridgetracker_001.viewmodel.SkladViewModel
import cz.filip.fridgetracker_001.viewmodel.SkladViewModelFactory
import cz.filip.fridgetracker_001.ui.theme.AppTheme
import cz.filip.fridgetracker_001.viewmodel.CodeViewModel
import cz.filip.fridgetracker_001.viewmodel.CodeViewModelFactory
import cz.filip.fridgetracker_001.viewmodel.NakupViewModel
import cz.filip.fridgetracker_001.viewmodel.NakupViewModelFactory
import cz.filip.fridgetracker_001.viewmodel.PolozkyViewModel
import cz.filip.fridgetracker_001.viewmodel.PolozkyViewModelFactory
import cz.filip.fridgetracker_001.viewmodel.PotravinaViewModel
import cz.filip.fridgetracker_001.viewmodel.PotravinaViewModelFactory
import cz.filip.fridgetracker_001.viewmodel.SeznamViewModel
import cz.filip.fridgetracker_001.viewmodel.SeznamViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var skladViewModel: SkladViewModel
    private lateinit var potravinaViewModel: PotravinaViewModel
    private lateinit var codeViewModel: CodeViewModel
    private lateinit var seznamViewModel: SeznamViewModel
    private lateinit var nakupViewModel: NakupViewModel
    private lateinit var polozkyViewModel: PolozkyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Volání metody onCreate, kde se inicializuje Activity

        // 1) Zkontroluju permission pro notifikace (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Pokud je verze systému Android >= 33 (Tiramisu), je nutné explicitní povolení notifikací
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Pokud nemáme udělené oprávnění:
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    123 // libovolný requestCode
                )
            } else {
                // Permission už je uděleno, rovnou naplánujeme Worker
                NotificationUtils.scheduleNext(this)
            }
        } else {
            // Pro starší verze Androidu se notifikace nemusí explicitně povolovat
            NotificationUtils.scheduleNext(this)
        }

        // Zavoláme si funkci pro nastavení "edge to edge" layoutu (např. pro fullscreen design)
        enableEdgeToEdge()

        // Nastavíme composable obsah pro tuto aktivitu
        setContent {

            // Inicializace databáze
            val database = SkladDatabase.getDatabase(this)
            // Získáme instanci naší Room databáze

            val skladDao = database.skladDao()
            val potravinaDao = database.potravinaDao()
            val codeDao = database.codeDao()
            val seznamDao = database.seznamDao()
            val nakupDao = database.nakupDao()
            val polozkyDao = database.polozkyDao()
            // Z databáze si vytáhneme DAO objekty pro tabulky (sklad, potravina)

            // Inicializace repository
            val skladRepository = SkladRepository(skladDao)
            val potravinaRepository = PotravinaRepository(potravinaDao)
            val codeRepository = CodeRepository(codeDao)
            val seznamRepository = SeznamRepository(seznamDao)
            val nakupRepository = NakupRepository(nakupDao)
            val polozkyRepository = PolozkyRepository(polozkyDao, applicationContext)

            // Inicializace ViewModelů
            val skladFactory = SkladViewModelFactory(application, skladRepository)
            skladViewModel = ViewModelProvider(this, skladFactory)[SkladViewModel::class.java]
            // Tady se vytvoří "skladViewModel" pomocí "SkladViewModelFactory".

            // ViewModelFactory pro Code (pokud chcete samostatný CodeViewModel)
            val codeFactory = CodeViewModelFactory(application, codeRepository)
            codeViewModel = ViewModelProvider(this, codeFactory)[CodeViewModel::class.java]

            val potravinaFactory = PotravinaViewModelFactory(application, potravinaRepository, codeRepository)
            potravinaViewModel = ViewModelProvider(this, potravinaFactory)[PotravinaViewModel::class.java]
            // Tady se vytvoří "potravinaViewModel" pomocí "PotravinaViewModelFactory".

            val seznamFactory = SeznamViewModelFactory(application, seznamRepository)
            seznamViewModel = ViewModelProvider(this, seznamFactory)[SeznamViewModel::class.java]

            val nakupFactory = NakupViewModelFactory(application, nakupRepository)
            nakupViewModel = ViewModelProvider(this, nakupFactory)[NakupViewModel::class.java]

            val polozkyFactory = PolozkyViewModelFactory(application, polozkyRepository)
            polozkyViewModel = ViewModelProvider(this, polozkyFactory)[PolozkyViewModel::class.java]


            // Zde už můžeme vykreslit naše composable (Jetpack Compose)
            AppTheme {
                MainScaffold(skladViewModel, potravinaViewModel,codeViewModel,seznamViewModel, nakupViewModel,polozkyViewModel)
                // V MainScaffold se vykresluje hlavní UI aplikace,
                // přičemž má přístup k oběma ViewModelům.
            }
        }
    }

    // Starší styl callbacku (nyní deprecated):
    @Deprecated("This method has been deprecated in favor of using the Activity Result API ...")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    )  {
        // Tato funkce se volá poté, co uživatel zareaguje na "requestPermissions"

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 123) {
            // Kontrolujeme, zda se jedná o námi zvolený requestCode
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Pokud byl uživatel udělen permission pro notifikace,
                // naplánujeme Worker
                NotificationUtils.scheduleNext(this)
            } else {
                // Povolení zamítnuto -> nic neplánujeme
                // nebo můžeme ukázat dialog, že je permission potřeba.
            }
        }
    }
}