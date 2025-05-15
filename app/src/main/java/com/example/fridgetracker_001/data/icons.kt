package com.example.fridgetracker_001.data

import android.graphics.Color.RED
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.SeznamEntity


enum class SkladIcon(@DrawableRes val resId: Int) {
    LEDNICKA(R.drawable.lednicka),
    LEDNICE(R.drawable.lednice),
    LEDNICE2(R.drawable.lednice2),
    LEDNICE3(R.drawable.lednice3),
    LEDNICE4(R.drawable.lednice4),
    LEDNICE5(R.drawable.lednice5),
    MRAZAK(R.drawable.mrazak),
    MRAZAK2(R.drawable.mrazak2),
    SPIZ(R.drawable.spiz),
    SPIZ2(R.drawable.spiz2),
    SPIZ3(R.drawable.spiz3),
    LEDNICE_KRESLENA(R.drawable.lednicekreslena);

    companion object {
        fun fromName(name: String): SkladIcon =
            entries.find { it.name == name } ?: LEDNICKA  // fallback na výchozí ikonu
    }
}

enum class FoodIcon(@DrawableRes val resId: Int) {
    GENERIC(R.drawable.generic1),
    GENERIC2(R.drawable.generic2),
    GENERIC3(R.drawable.generic3),

    BEEF(R.drawable.food_beef),
    CUSTOM(R.drawable.food_custom),
    HRANOLKY(R.drawable.food_hranolky),
    KONZERVA(R.drawable.food_konzerva),
    KURE(R.drawable.food_kure),
    MASLO(R.drawable.food_maslo),
    MELOUN(R.drawable.food_meloun),
    MLEKO(R.drawable.food_mleko),
    MLEKO2(R.drawable.food_mleko2),
    MOZZARELA(R.drawable.food_mozzarela),
    MRKEV(R.drawable.food_mrkev),
    PIZZA(R.drawable.food_pizza),
    RYBA(R.drawable.food_ryba),
    RYZE(R.drawable.food_ryze),
    SANDWICH(R.drawable.food_sandwich),
    SLANINA(R.drawable.food_slanina),
    STEAK(R.drawable.food_steak),
    VAJICKO(R.drawable.food_vajicko),
    YOGURT(R.drawable.food_yogurt),
    ZMRZLINA(R.drawable.food_zmrzlina),
    BROKOLICE(R.drawable.food_brokolice),
    JAHODA(R.drawable.food_jahoda),
    OLIVOVY_OLEJ(R.drawable.food_olivovyolej),
    TOFU(R.drawable.food_tofu),
    FROZEN(R.drawable.food_frozen),

    GREEKSALAD(R.drawable.food_greeksalad),
    HAMBURGER(R.drawable.food_hamburger),
    HOTDOG(R.drawable.food_hotdog),
    LASAGNA(R.drawable.food_lasagna),
    NOODLES(R.drawable.food_noodles),
    OMELETTE(R.drawable.food_omelete),
    PALACINKY(R.drawable.food_palacinky),
    SALAD(R.drawable.food_salad),
    SPAGHETTI(R.drawable.food_spaghetti),
    SUSHI(R.drawable.food_sushi),

    PARKY(R.drawable.food_parky),
    SALAM(R.drawable.food_salam),

    CRAB(R.drawable.food_crab),
    KEBAB(R.drawable.food_kebab),
    LOSOS(R.drawable.food_losos),
    MLETEMASO(R.drawable.food_mletemaso),

    COCONUTMILK(R.drawable.food_coconutmilk),
    OATMILK(R.drawable.food_oatmilk),
    SYR(R.drawable.food_syr),

    COKOLADA(R.drawable.food_cokolada),
    COLA(R.drawable.food_cola),
    CUSTOM2(R.drawable.food_custom2),
    DZUS(R.drawable.food_dzus),
    HONEY(R.drawable.food_honey),
    HOUBY(R.drawable.food_houby),
    NUT(R.drawable.food_nut),
    OMACKA(R.drawable.food_omacka),
    SOYSAUCE(R.drawable.food_soysauce),
    WINE(R.drawable.food_wine),

