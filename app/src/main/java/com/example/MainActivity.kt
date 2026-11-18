package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppLanguage
import com.example.navigation.TravelGuideApp
import com.example.ui.theme.TravelGuideTheme
import com.example.viewmodel.TravelViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TravelViewModel = viewModel()
            val language by viewModel.language.collectAsStateWithLifecycle()
            TravelGuideTheme(isHindi = language == AppLanguage.HINDI) {
                TravelGuideApp(viewModel = viewModel)
            }
        }
    }
}
