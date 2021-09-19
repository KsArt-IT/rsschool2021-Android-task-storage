package ru.ksart.potatohandbook.model.db

object PotatoContract {
    const val TABLE_NAME = "potatoes"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val IMAGE_URI = "image_uri"
        const val IMAGE_URL = "image_url"
        const val VARIETY = "variety"
        const val RIPENING = "ripening"
        const val PRODUCTIVITY = "productivity"
    }
}
