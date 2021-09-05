package ru.ksart.potatohandbook.model.db

import androidx.room.TypeConverter
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity

class EnumConverter {
    @TypeConverter
    fun converterPotatoVarietyToString(enum: PotatoVariety): String = enum.name

    @TypeConverter
    fun converterStringToPotatoVariety(enumString: String): PotatoVariety {
        return try {
            PotatoVariety.valueOf(enumString)
        } catch (e: Exception) {
            PotatoVariety.Na
        }
    }

    @TypeConverter
    fun converterPeriodRipeningToString(enum: PeriodRipening): String = enum.name

    @TypeConverter
    fun converterStringToPeriodRipening(enumString: String): PeriodRipening {
        return try {
            PeriodRipening.valueOf(enumString)
        } catch (e: Exception) {
            PeriodRipening.Na
        }
    }

    @TypeConverter
    fun converterProductivityToString(enum: Productivity): String = enum.name

    @TypeConverter
    fun converterStringToProductivity(enumString: String): Productivity {
        return try {
            Productivity.valueOf(enumString)
        } catch (e: Exception) {
            Productivity.Na
        }
    }
}
