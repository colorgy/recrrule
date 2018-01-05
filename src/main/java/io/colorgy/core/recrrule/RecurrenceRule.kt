package io.colorgy.core.recrrule

import org.threeten.bp.LocalDateTime

class RecurrenceRule(val DTStart: LocalDateTime, val until: LocalDateTime?, val interval: Int, val freq: Freq, val wkst: WKST){

    companion object {

        @JvmStatic
        fun parse(rruleString: String): RecurrenceRule {
            return RecurrenceRuleParser(rruleString).parse()
        }
    }

    fun isInfinite(): Boolean = until == null

    fun iterator(date: LocalDateTime): RRuleMilliIterator {
        val rruleEventIterator = RRuleMilliIterator(this)
        rruleEventIterator.jumpAfter(date)
        return rruleEventIterator
    }
}
