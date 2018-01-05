package io.colorgy.core.recrrule

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

private const val PARAM_DTSTART = "DTSTART"
private const val PARAM_UNTIL = "UNTIL"
private const val PARAM_FREQ = "FREQ"
private const val PARAM_INTERVAL = "INTERVAL"
private const val PARAM_WKST = "WKST"

class RecurrenceRuleParser(private val rruleString: String){

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

    fun parse(): RecurrenceRule {
        val rulePairs = rruleString.split(";")
        val ruleMap = try {
            rulePairs.map { pairString -> pairString.split("=") }
                    .map { pair -> pair[0] to pair[1] }
                    .toMap()
        }catch (exception: IndexOutOfBoundsException){
            throw RRuleParseException("invalid rrule format: $rruleString")
        }

        val dTStart = parseDTStart(ruleMap[PARAM_DTSTART])
        val until = parseUntil(ruleMap[PARAM_UNTIL])
        val freq = parseFreq(ruleMap[PARAM_FREQ])
        val interval = parseInterval(ruleMap[PARAM_INTERVAL])
        val wkst = parseWKST(ruleMap[PARAM_WKST])

        return RecurrenceRule(dTStart, until, interval, freq, wkst)
    }

    private fun parseUntil(until: String?): LocalDateTime? {
        return until?.let {
            LocalDateTime.parse(it, dateFormatter)
        }
    }

    private fun parseWKST(wkst: String?): WKST {
        return ensureNotNull(wkst, PARAM_WKST).let {
            when(it){
                "MO" -> WKST.MO
                "TU" -> WKST.TU
                "WE" -> WKST.WE
                "TH" -> WKST.TH
                "FR" -> WKST.FR
                "SA" -> WKST.SA
                "SU" -> WKST.SU
                else -> throw RRuleParseException("Undefined WKST: $it")
            }
        }
    }


    private fun parseInterval(interval: String?): Int {
        return interval?.toInt() ?: 1
    }

    private fun parseFreq(freq: String?): Freq {
        return ensureNotNull(freq, PARAM_FREQ).let {
            when(it){
                "WEEKLY" -> Freq.WEEKLY
                "DAILY" -> Freq.DAILY
                "MONTHLY" -> Freq.MONTHLY
                "YEARLY" -> Freq.YEARLY
                else -> throw RRuleParseException("Undefined Freq: $it")
            }
        }
    }

    private fun parseDTStart(dtStart: String?): LocalDateTime{
        return ensureNotNull(dtStart, PARAM_DTSTART).let {
            LocalDateTime.parse(it, dateFormatter)
        }
    }

    private fun ensureNotNull(verifyString: String?, key: String): String{
        return verifyString ?: throw RRuleParseException("$rruleString missing $key!")
    }

}
