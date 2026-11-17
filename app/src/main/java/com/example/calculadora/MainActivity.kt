package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {

    // Instancia del ViewModel acoplada al ciclo de vida de la Activity
    private val viewModel: CalculadoraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FondoPrincipal
                ) { innerPadding ->
                    CalculadoraScreen(
                        state = viewModel.state,
                        onAccion = viewModel::onAccion,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}