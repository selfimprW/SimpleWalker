package com.zhihu.walker.ext_walker

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.util.zip.ZipInputStream

/**
 * .aar 文件的遍历器，就是多一层 zip 解压
 */
object AarWalker : WalkerForASM() {

    private const val DOT_CLASS = ".class"

    private const val DOT_JAR = ".jar"

    override fun getExt(): String {
        return "aar"
    }

    override fun walk(aarFile: File) {
        if (!aarFile.name.endsWith(getExt())) {
            throw IllegalArgumentException("文件类型不是 aar：${aarFile.absoluteFile}")
        }
        ZipInputStream(FileInputStream(aarFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.endsWith(DOT_JAR)) {
                    walkJarInputStream(aarFile, zis)
                } else {
//                    Log.i("非 $DOT_JAR 文件不处理: ${entry.name}")
                }
                entry = zis.nextEntry
            }
        }
    }

    private fun walkJarInputStream(file: File, input: InputStream) {
        // 不 close
        ZipInputStream(input).let { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.endsWith(DOT_CLASS)) {
                    accept(file, zis)
                } else {
//                    Log.i("非 $DOT_CLASS 文件不处理: ${entry.name}")
                }
                entry = zis.nextEntry
            }
        }
    }

}