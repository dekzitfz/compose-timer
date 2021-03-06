/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.util.concurrent.TimeUnit

@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp(TimerViewModel())
            }
        }
    }
}

class TimerViewModel : ViewModel() {
    val timerDisplayProgress = MutableLiveData("")
    val timerProgress = MutableLiveData(1.0)
    val isRunning = MutableLiveData(false)
    var timer: CountDownTimer? = null

    fun startTimer(timerTimeInMillis: Long) {
        isRunning.value = true
        timer = object : CountDownTimer(timerTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val display = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                timerDisplayProgress.value = display

                val remainingSeconds: Double = millisUntilFinished.toDouble() / 1000
                Log.d("TIMER", "remainingSeconds $remainingSeconds")
                val currentProgress: Double = remainingSeconds / (timerTimeInMillis / 1000)
                timerProgress.value = currentProgress
            }
            override fun onFinish() {
                isRunning.value = false
                timerProgress.value = 1.0
                timerDisplayProgress.value = ""
            }
        }.start()
    }

    fun endTimer() {
        isRunning.value = false
        timer?.cancel()
    }
}

@Composable
fun Progress(value: Double, displayTime: String) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = value.toFloat(),
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        )

        Text(text = displayTime)
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp(viewModel: TimerViewModel) {
    val isRunning: Boolean by viewModel.isRunning.observeAsState(false)
    val timerProgress: Double by viewModel.timerProgress.observeAsState(1.0)
    val timerDisplayProgress: String by viewModel.timerDisplayProgress.observeAsState("")

    val hour = remember { mutableStateOf("0") }
    val minute = remember { mutableStateOf("0") }
    val second = remember { mutableStateOf("0") }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {

                OutlinedTextField(
                    value = hour.value,
                    onValueChange = { hour.value = it },
                    label = { Text(text = "Hours") },
                    singleLine = true,
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false
                    )
                )
                Spacer(Modifier.width(4.dp))
                OutlinedTextField(
                    value = minute.value,
                    onValueChange = { minute.value = it },
                    label = { Text(text = "Minutes") },
                    singleLine = true,
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false
                    )
                )
                Spacer(Modifier.width(4.dp))
                OutlinedTextField(
                    value = second.value,
                    onValueChange = { second.value = it },
                    label = { Text(text = "Seconds") },
                    singleLine = true,
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val totalTime: Long = (second.value.toLong() * 1000) + (minute.value.toLong() * 60000) + (hour.value.toLong() * 3600000)

                    if (!isRunning) {
                        Log.d("TIMER", "timer should start")
                        viewModel.startTimer(totalTime)
                    } else {
                        viewModel.endTimer()
                        Log.d("TIMER", "timer should finish/cancel")
                    }
                }
            ) {
                if (isRunning) {
                    Text("Stop")
                } else {
                    Text("Start")
                }
            }

            Spacer(Modifier.height(18.dp))

            AnimatedVisibility(
                visible = isRunning,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Progress(timerProgress, timerDisplayProgress)
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp(TimerViewModel())
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp(TimerViewModel())
    }
}
