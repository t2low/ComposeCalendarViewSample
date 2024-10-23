package com.example.calendarsample

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate

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