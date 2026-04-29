package ru.den.writes.code

class AuroraPlatform: Platform {
    override val name: String = "Аврора ОС"
}
actual fun getPlatform(): Platform = AuroraPlatform()