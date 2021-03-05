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
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme

@ExperimentalAnimationApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun Progress(value: Double) {
    CircularProgressIndicator(
        progress = value.toFloat(),
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
    )
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    val isRunning = remember { mutableStateOf(false) }
    val timerProgress = remember { mutableStateOf(1.0) }
    val timerTimeInMillis = remember { mutableStateOf(0L) }

    val hour = remember { mutableStateOf("0")}
    val minute = remember { mutableStateOf("0")}
    val second = remember { mutableStateOf("0")}

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
                    timerTimeInMillis.value = totalTime

                    val timer = object: CountDownTimer(timerTimeInMillis.value, 1000){
                        override fun onTick(millisUntilFinished: Long) {
                            val remainingSeconds: Double = millisUntilFinished.toDouble() / 1000
                            val currentProgress: Double = remainingSeconds/(timerTimeInMillis.value / 1000)
                            timerProgress.value = currentProgress
                        }
                        override fun onFinish() {
                            isRunning.value = false
                        }
                    }

                    isRunning.value = !isRunning.value
                    if(isRunning.value){
                        timer.start()
                    }else{
                        timerTimeInMillis.value = 0L
                        timerProgress.value = 1.0
                        timer.onFinish()
                        timer.cancel()
                    }
                }
            ) {
                if(isRunning.value){
                    Text("Stop")
                }else{
                    Text("Start")
                }
            }

            Spacer(Modifier.height(18.dp))

            AnimatedVisibility(
                visible = isRunning.value,
                enter = fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(animationSpec = tween(durationMillis = 500))
            ) {
                Progress(timerProgress.value)
            }
        }

    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