    ANANAS(R.drawable.food_ananas),
    AVOCADO(R.drawable.food_avocado),
    BANAN(R.drawable.food_banan),
    BRAMBORY(R.drawable.food_brambory),
    CABBAGE(R.drawable.food_cabbage),
    CESNEK(R.drawable.food_cesnek),
    CIBULE(R.drawable.food_cibule),
    CITRON(R.drawable.food_citron),
    CORN(R.drawable.food_corn),
    GINGER(R.drawable.food_ginger),
    GRAPEFRUIT(R.drawable.food_grapefruit),
    GROUPVEGE(R.drawable.food_groupvege),
    HRUSKA(R.drawable.food_hruska),
    CHILLI(R.drawable.food_chilli),
    JABLKO(R.drawable.food_jablko),
    KIWI(R.drawable.food_kiwi),
    MANGO(R.drawable.food_mango),
    OKURKA(R.drawable.food_okurka),
    PAPRIKA(R.drawable.food_paprika),
    POMERANC(R.drawable.food_pomeranc),
    SCALLIONS(R.drawable.food_scallions),
    TOMATOES(R.drawable.food_tomatoes),

    BAGETA(R.drawable.food_bageta),
    CROISSANT(R.drawable.food_croissant),
    CHLEBA(R.drawable.food_chleba),
    PECIVOOBECNE(R.drawable.food_pecivoobecne),
    PRETZEL(R.drawable.food_pretzel),

    ARASIDMASLO(R.drawable.food_arasidmaslo),
    CEREAL(R.drawable.food_cereal),
    SUNFLOWEROIL(R.drawable.food_sunfloweroil),

    EGSSS(R.drawable.food_eggsss),
    VOLSKE(R.drawable.food_egsvolske);








    companion object {
        fun fromName(name: String): FoodIcon =
            entries.find { it.name == name } ?: CUSTOM
    }
}

object IconRegistry {
    val iconList = listOf(
        R.drawable.lednicka,
        R.drawable.lednice,
        R.drawable.lednice2,
        R.drawable.lednice3,
        R.drawable.lednice4,
        R.drawable.lednice5,
        R.drawable.mrazak,
        R.drawable.mrazak2,
        R.drawable.spiz,
        R.drawable.spiz2,
        R.drawable.spiz3,
        R.drawable.lednicekreslena,
    )

    val foodList = listOf(
        R.drawable.food_beef,
        R.drawable.food_custom,
        R.drawable.food_hranolky,
        R.drawable.food_konzerva,
        R.drawable.food_kure,
        R.drawable.food_maslo,
        R.drawable.food_meloun,
        R.drawable.food_mleko,
        R.drawable.food_mleko2,
        R.drawable.food_mozzarela,
        R.drawable.food_mrkev,
        R.drawable.food_pizza,
        R.drawable.food_ryba,
        R.drawable.food_ryze,
        R.drawable.food_sandwich,
        R.drawable.food_slanina,
        R.drawable.food_steak,
        R.drawable.food_vajicko,
        R.drawable.food_yogurt,
        R.drawable.food_zmrzlina,
        R.drawable.food_brokolice,
        R.drawable.food_jahoda,
        R.drawable.food_olivovyolej,
        R.drawable.food_tofu,
    )
}

