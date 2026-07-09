package ru.den.writes.code.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.auroraos.kmp.pathInfo.PathInfo

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val path = "${PathInfo.getAppData()}/my_room.db"
    return Room.databaseBuilder<AppDatabase>(
        name = path,
    )
}
