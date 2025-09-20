package com.example.smartlawyeragenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.smartlawyeragenda.data.AppDatabase
import com.example.smartlawyeragenda.ui.AppNavHost
import com.example.smartlawyeragenda.ui.theme.SmartLawyerAgendaTheme
import com.example.smartlawyeragenda.ui.theme.SmartLawyerAgendaThemeWithManager
import com.example.smartlawyeragenda.ui.theme.rememberThemeState
import com.example.smartlawyeragenda.utils.BackupManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartLawyerAgendaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartLawyerAgendaApp()
                }
            }
        }
    }
}

@Composable
fun SmartLawyerAgendaApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val backupManager = remember { BackupManager(context) }
    val themeState = rememberThemeState()
    
    SmartLawyerAgendaThemeWithManager(
        themeState = themeState
    ) {
        AppNavHost(
            database = database,
            backupManager = backupManager,
            themeState = themeState
        )
    }
}