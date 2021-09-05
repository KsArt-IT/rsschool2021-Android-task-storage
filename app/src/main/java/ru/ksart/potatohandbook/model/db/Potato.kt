package ru.ksart.potatohandbook.model.db

import androidx.room.*
import ru.ksart.potatohandbook.model.data.PeriodRipening
import ru.ksart.potatohandbook.model.data.PotatoVariety
import ru.ksart.potatohandbook.model.data.Productivity

@Entity(
    tableName = PotatoContract.TABLE_NAME,
    indices = [
        Index(
            PotatoContract.Columns.NAME,
            unique = true
        )
    ]
)
@TypeConverters(EnumConverter::class)
data class Potato(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PotatoContract.Columns.ID)
    val id: Long = 0,
    @ColumnInfo(name = PotatoContract.Columns.NAME)
    val name: String,
    @ColumnInfo(name = PotatoContract.Columns.DESCRIPTION)
    val description: String,
    @ColumnInfo(name = PotatoContract.Columns.IMAGE_URI)
    val imageUri: String?,
    @ColumnInfo(name = PotatoContract.Columns.IMAGE_URL)
    val imageUrl: String?,
    // -Разновидность картофеля: Столовый, Кормовой, Технический, Универсальный
    @ColumnInfo(name = PotatoContract.Columns.VARIETY)
    val variety: PotatoVariety = PotatoVariety.Na,
    // -Срок созревания: Ранний(40-50 дней), Среднеранний(55-65 дней), Среднеспелый(65-80 дней), Среднепоздний(80-100 дней), Поздний(Свыше 100 дней)
    @ColumnInfo(name = PotatoContract.Columns.RIPENING)
    val ripening: PeriodRipening = PeriodRipening.Na,
    // -Урожайность, Высокая, средняя, низкая и в количественном: Центнеров на гектар с возможность пересчета на тонн на гектар и Кг на сотку
    @ColumnInfo(name = PotatoContract.Columns.PRODUCTIVITY)
    val productivity: Productivity = Productivity.Na,
)
