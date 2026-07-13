package ru.den.writes.code.data.local

import androidx.room.TypeConverter
import ru.den.writes.code.domain.model.TaskPriority

class PriorityConverter {
    @TypeConverter
    fun fromPriority(priority: TaskPriority): String = priority.name

    @TypeConverter
    fun toPriority(name: String): TaskPriority = TaskPriority.valueOf(name)
}
