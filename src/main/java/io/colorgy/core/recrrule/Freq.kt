package io.colorgy.core.recrrule

import org.threeten.bp.Period
import org.threeten.bp.temporal.TemporalAmount

enum class Freq{

    DAILY{
        override fun duration(): TemporalAmount = Period.ofDays(1)
    }, WEEKLY{
        override fun duration(): TemporalAmount = Period.ofWeeks(1)

    }, MONTHLY{
        override fun duration(): TemporalAmount = Period.ofMonths(1)

    }, YEARLY{
        override fun duration(): TemporalAmount = Period.ofYears(1)

    };

    abstract fun duration(): TemporalAmount


}
