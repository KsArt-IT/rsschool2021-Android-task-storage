package ru.ksart.potatohandbook.model.data

import androidx.annotation.StringRes
import ru.ksart.potatohandbook.R

enum class Productivity(
    @StringRes val caption: Int
) {
    Na(R.string.na),
    High(R.string.yield_high_caption),
    Average(R.string.yield_average_caption),
    Low(R.string.yield_low_caption);
}
