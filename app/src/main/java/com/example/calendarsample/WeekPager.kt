package com.example.calendarsample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate

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
private fun WeekPagerPreview() {
    val today = LocalDate.now()
    val state = rememberCalendarState(
        initialDate = today,
        startDate = today.minusMonths(1),
        endDate = today.plusMonths(1),
    )
    WeekPager(
        state = state,
        dayOfWeekContent = { day, isSelected ->
            Text(
                text = "$day",
                color = if (isSelected) Color.Red else Color.Black,
            )
        }
    )
}
