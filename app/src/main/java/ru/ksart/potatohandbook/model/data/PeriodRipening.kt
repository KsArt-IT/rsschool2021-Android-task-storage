package ru.ksart.potatohandbook.model.data

import androidx.annotation.StringRes
import ru.ksart.potatohandbook.R

enum class PeriodRipening(
    @StringRes val caption: Int
) {
    //    -Срок созревания: Ранний(40-50 дней), Среднеранний(55-65 дней), Среднеспелый(65-80 дней), Среднепоздний(80-100 дней), Поздний(Свыше 100 дней)
    Na(R.string.na),
    Early(R.string.ripening_period_early_caption),
    MediumEarly(R.string.ripening_period_medium_early_caption),
    MediumRipe(R.string.ripening_period_medium_ripe_caption),
    MediumLate(R.string.ripening_period_medium_late_caption),
    Late(R.string.ripening_period_late_caption)
}
