package io.colorgy.core.recrrule

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

import java.util.HashMap

import io.colorgy.app.model.FreqAdapter

class RRuleStringBuilder {

    private val paramMap = HashMap<Param, String>()
    private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    private enum class Param {
        DTSTART, UNTIL, FREQ, INTERVAL, WKST
    }

    fun rrule(rrule: String): RRuleStringBuilder {
        val params = rrule.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i in params.indices) {
            val paramPair = params[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (paramPair[0] == PARAM_DTSTART) {
                paramMap.put(Param.DTSTART, paramPair[1])
            } else if (paramPair[0] == PARAM_FREQ) {
                paramMap.put(Param.FREQ, paramPair[1])
            } else if (paramPair[0] == PARAM_UNTIL) {
                paramMap.put(Param.UNTIL, paramPair[1])
            } else if (paramPair[0] == PARAM_WKST) {
                paramMap.put(Param.WKST, paramPair[1])
            } else if (paramPair[0] == PARAM_INTERVAL) {
                paramMap.put(Param.INTERVAL, paramPair[1])
            }
        }
        return this
    }

    fun dtStart(dtStart: LocalDateTime): RRuleStringBuilder {
        val dtStartFormat = dtStart.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
        val WKST = getWKSTByDate(dtStart)

        paramMap.put(Param.DTSTART, dtStartFormat)
        paramMap.put(Param.WKST, WKST)

        return this
    }

    fun until(until: LocalDateTime): RRuleStringBuilder {
        val untilFormat = until.format(formatter)
        paramMap.put(Param.UNTIL, untilFormat)

        return this
    }

    private fun getWKSTByDate(dateTime: LocalDateTime): String {
        val weekDay = dateTime.dayOfWeek
        return when (weekDay) {
            DayOfWeek.SUNDAY -> "SU"
            DayOfWeek.MONDAY -> "MO"
            DayOfWeek.TUESDAY -> "TU"
            DayOfWeek.WEDNESDAY -> "WE"
            DayOfWeek.THURSDAY -> "TH"
            DayOfWeek.FRIDAY -> "FR"
            else -> "SA"
        }
    }

    fun repeatIndex(repeatIndex: Int): RRuleStringBuilder {
        when (repeatIndex) {
            FreqAdapter.POS_DOUBLE_WEEKLY -> {
                paramMap.put(Param.FREQ, FREQS[repeatIndex])
                paramMap.put(Param.INTERVAL, "2")
            }
            FreqAdapter.POS_NEVER -> {
                paramMap.remove(Param.FREQ)
                paramMap.remove(Param.WKST)
                paramMap.remove(Param.DTSTART)
                paramMap.remove(Param.UNTIL)
                paramMap.remove(Param.INTERVAL)
            }
            else -> {
                paramMap.put(Param.FREQ, FREQS[repeatIndex])
                paramMap.remove(Param.INTERVAL)
            }
        }

        return this
    }

    fun build(): String {
        if (paramMap.size == 0)
            return ""

        val builder = StringBuilder()
                .append(PARAM_DTSTART).append("=").append(paramMap[Param.DTSTART]).append(";")
                .append(PARAM_UNTIL).append("=").append(paramMap[Param.UNTIL]).append(";")
                .append(PARAM_FREQ).append("=").append(paramMap[Param.FREQ]).append(";")

        if (paramMap[Param.INTERVAL] != null) {
            builder.append(PARAM_INTERVAL).append("=").append(paramMap[Param.INTERVAL]).append(";")
        }

        builder.append(PARAM_WKST).append("=").append(paramMap[Param.WKST])

        return builder.toString()
    }

    companion object {

        private val PARAM_DTSTART = "DTSTART"
        private val PARAM_UNTIL = "UNTIL"
        private val PARAM_FREQ = "FREQ"
        private val PARAM_INTERVAL = "INTERVAL"
        private val PARAM_WKST = "WKST"
        private val DATE_PATTERN = "yyyyMMdd'T'HHmmss'Z'"

        private val FREQS = arrayOf("DAILY", "WEEKLY", "WEEKLY", "MONTHLY", "YEARLY")
    }
}
