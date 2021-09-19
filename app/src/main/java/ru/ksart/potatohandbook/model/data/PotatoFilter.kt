package ru.ksart.potatohandbook.model.data

data class PotatoFilter(
    val variety: PotatoVariety?,
    val ripening: PeriodRipening?,
    val productivity: Productivity?,
)
