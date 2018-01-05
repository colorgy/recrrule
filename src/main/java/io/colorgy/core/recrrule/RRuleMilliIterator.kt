package io.colorgy.core.recrrule

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class RRuleMilliIterator(private val rrule: RecurrenceRule) : Iterator<Long>{

    private var currentEventTime: LocalDateTime = rrule.DTStart
    private var nextEventTime = rrule.DTStart

    fun jumpAfter(dateTime: LocalDateTime){
        currentEventTime = dateTime
        nextEventTime = dateTime
    }

    override fun hasNext(): Boolean {
        return if(rrule.until == null){
            true
        }else{
            rrule.until > nextEventTime
        }
    }

    override fun next(): Long {
        nextOffset()

        return currentEventTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
    }

    private fun nextOffset(){
        currentEventTime = LocalDateTime.from(nextEventTime)
        val interval = rrule.interval

        for (i in 1..interval){
            nextEventTime += rrule.freq.duration()
        }
    }

    private fun previousOffset(){
        nextEventTime = LocalDateTime.from(currentEventTime)
        val interval = rrule.interval

        for (i in 1..interval){
            currentEventTime -= rrule.freq.duration()
        }
    }

    fun skipAllButLast(){
        if(rrule.isInfinite()){
            //TODO OR just throw exception
            return
        }

        while(rrule.until!! > nextEventTime){
            nextOffset()
        }
        previousOffset()
    }
}
