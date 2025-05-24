package cz.filip.fridgetracker_001.data

import androidx.room.TypeConverter

// Třída s převodními metodami (TypeConverters), které umožňují ukládání a načítání
// vlastních typů (např. enumů) do/z databáze Room jako String hodnoty.
class Converters {

    // Převod SortOption -> String pro uložení do databáze
    @TypeConverter
    fun fromSortOption(value: SortOption): String = value.name

    // Převod String -> SortOption při čtení z databáze
    @TypeConverter
    fun toSortOption(value: String): SortOption =
        SortOption.entries.firstOrNull { it.name == value } ?: SortOption.DEFAULT

    // Převod ViewTypeNakup -> String
    @TypeConverter
    fun fromViewType(value: ViewTypeNakup): String {
        return value.name
    }

    // Převod String -> ViewTypeNakup s fallbackem (výchozí hodnota YELLOW)
    @TypeConverter
    fun toViewType(value: String): ViewTypeNakup {
        return try {
            ViewTypeNakup.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ViewTypeNakup.YELLOW // fallback, pokud je hodnota neplatná
        }
    }

    // Převod SkladIcon -> String
    @TypeConverter
    fun fromSkladIcon(icon: SkladIcon): String = icon.name

    // Převod String -> SkladIcon pomocí statické metody fromName()
    @TypeConverter
    fun toSkladIcon(name: String): SkladIcon = SkladIcon.fromName(name)

    // Převod FoodIcon -> String
    @TypeConverter
    fun fromFoodIcon(icon: FoodIcon): String = icon.name

    // Převod String -> FoodIcon pomocí statické metody fromName()
    @TypeConverter
    fun toFoodIcon(name: String): FoodIcon = FoodIcon.fromName(name)
}
