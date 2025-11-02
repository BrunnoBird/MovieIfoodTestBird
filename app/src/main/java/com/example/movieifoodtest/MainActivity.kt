package com.example.movieifoodtest

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.movieifoodtest.navigation.NavigationRoot
import com.example.movieifoodtest.ui.theme.MovieIfoodTestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK)
        )
        setContent {
            MovieIfoodTestTheme {
                MovieIfoodTestApp()
            }
        }
    }
}

@Composable
fun MovieIfoodTestApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavigationRoot(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            onToggleFailure = { message ->
                scope.launch {
                    snackbarHostState.showSnackbar("Ocorreu uma falha. $message")
                }
            },
            onToggleSuccess = {
                scope.launch {
                    snackbarHostState.showSnackbar("Operação realizada com sucesso!")
                }
            }
        )
    }
}
