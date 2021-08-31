package ru.ksart.potatohandbook.model.db

object PotatoContract {
    const val TABLE_NAME = "potatoes"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val IMAGE_URI = "image_uri"
    }
}