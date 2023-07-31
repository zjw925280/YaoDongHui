package net.knowfx.yaodonghui.utils

import java.util.*
import kotlin.math.pow

/**
 * @ClassName: CheckIDCardRule
 * @Description: 身份证格式校验
 * @Author: Rain
 * @Version: 1.0
 */
object CheckIDCardRule {
    private val cityCode = arrayOf(
        "11", "12", "13", "14", "15", "21",
        "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42",
        "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
        "63", "64", "65", "71", "81", "82", "91"
    )

    fun checkIDCard(content: String): Boolean {
        if (content == "" || (content.length != 18 && content.length != 15)) {
            return false
        }
        return if (content.length == 15) {
            checkIdCard15(content)
        } else {
            checkIdCard18(content)
        }
    }

    /**
     * 18位身份证号 最后一位校验码 判断方法
     * 逻辑：
     * 1：身份证号前17位数分别乘不同的系数
     * 从第1位到17位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 2：将乘积之和除以11，余数可能为：0 1 2 3 4 5 6 7 8 9 10
     * 3：根据余数，分别对应最后一位身份证号：1 0 X 9 8 7 6 5 4 3 2
     * 余数与校验码对应关系：0:1,1:0,2:X,3:9,4:8,5:7,6:6,7:5,8:4,9:3:10:2
     *
     * @param content 需要校验的数据内容
     * @return 校验合法返回true，不合法返回false
     */
    private fun checkIdCard18(content: String): Boolean {
        val chars = content.toCharArray()
        val charsLength = chars.size - 1
        var count = 0
        for (i in 0 until charsLength) {
            val charI = chars[i].toString().toInt()
            count += (charI * (2.0.pow((17 - i).toDouble()) % 11)).toInt()
        }
        val idCard18 = chars[17].toString().uppercase(Locale.getDefault())
        val idCardLast: String = when (count % 11) {
            0 -> "1"
            1 -> "0"
            2 -> "X"
            else -> (12 - (count % 11)).toString() + ""
        }
        return idCard18 == idCardLast
    }

    /**
     * 检查15位身份证号是否合法
     */
    @Suppress("DEPRECATION")
    private fun checkIdCard15(content: String): Boolean {
        if (checkIsAllNumber(content)) {
            val provinceId = content.substring(0, 2)
            val year = content.substring(6, 8).toInt()
            val month = content.substring(8, 10).toInt()
            val day = content.substring(10, 12).toInt()

            if (!cityCode.contains(provinceId)) {
                return false
            }
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            if (Date().before(calendar.time)) {
                return false
            }

            val curDay = GregorianCalendar()
            val curYear = curDay.get(Calendar.YEAR) % 100

            if (year < 50 && year < curYear) {
                return false
            }

            if (month < 1 || month > 12) {
                return false
            }

            curDay.time = calendar.time

            return when (month) {
                1, 3, 5, 7, 8, 10, 12 -> {
                    day in 1..31
                }
                2 -> {
                    if (curDay.isLeapYear(curDay.get(Calendar.YEAR))) {
                        day in 1..29
                    } else {
                        day in 1..28
                    }
                }
                else -> {
                    day in 1..30
                }
            }

        } else {
            return false
        }
    }

    private fun checkIsAllNumber(content: String): Boolean {
        return content.matches(Regex("^\\d*$"))
    }
}