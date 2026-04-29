package org.jetbrains.compose.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.posix.SEEK_SET
import platform.posix.closedir
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.opendir
import platform.posix.readdir
import ru.auroraos.kmp.pathInfo.PathInfo

internal actual fun getPlatformResourceReader(): ResourceReader = DefaultLinuxResourceReader

internal object DefaultLinuxResourceReader : ResourceReader {
    private val composeResourcesDir: String by lazy { findComposeResourcesPath() }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun read(path: String): ByteArray {
        val cleanPath = path.substringAfter(".generated.resources/")
        val res = try {
            PathInfo.getResourceBytes(cleanPath)
        } catch (e: Exception) {
            throw e
        }
        return res
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun readPart(
        path: String,
        offset: Long,
        size: Long
    ): ByteArray {
        val cleanPath = path.substringAfter(".generated.resources/")
        val res = try {
            PathInfo.getResourceBytes(cleanPath, offset, size)
        } catch (e: Exception) {
            throw e
        }
        return res
    }

    override fun getUri(path: String): String {
        return "file://" + getPathInBundle(path)
    }

    private fun getPathInBundle(path: String): String {
        return "$composeResourcesDir/$path"
    }

    private fun findComposeResourcesPath(): String {
        return PathInfo.getPathResources()
    }
}

internal actual val ProvidableCompositionLocal<ResourceReader>.currentOrPreview: ResourceReader
    @Composable
    get() = current

@OptIn(ExperimentalForeignApi::class)
fun PathInfo.getResourceBytes(
    path: String,
    offset: Long,
    size: Long
): ByteArray {
    val filePath = getPathResources() + "/${path.replace("/", "_")}"
    val file = fopen(filePath, "rb") ?: throw NoSuchElementException("Cannot open file: $filePath")

    try {
        if (size > Int.MAX_VALUE) {
            throw IllegalArgumentException("Requested size is too large for a single ByteArray")
        }

        if (fseek(file, offset, SEEK_SET) != 0) {
            throw IllegalArgumentException("Failed to seek to offset $offset in file: $filePath")
        }

        val bytes = ByteArray(size.toInt())
        if (size > 0L) {
            memScoped {
                val pinned = bytes.pin()
                try {
                    val bytesRead = fread(pinned.addressOf(0), 1u, size.toULong(), file).toLong()
                    if (bytesRead != size) {
                        throw IllegalArgumentException("Failed to read $size bytes from file (read $bytesRead bytes). Reached EOF?")
                    }
                } finally {
                    pinned.unpin()
                }
            }
        }

        return bytes
    } finally {
        fclose(file)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun PathInfo.listDirectoryContents(dirPath: String): List<String> {
    val dirPtr = opendir(dirPath)
        ?: throw IllegalArgumentException("Не удалось открыть папку: $dirPath")

    val contents = mutableListOf<String>()

    try {
        while (true) {
            val entry = readdir(dirPtr) ?: break
            val fileName = entry.pointed.d_name.toKString()
            if (fileName != "." && fileName != "..") {
                contents.add(fileName)
            }
        }
    } finally {
        closedir(dirPtr)
    }

    return contents
}