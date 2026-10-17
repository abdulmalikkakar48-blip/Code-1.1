package com.maree.app

import java.io.DataInputStream
import java.io.InputStream
import java.util.zip.GZIPInputStream

internal data class ImportedRegionMask(val width: Int, val height: Int, val regionCount: Int, val labels: IntArray) {
    fun regionAt(x: Int, y: Int): Int = if (x in 0 until width && y in 0 until height) labels[y * width + x] - 1 else -1
}

internal fun decodeImportedMask(input: InputStream, width: Int, height: Int, regions: Int): ImportedRegionMask {
    require(width in 1..2048 && height in 1..2048 && width.toLong() * height <= 4_194_304 && regions in 1..8192)
    return DataInputStream(GZIPInputStream(input).buffered(65_536)).use { stream ->
        require(stream.readInt() == 0x53434d31 && stream.readInt() == width && stream.readInt() == height && stream.readInt() == regions)
        val seen = BooleanArray(regions + 1)
        val labels = IntArray(width * height) { stream.readUnsignedShort().also { require(it in 0..regions); seen[it] = true } }
        require(stream.read() == -1)
        require((1..regions).all { seen[it] })
        ImportedRegionMask(width, height, regions, labels)
    }
}
