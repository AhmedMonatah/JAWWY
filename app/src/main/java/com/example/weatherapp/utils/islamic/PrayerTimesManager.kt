package com.example.weatherapp.utils.islamic

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.text.SimpleDateFormat
import java.util.*

import com.example.weatherapp.R

data class PrayerTimeInfo(
    val nameResId: Int,
    val time: String,
    val isNext: Boolean = false
)

data class IslamicDateInfo(
    val day: Int,
    val monthResId: Int,
    val year: Int,
    val daysToRamadan: Int,
    val isRamadan: Boolean
)

object PrayerTimesManager {

    fun getPrayerTimes(lat: Double, lon: Double): List<PrayerTimeInfo> {
        val coordinates = Coordinates(lat, lon)
        val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        params.madhab = Madhab.SHAFI

        val date = DateComponents.from(Date())
        val prayerTimes = PrayerTimes(coordinates, date, params)

        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        
        val now = Date()
        val prayers = listOf(
            R.string.prayer_fajr to prayerTimes.fajr,
            R.string.prayer_sunrise to prayerTimes.sunrise,
            R.string.prayer_dhuhr to prayerTimes.dhuhr,
            R.string.prayer_asr to prayerTimes.asr,
            R.string.prayer_maghrib to prayerTimes.maghrib,
            R.string.prayer_isha to prayerTimes.isha
        )

        var nextIndex = prayers.indexOfFirst { it.second.after(now) }
        if (nextIndex == -1) nextIndex = 0

        return prayers.mapIndexed { index, pair ->
            PrayerTimeInfo(
                nameResId = pair.first,
                time = format.format(pair.second),
                isNext = index == nextIndex
            )
        }
    }

    fun getIslamicDateInfo(): IslamicDateInfo {
        val uCal = UmmalquraCalendar()
        
        val day = uCal.get(UmmalquraCalendar.DAY_OF_MONTH)
        val monthResId = getHijriMonthNameResId(uCal.get(UmmalquraCalendar.MONTH))
        val year = uCal.get(UmmalquraCalendar.YEAR)
        
        val currentMonth = uCal.get(UmmalquraCalendar.MONTH) 
        val isRamadan = currentMonth == 8 
        
        var daysToRamadan = 0
        if (!isRamadan) {
            val targetCal = UmmalquraCalendar()
            if (currentMonth >= 8) { 
                targetCal.set(UmmalquraCalendar.YEAR, uCal.get(UmmalquraCalendar.YEAR) + 1)
            }
            targetCal.set(UmmalquraCalendar.MONTH, 8) 
            targetCal.set(UmmalquraCalendar.DAY_OF_MONTH, 1)
            
            val diff = targetCal.timeInMillis - uCal.timeInMillis
            daysToRamadan = (diff / (1000 * 60 * 60 * 24)).toInt()
        }

        return IslamicDateInfo(day, monthResId, year, daysToRamadan, isRamadan)
    }

    private fun getHijriMonthNameResId(month: Int): Int {
        val months = arrayOf(
            R.string.month_1, R.string.month_2, R.string.month_3, R.string.month_4,
            R.string.month_5, R.string.month_6, R.string.month_7, R.string.month_8,
            R.string.month_9, R.string.month_10, R.string.month_11, R.string.month_12
        )
        return months.getOrElse(month) { R.string.month_1 }
    }
}
