package ru.ksart.potatohandbook.model.db

interface PotatoDatabase {
    fun potatoDao(): PotatoDao
}