package ru.den.writes.code.data.local

import androidx.room.TypeConverter
import ru.den.writes.code.domain.model.TaskPriority

class PriorityConverter {
    @TypeConverter
    fun fromPriority(priority: TaskPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(name: String): TaskPriority {
        return TaskPriority.valueOf(name)
    }
}
