package com.example.fridgetracker_001.data

import android.graphics.Color.RED
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.fridgetracker_001.R
import com.example.fridgetracker_001.data.entities.SeznamEntity

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
        R.drawable.vlastni,
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
    )

    data class KindOption(@StringRes val nameRes: Int, @DrawableRes val imageRes: Int)

}

enum class KindOptionEnum(
    val stringRes: Int,
    val imageRes: Int
) {
    FROZEN(R.string.kind_frozen, R.drawable.kind_mrazene),
    NONPERISHABLE(R.string.kind_nonperishable, R.drawable.kind_trvanlive),
    FRUIT_VEG(R.string.kind_fruit_veg, R.drawable.kind_ovocezelenina),
    DAIRY(R.string.kind_dairy, R.drawable.kind_mlecne),
    MEAT_FISH(R.string.kind_meat_fish, R.drawable.kind_masoryba),
    BAKERY(R.string.kind_bakery, R.drawable.kind_pecivo),
    EGGS(R.string.kind_eggs, R.drawable.kind_vejce),
    GRAINS_LEGUMES(R.string.kind_grains_legumes, R.drawable.kind_lusteniny),
    DELI(R.string.kind_deli, R.drawable.kind_lahudky),
    DRINKS(R.string.kind_drinks, R.drawable.kind_napoje),
    READY_MEALS(R.string.kind_ready_meals, R.drawable.kind_hotovajidla),
    OTHER(R.string.kind_other, R.drawable.kind_ostatni),
    UNKNOWN(R.string.kind_unknown, R.drawable.minus);

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