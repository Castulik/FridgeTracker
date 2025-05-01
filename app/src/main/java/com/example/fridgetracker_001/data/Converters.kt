package com.example.fridgetracker_001.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSortOption(value: SortOption): String = value.name

    @TypeConverter
    fun toSortOption(value: String): SortOption =
        SortOption.entries.firstOrNull { it.name == value } ?: SortOption.DEFAULT

    @TypeConverter
    fun fromViewType(value: ViewTypeNakup): String {
        return value.name
    }
    @TypeConverter
    fun toViewType(value: String): ViewTypeNakup {
        return try {
            ViewTypeNakup.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ViewTypeNakup.YELLOW // fallback, kdyby někdo napsal nesmysl
        }
    }

    @TypeConverter
    fun fromSkladIcon(icon: SkladIcon): String = icon.name

    @TypeConverter
    fun toSkladIcon(name: String): SkladIcon = SkladIcon.fromName(name)

    @TypeConverter
    fun fromFoodIcon(icon: FoodIcon): String = icon.name

    @TypeConverter
    fun toFoodIcon(name: String): FoodIcon = FoodIcon.fromName(name)
}