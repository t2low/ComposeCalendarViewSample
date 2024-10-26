package com.example.calendarsample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalFoundationApi::class)
class CalendarState(
    initialDate: LocalDate,
    val info: Info,
    val weekPagerState: PagerState,
    val dayPagerState: PagerState,
) {
    data class Info(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val startDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
    ) {
        val startWeekDate = startDate.with(TemporalAdjusters.previousOrSame(startDayOfWeek))
        val endWeekDate = endDate.with(TemporalAdjusters.nextOrSame(startDayOfWeek)).minusDays(1)
        val totalWeeks = ChronoUnit.WEEKS.between(startWeekDate, endWeekDate) + 1
        val totalDays = ChronoUnit.DAYS.between(startWeekDate, endWeekDate) + 1
    }

    var currentDate by mutableStateOf(initialDate)
        private set

    suspend fun setCurrentDate(date: LocalDate) {
        val dateNumber = getDateNumber(date)
        setCurrentDate(dateNumber = dateNumber.toInt())
    }

    suspend fun setCurrentDate(dateNumber: Int) {
        if (dateNumber !in 0 until info.totalDays) {
            throw IllegalArgumentException()
        }
        val date = getDate(dayNumber = dateNumber.toLong())
        currentDate = date

        val weekNumber = getWeekNumber(date).toInt()
        if (weekPagerState.targetPage != weekNumber) {
            weekPagerState.animateScrollToPage(weekNumber)
        }
        if (dayPagerState.targetPage != dateNumber) {
            dayPagerState.animateScrollToPage(dateNumber)
        }
    }

    suspend fun setCurrentWeek(weekNumber: Int) {
        if (weekNumber !in 0 until info.totalWeeks) {
            return
        }

        val currentWeekNumber = getWeekNumber(currentDate).toInt()
        val nextDate = if (weekNumber == currentWeekNumber) {
            // 今週
            currentDate
        } else if (weekNumber > currentWeekNumber) {
            // 次週
            currentDate.with(TemporalAdjusters.next(info.startDayOfWeek))
        } else {
            // 前週
            currentDate.with(TemporalAdjusters.previousOrSame(info.startDayOfWeek)).minusDays(1)
        }
        val dateNumber = getDateNumber(nextDate)
        setCurrentDate(dateNumber = dateNumber.toInt())
    }

    fun getWeek(weekNumber: Long): List<LocalDate> {
        if (weekNumber !in 0 until info.totalWeeks) {
            throw IllegalArgumentException()
        }
        val startDate = info.startWeekDate.plusWeeks(weekNumber)
        return (0 until 7).map {
            startDate.plusDays(it.toLong())
        }
    }

    fun getDate(dayNumber: Long): LocalDate {
        if (dayNumber !in 0 until info.totalDays) {
            throw IllegalArgumentException()
        }
        return info.startWeekDate.plusDays(dayNumber)
    }

    fun getWeekNumber(date: LocalDate): Long {
        return ChronoUnit.WEEKS.between(info.startWeekDate, date)
    }

    fun getDateNumber(date: LocalDate): Long {
        return ChronoUnit.DAYS.between(info.startWeekDate, date)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberCalendarState(
    initialDate: LocalDate,
    startDate: LocalDate,
    endDate: LocalDate,
    startDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): CalendarState {

    val info = CalendarState.Info(
        startDate = startDate,
        endDate = endDate,
        startDayOfWeek = startDayOfWeek,
    )
    val initialWeekNumber = ChronoUnit.WEEKS.between(info.startWeekDate, initialDate)
    val weekPagerState = rememberPagerState(
        initialPage = initialWeekNumber.toInt(),
        pageCount = { info.totalWeeks.toInt() }
    )

    val initialDayNumber = ChronoUnit.DAYS.between(info.startWeekDate, initialDate)
    val dayPagerState = rememberPagerState(
        initialPage = initialDayNumber.toInt(),
        pageCount = { info.totalDays.toInt() },
    )

    return remember {
        CalendarState(
            initialDate = initialDate,
            info = info,
            weekPagerState = weekPagerState,
            dayPagerState = dayPagerState,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    state: CalendarState,
    modifier: Modifier = Modifier,
    dayOfWeekContent: @Composable (LocalDate, Boolean) -> Unit,
    dateContent: @Composable (LocalDate) -> Unit,
) {
    LaunchedEffect(state.dayPagerState.targetPage) {
        val page = state.dayPagerState.targetPage
        state.setCurrentDate(dateNumber = page)
    }
    LaunchedEffect(state.weekPagerState.targetPage) {
        val page = state.weekPagerState.targetPage
        state.setCurrentWeek(weekNumber = page)
    }

    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        WeekPager(
            state = state,
            dayOfWeekContent = dayOfWeekContent,
        )
        DayPager(
            state = state,
            pageContent = dateContent,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayPager(
    state: CalendarState,
    pageContent: @Composable (LocalDate) -> Unit,
) {
    HorizontalPager(
        state = state.dayPagerState,
        modifier = Modifier
            .fillMaxWidth(),
        pageContent = { page ->
            val date = state.getDate(dayNumber = page.toLong())
            pageContent(date)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekPager(
    state: CalendarState,
    dayOfWeekContent: @Composable (LocalDate, Boolean) -> Unit,
) {
    HorizontalPager(
        state = state.weekPagerState,
        modifier = Modifier
            .fillMaxWidth(),
        pageContent = { page ->
            val week = state.getWeek(weekNumber = page.toLong())
            Row {
                week.forEach { day ->
                    val isSelected = (day == state.currentDate)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        dayOfWeekContent(day, isSelected)
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun CalendarViewPreview() {
    val today = LocalDate.now()
    val state = rememberCalendarState(
        initialDate = today,
        startDate = today.minusMonths(1),
        endDate = today.plusMonths(1),
    )
    CalendarView(
        state = state,
        dayOfWeekContent = { day, isSelected ->
            Text(
                text = "$day",
                color = if (isSelected) Color.Red else Color.Black,
            )
        },
        dateContent = { date ->
            Text(
                text = "$date",
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    )
}