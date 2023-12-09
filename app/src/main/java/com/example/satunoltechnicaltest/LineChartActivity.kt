package com.example.satunoltechnicaltest

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.satunoltechnicaltest.ui.theme.SatunolTechnicalTestTheme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import kotlinx.coroutines.launch
import java.io.InputStream

class LineChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SatunolTechnicalTestTheme {
                LineChartScreen(csvInputStream = resources.openRawResource(R.raw.blood_pressure))
            }
        }
    }
}

@Composable
fun LineChartScreen(csvInputStream: InputStream) {
    var data1 by remember { mutableStateOf(emptyList<Entry>()) }
    var data2 by remember { mutableStateOf(emptyList<Entry>()) }
    var isVisible by remember { mutableStateOf(true) }
    var selectedPointDetails by remember { mutableStateOf("") }

    LaunchedEffect(csvInputStream) {
        val csvData = readCsvData(
            inputStream = csvInputStream, limit = 100
        )

        data1 = csvData.map { Entry(it.id, it.apiHi) }
        data2 = csvData.map { Entry(it.id, it.apiLo) }

        println("Data1: $data1")
        println("Data2: $data2")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Systole and Diastole Line Chart",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { isVisible = !isVisible }) {
                if (isVisible) {
                    Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
                } else {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }
        }

        if (isVisible) {
            LineChartView(data1 = data1, data2 = data2, onPointSelected = { details ->
                selectedPointDetails = details
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        PointDetails(selectedPointDetails = selectedPointDetails)
    }
}

@Composable
fun LineChartView(
    data1: List<Entry>, data2: List<Entry>, onPointSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val chart = remember { LineChart(context) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(chart) {
        // Setup chart properties
        with(chart) {
            description = Description().apply {
                text = "Blood Pressure Chart"
            }
            // Enable zooming
            setPinchZoom(true)
            setScaleEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(true)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            axisLeft.setDrawGridLines(true)
            axisRight.setDrawGridLines(true)

            // Add data to the chart
            val dataSet1 = LineDataSet(data1, "Systole").apply {
                setDrawCircles(true)
                setDrawValues(true)
                setDrawIcons(false)
                color = Color.BLUE
                lineWidth = 2f
                circleRadius = 5f
                valueTextSize = 20f
            }

            val dataSet2 = LineDataSet(data2, "Diastole").apply {
                setDrawCircles(true)
                setDrawValues(true)
                setDrawIcons(true)
                color = Color.RED
                lineWidth = 2f
                circleRadius = 5f
                valueTextSize = 20f
            }
            println("Systole: $dataSet1")
            println("Diastole: $dataSet2")

            val lineData = com.github.mikephil.charting.data.LineData(dataSet1, dataSet2)
            data = lineData

            // Set an OnChartGestureListener for handling interactions
            onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(
                    me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                    // Handle gesture start
                }

                override fun onChartGestureEnd(
                    me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                    // Handle gesture end
                }

                override fun onChartLongPressed(me: MotionEvent?) {
                    // Handle long press (e.g., show details)
                    val entry1 = getEntryByTouchPoint(me?.x ?: 0f, me?.y ?: 0f)
                    entry1?.let {
                        Toast.makeText(
                            context, "Value: ${it.y}", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChartDoubleTapped(me: MotionEvent?) {
                    // Handle double tap
                }

                override fun onChartSingleTapped(me: MotionEvent?) {
                    // Handle single tap
                    val entry = getEntryByTouchPoint(me?.x ?: 0f, me?.y ?: 0f)

                    if (entry != null) {
                        val details = "Value: ${entry.y} from Entry: ${entry.data}"
                        onPointSelected(details)
                    }
                }

                override fun onChartFling(
                    me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float
                ) {
                    // Handle fling
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                    // Handle scale
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                    // Handle translate
                }
            }
        }

        onDispose { /* Cleanup, if needed */ }
    }

    LaunchedEffect(chart) {
        coroutineScope.launch {
            chart.invalidate()
        }
    }

    AndroidView(
        factory = { chart }, modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    )
}

@Composable
fun PointDetails(selectedPointDetails: String) {
    // Customize the appearance of the details as needed
    Text(
        text = selectedPointDetails, fontSize = 14.sp, fontWeight = FontWeight.Bold
    )
}