package io.colorgy.core.recrrule

import io.colorgy.core.recrrule.Freq
import io.colorgy.core.recrrule.RRuleParseException
import io.colorgy.core.recrrule.RecurrenceRuleParser
import io.colorgy.core.recrrule.WKST
import org.junit.Test

import org.junit.Assert.*
import org.threeten.bp.LocalDateTime


class RecurrenceRuleParserTest {

    @Test
    fun testParse() {
        val rruleString = "DTSTART=20150908T040000Z;UNTIL=20150930T160000Z;FREQ=WEEKLY;WKST=TU"
        val expectDTStart = LocalDateTime.of(2015, 9, 8, 4, 0)
        val expectUntil = LocalDateTime.of(2015, 9, 30, 16, 0)
        val expectInterval = 1
        val expectFreq = Freq.WEEKLY
        val expectWKST = WKST.TU

        val rrule = RecurrenceRuleParser(rruleString).parse()

        assertEquals(expectDTStart, rrule.DTStart)
        assertEquals(expectUntil, rrule.until)
        assertEquals(expectInterval, rrule.interval)
        assertEquals(expectFreq, rrule.freq)
        assertEquals(expectWKST, rrule.wkst)
    }

    @Test
    fun testParse_rrule_must_contains_DTSTART() {
        val rruleString = "UNTIL=20150930T160000Z;FREQ=WEEKLY;WKST=TU"
        val expectException = RRuleParseException("UNTIL=20150930T160000Z;FREQ=WEEKLY;WKST=TU missing DTSTART!")

        try {
            val rrule = RecurrenceRuleParser(rruleString).parse()
        }catch (exception: Exception){
            assertEquals(expectException.javaClass, exception.javaClass)
            assertEquals(expectException.message, exception.message)
        }
    }

    @Test
    fun testParse_rrule_must_contains_FREQ() {
        val rruleString = "DTSTART=20150908T040000Z;UNTIL=20150930T160000Z;WKST=TU"
        val expectException = RRuleParseException("DTSTART=20150908T040000Z;UNTIL=20150930T160000Z;WKST=TU missing FREQ!")

        try {
            val rrule = RecurrenceRuleParser(rruleString).parse()
        }catch (exception: Exception){
            assertEquals(expectException.javaClass, exception.javaClass)
            assertEquals(expectException.message, exception.message)
        }
    }

    @Test
    fun testParse_rrule_must_contains_WKST() {
        val rruleString = "DTSTART=20150908T040000Z;UNTIL=20150930T160000Z;FREQ=WEEKLY"
        val expectException = RRuleParseException("DTSTART=20150908T040000Z;UNTIL=20150930T160000Z;FREQ=WEEKLY missing WKST!")

        try {
            val rrule = RecurrenceRuleParser(rruleString).parse()
        }catch (exception: Exception){
            assertEquals(expectException.javaClass, exception.javaClass)
            assertEquals(expectException.message, exception.message)
        }
    }

    @Test
    fun testParse_rrule_isInfinite() {
        val rruleString = "DTSTART=20150908T040000Z;FREQ=WEEKLY;WKST=TU"

        val rrule = RecurrenceRuleParser(rruleString).parse()
        assertTrue(rrule.isInfinite())
    }
}