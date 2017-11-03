package com.gioppl.signature

/**
 * Created by GIOPPL on 2017/11/2.
 */

object ArrayOption {
    fun twoArray(pixels: IntArray, width: Int): Array<BooleanArray> {
        val b = Array(pixels.size / width) { BooleanArray(width) }
        var row = 0//记录行的数据
        var col = 0//记录列的数据
        for (i in pixels.indices) {
            if (col == b[row].size - 1) {
                if (pixels[i] != -1) {
                    b[row][col] = true
                } else {
                    b[row][col] = false
                }
                row++
                col = 0
            } else {
                if (pixels[i] != -1) {
                    b[row][col] = true
                } else {
                    b[row][col] = false
                }
                col++
            }
        }
        return b
    }

    fun split_row_right(b: Array<BooleanArray>): Int {
        var col_count = 0
        var row_count = 0
        for (i in b.size - 1 downTo 1) {
            for (j in b[i].size - 1 downTo 1) {
                if (b[i][j]) {
                    row_count++
                }
            }
            if (row_count == 0) {
                col_count++
            } else {
                return col_count
            }
        }
        return col_count
    }

    fun split_col_right(b: Array<BooleanArray>): Int {
        var col_count = 0
        var row_count = 0
        for (j in b[0].size - 1 downTo 1) {
            for (i in b.size - 1 downTo 1) {
                if (b[i][j]) {
                    col_count++
                }
            }
            if (col_count == 0) {
                row_count++
            } else {
                return row_count
            }
        }
        return row_count
    }

    //左边行的截取
    public fun split_row_left(b: Array<BooleanArray>): Int {
        var col_count = 0
        var row_count = 0
        for (i in 0..b.size - 1) {
            for (j in 0..b[i].size - 1)
                if (b[i][j])//true 表示这个颜色是黑色
                    row_count++
            if (row_count == 0) {
                col_count++
            } else {
                return col_count
            }
//            println()
        }
        return col_count
    }

    //左边列的截取
    public fun split_col_left(b: Array<BooleanArray>): Int {
        var col_count = 0
        var row_count = 0
        for (j in 0..b[0].size - 1) {
            for (i in 0..b.size - 1) {
                if (b[i][j]) {
                    col_count++
                }
            }
            if (col_count == 0) {
                row_count++
            } else {
                return row_count
            }
        }
        return row_count
    }
}
