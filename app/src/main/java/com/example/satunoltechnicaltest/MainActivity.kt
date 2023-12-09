package com.example.satunoltechnicaltest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.satunoltechnicaltest.ui.theme.SatunolTechnicalTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SatunolTechnicalTestTheme {
                // A surface container using the 'background' color from the theme
                Surface {
                    val context = LocalContext.current
                    val navToOtherActivity = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        // Handle the result if needed
                        if (result.resultCode == RESULT_OK) {
                            // Handle success
                        } else {
                            // Handle failure or other scenarios
                        }
                    }

                    MainActivityScreen(
                        navToOtherActivity = navToOtherActivity,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(navToOtherActivity: ActivityResultLauncher<Intent>, context: Context) {
    val desc = "The purpose of this app is to complete the technical test given " +
            "by PT Satunol Mikro Sistem for the position of Android Application Developer. " +
            "My name is Hisyam Fariqi and I chose number 3 from the task list."

    val desc2 = "In this app I use the MPAndroidChart dependency to show " +
            "the Line Chart Diagram as instructed in the task explanation. " +
            "I use blood pressure dataset that I got from kaggle.com and " +
            "used 3 columns from the dataset. The columns are: id for the patient id, " +
            "api_hi for systole, and api_lo for diastole. The dataset is in CSV format " +
            "and I use opencsv dependency to read the csv file. So the X axis indicates " +
            "the patient ID, and the Y axis indicates the systole that is in blue color" +
            " and diastole that is in red color."

    val desc3 = "The user is able to zoom in/out the chart and tap on one of the point " +
            "to show the details of the point. To see the result, press the button below " +
            "and then press the icon at top right twice. Thank you."
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Satunol Technical Test", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = desc)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = desc2)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = desc3)
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = Modifier
            .fillMaxWidth(),
            onClick = {
                // Launch another activity when the button is clicked
                val intent = Intent(context, LineChartActivity::class.java)
                navToOtherActivity.launch(intent)
            }
        ) {
            Text("Open Line Chart")
        }
    }
}