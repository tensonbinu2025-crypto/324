package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.FootballEliteMainApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: AppViewModel by viewModels {
    val app = application as FootballEliteApplication
    AppViewModelFactory(app.repository)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FootballEliteMainApp(viewModel)
      }
    }
  }
}
