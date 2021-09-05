package ru.ksart.potatohandbook.model.data

import androidx.annotation.StringRes
import ru.ksart.potatohandbook.R

enum class PotatoVariety(
    @StringRes val caption: Int
) {
    //    Столовый, Кормовой, Технический, Универсальный
    Na(R.string.na),
    Table(R.string.potato_variety_table_caption),
    Fodder(R.string.potato_variety_fodder_caption),
    Technical(R.string.potato_variety_technical_caption),
    Universal(R.string.potato_variety_universal_caption);
}
