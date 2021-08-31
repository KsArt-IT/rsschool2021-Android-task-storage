package ru.ksart.potatohandbook.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = PotatoContract.TABLE_NAME
)
data class Potato(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PotatoContract.Columns.ID)
    val id: Long = 0,
    @ColumnInfo(name = PotatoContract.Columns.NAME, index = true)
    val name: String,
    @ColumnInfo(name = PotatoContract.Columns.DESCRIPTION)
    val description: String,
    @ColumnInfo(name = PotatoContract.Columns.IMAGE_URI)
    val imageUri: String?,
)
