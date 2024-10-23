package com.example.calendarsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calendarsample.ui.theme.CalendarSampleTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalendarSampleTheme {
                val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/dd\n(E)") }
                val scope = rememberCoroutineScope()
                val today = LocalDate.now()
                val state = rememberCalendarState(
                    initialDate = today,
                    startDate = today.minusMonths(1),
                    endDate = today.plusMonths(1),
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    state.setCurrentDate(date = LocalDate.now())
                                }
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                )
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        CalendarView(
                            state = state,
                            dayOfWeekContent = { date, isSelected ->
                                val bgColor = if (isSelected) {
                                    Color.Yellow
                                } else {
                                    Color.White
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 64.dp)
                                        .border(
                                            width = 1.dp,
                                            color = bgColor,
                                            shape = RoundedCornerShape(4.dp),
                                        )
                                        .clickable {
                                            scope.launch {
                                                state.setCurrentDate(date = date)
                                            }
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val color = when (date.dayOfWeek) {
                                        DayOfWeek.SATURDAY -> Color.Blue
                                        DayOfWeek.SUNDAY -> Color.Red
                                        else -> Color.Black
                                    }
                                    Text(
                                        text = date.format(dateFormatter),
                                        color = color,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            },
                            dateContent = { date ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                ) {
                                    Text(
                                        text = date.toString(),
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendarSampleTheme {
        Greeting("Android")
    }
}