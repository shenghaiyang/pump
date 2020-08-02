@file:JvmName("Util")

package com.shenghaiyang.pump.gradle

import org.objectweb.asm.ClassReader
import java.nio.file.attribute.FileTime
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.zip.CRC32

/**
 * Pump utils
 *
 * @author shenghaiyang
 */

internal const val NAME_OBJECT = "java/lang/Object"

internal const val NAME_MAP = "java/util/Map"

internal const val NAME_LAYOUT_INFLATER = "android/view/LayoutInflater"

internal const val NAME_VIEW_GROUP = "android/view/ViewGroup"

internal const val NAME_VIEW_BINDING = "androidx/viewbinding/ViewBinding"

internal const val NAME_NON_NULL = "androidx/annotation/NonNull"

internal const val NAME_NULLABLE = "androidx/annotation/Nullable"

internal const val NAME_STORE = "com/shenghaiyang/pump/InflaterStore"

internal const val NAME_VIEW_BINDING_INFLATER = "com/shenghaiyang/pump/ViewBindingInflater"

internal const val NAME_METHOD_INIT = "<init>"

internal const val CLASS_FILE_SUFFIX = "class"

internal const val VIEW_BINDING_INFLATE_METHOD_NAME = "inflate"

internal const val INFLATER_METHOD_NAME = "inflate"

internal val FILE_TIME_ZERO = FileTime.fromMillis(0)

private const val INFLATER_NAME_SUFFIX = "Inflater"

internal fun generateInflaterName(bindingClassName: String): String = "$bindingClassName$INFLATER_NAME_SUFFIX"


/**
 * put new jar entry to the jar output stream
 *
 * @param name the name of new entry
 * @param content the content of new entry
 */
internal fun JarOutputStream.putNewEntry(name: String, content: ByteArray) {
    val newEntry = JarEntry(name)
    val crc32 = CRC32()
    crc32.update(content)
    newEntry.crc = crc32.value
    newEntry.method = JarEntry.STORED
    newEntry.size = content.size.toLong()
    newEntry.compressedSize = content.size.toLong()
    newEntry.lastAccessTime = FILE_TIME_ZERO
    newEntry.lastModifiedTime = FILE_TIME_ZERO
    newEntry.creationTime = FILE_TIME_ZERO
    this.putNextEntry(newEntry)
    this.write(content)
    this.closeEntry()
}

/**
 * check the class reader is view binding
 * @return true for view binding class, otherwise false
 */
internal fun ClassReader.isViewBindingClass() = this.interfaces.contains(NAME_VIEW_BINDING)

internal fun JarEntry.isClassEntry() = this.name.endsWith(CLASS_FILE_SUFFIX)
