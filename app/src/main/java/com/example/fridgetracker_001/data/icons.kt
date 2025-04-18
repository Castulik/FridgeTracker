package com.example.fridgetracker_001.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.fridgetracker_001.R

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


    val kindOptions = listOf(
        KindOption(R.string.kind_frozen,          R.drawable.kind_mrazene),
        KindOption(R.string.kind_nonperishable,   R.drawable.kind_trvanlive),
        KindOption(R.string.kind_fruit_veg,       R.drawable.kind_ovocezelenina),
        KindOption(R.string.kind_dairy,           R.drawable.kind_mlecne),
        KindOption(R.string.kind_meat_fish,       R.drawable.kind_masoryba),
        KindOption(R.string.kind_bakery,          R.drawable.kind_pecivo),
        KindOption(R.string.kind_eggs,            R.drawable.kind_vejce),
        KindOption(R.string.kind_grains_legumes,  R.drawable.kind_lusteniny),
        KindOption(R.string.kind_deli,            R.drawable.kind_lahudky),
        KindOption(R.string.kind_drinks,          R.drawable.kind_napoje),
        KindOption(R.string.kind_ready_meals,     R.drawable.kind_hotovajidla),
        KindOption(R.string.kind_other,           R.drawable.kind_ostatni)
    )
}

// Výchozí stav kategorií – všechny rozbalené
const val DEFAULT_CATEGORY_STATE = """{
  "Mražené": true,
  "Trvanlivé": true,
  "Ovoce a Zelenina": true,
  "Mléčné výrobky": true,
  "Maso a Ryby": true,
  "Pečivo": true,
  "Vejce": true,
  "Obiloviny a luštěniny": true,
  "Uzeniny a lahůdky": true,
  "Nápoje": true,
  "Hotová jídla": true,
  "Ostatní": true
}"""