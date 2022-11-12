package com.example.horizontalpicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.horizontalpicker.ui.theme.HorizontalPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorizontalPickerTheme {

                var selectedItem by remember {
                    mutableStateOf("")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Selected Item: $selectedItem", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(64.dp))
                    HorizontalPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.White),
                        items = (1..10).map { it.toString() },
                        visibleItemsCount = 6,
                        onValueSelectedListener = {
                            selectedItem = it
                        }
                    )
                }
            }
        }
    }
}