enum class KindOptionEnum(
    val stringRes: Int,
    val imageRes: Int,
    val icons: List<FoodIcon> = emptyList()
) {
    NONPERISHABLE(
        R.string.kind_nonperishable,
        R.drawable.kind_trvanlive,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.KONZERVA, FoodIcon.RYZE, FoodIcon.TOFU, FoodIcon.OLIVOVY_OLEJ, FoodIcon.CEREAL, FoodIcon.SUNFLOWEROIL)
    ),
    FRUIT_VEG(
        R.string.kind_fruit_veg,
        R.drawable.kind_ovocezelenina,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.MELOUN, FoodIcon.JAHODA, FoodIcon.BROKOLICE, FoodIcon.MRKEV, FoodIcon.ANANAS, FoodIcon.AVOCADO, FoodIcon.BANAN, FoodIcon.BRAMBORY, FoodIcon.CABBAGE, FoodIcon.CESNEK, FoodIcon.CIBULE, FoodIcon.CITRON, FoodIcon.CORN, FoodIcon.GINGER, FoodIcon.GRAPEFRUIT, FoodIcon.GROUPVEGE, FoodIcon.HRUSKA, FoodIcon.CHILLI, FoodIcon.JABLKO, FoodIcon.KIWI, FoodIcon.MANGO, FoodIcon.OKURKA, FoodIcon.PAPRIKA, FoodIcon.POMERANC, FoodIcon.SCALLIONS, FoodIcon.TOMATOES)
    ),
    DAIRY(
        R.string.kind_dairy,
        R.drawable.kind_mlecne,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.MLEKO, FoodIcon.MLEKO2, FoodIcon.MASLO, FoodIcon.MOZZARELA, FoodIcon.YOGURT, FoodIcon.COCONUTMILK, FoodIcon.OATMILK, FoodIcon.SYR)
    ),
    MEAT_FISH(
        R.string.kind_meat_fish,
        R.drawable.kind_masoryba,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.BEEF, FoodIcon.STEAK, FoodIcon.SLANINA, FoodIcon.RYBA, FoodIcon.KURE, FoodIcon.CRAB, FoodIcon.KEBAB, FoodIcon.LOSOS, FoodIcon.MLETEMASO)
    ),
    BAKERY(
        R.string.kind_bakery,
        R.drawable.kind_pecivo,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.BAGETA, FoodIcon.CROISSANT, FoodIcon.CHLEBA, FoodIcon.PECIVOOBECNE, FoodIcon.PRETZEL)
    ),
    EGGS(
        R.string.kind_eggs,
        R.drawable.kind_vejce,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.VAJICKO, FoodIcon.EGSSS, FoodIcon.VOLSKE)
    ),
    GRAINS_LEGUMES(
        R.string.kind_grains_legumes,
        R.drawable.kind_lusteniny,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.RYZE, FoodIcon.TOFU)
    ),
    DELI(
        R.string.kind_deli,
        R.drawable.kind_lahudky,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.SLANINA, FoodIcon.PARKY, FoodIcon.SALAM)
    ),
    READY_MEALS(
        R.string.kind_ready_meals,
        R.drawable.kind_hotovajidla,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.PIZZA, FoodIcon.SANDWICH, FoodIcon.HRANOLKY, FoodIcon.LASAGNA, FoodIcon.NOODLES, FoodIcon.OMELETTE, FoodIcon.PALACINKY, FoodIcon.SALAD, FoodIcon.SPAGHETTI, FoodIcon.SUSHI, FoodIcon.GREEKSALAD, FoodIcon.HAMBURGER, FoodIcon.HOTDOG)
    ),
    OTHER(
        R.string.kind_other,
        R.drawable.kind_ostatni,
        listOf(FoodIcon.GENERIC3, FoodIcon.GENERIC2, FoodIcon.GENERIC, FoodIcon.CUSTOM, FoodIcon.ZMRZLINA, FoodIcon.COKOLADA, FoodIcon.COLA, FoodIcon.CUSTOM2, FoodIcon.DZUS, FoodIcon.HONEY, FoodIcon.HOUBY, FoodIcon.NUT, FoodIcon.OMACKA, FoodIcon.SOYSAUCE, FoodIcon.WINE)
    ),
    UNKNOWN(
        R.string.kind_unknown,
        R.drawable.minus
    );

    companion object {
        fun fromString(value: String): KindOptionEnum =
            entries.find { it.name == value } ?: UNKNOWN
    }
}



enum class SortOption(
    val displayNameResId: Int,
) {
    NAME(R.string.name,),
    QUANTITY(R.string.sort_by_quantity,),
    DEFAULT(R.string.defaults,)
}

enum class SortCategoryOption(
    val displayNameResId: Int,
) {
    ALPHABETICAL(R.string.alphabetical,),
    COUNT(R.string.count,),
    DEFAULT(R.string.defaults,)
}

data class DualColor(
    val x: androidx.compose.ui.graphics.Color,  // první barva
    val y: androidx.compose.ui.graphics.Color   // druhá barva
)

enum class ViewTypeNakup(val colors: DualColor) {
    YELLOW(DualColor(Color(0xFFF4D35E), Color(0xFFF5F5DC))),
    ORANGE(DualColor(Color(0xFFCE7836), Color(0xFFEFD1B9))),
    BLUE(DualColor(Color(0xFF1E5CB7), Color(0xFFC2E5F8)))
}

// Výchozí stav kategorií – všechny rozbalené
const val DEFAULT_CATEGORY_STATE = """{
  "FROZEN": true,
  "NONPERISHABLE": true,
  "FRUIT_VEG": true,
  "DAIRY": true,
  "MEAT_FISH": true,
  "BAKERY": true,
  "EGGS": true,
  "GRAINS_LEGUMES": true,
  "DELI": true,
  "DRINKS": true,
  "READY_MEALS": true,
  "OTHER": true
}"""