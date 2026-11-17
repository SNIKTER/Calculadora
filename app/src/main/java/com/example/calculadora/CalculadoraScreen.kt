package com.example.calculadora

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val FondoPrincipal = Color(0xFF000000)
val SuperficiePantalla = Color(0xFF000000)
val ColorAcentoNaranja = Color(0xFFFF9F0A)
val ColorAcentoVerde = Color(0xFF34C759)
val ColorBotonNum = Color(0xFF333333)
val ColorBotonFunc = Color(0xFFA5A5A5)
val ColorBotonCientifico = Color(0xFF262626)
val TextoBlancoPuro = Color(0xFFFFFFFF)
val TextoNegro = Color(0xFF000000)
val TextoGrisClaro = Color(0xFF8E8E93)

@Composable
fun CalculadoraScreen(
    state: CalculadoraState,
    onAccion: (CalculadoraAccion) -> Unit,
    modifier: Modifier = Modifier
) {
    val config = LocalConfiguration.current
    val esHorizontal = config.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FondoPrincipal)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Barra superior estilo iOS (Historial, Botón Calculadora Científica y Paso a Paso)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAccion(CalculadoraAccion.ToggleHistorial) }) {
                    Text("📜", fontSize = 22.sp)
                }

                // Botón central para activar/desactivar el teclado científico de la imagen
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (state.mostrarCientifica) ColorAcentoNaranja else ColorBotonCientifico,
                    modifier = Modifier.clickable { onAccion(CalculadoraAccion.ToggleCientifica) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🧮", fontSize = 14.sp)
                        Text(
                            text = if (state.mostrarCientifica) "Científica ON" else "Científica",
                            color = if (state.mostrarCientifica) TextoNegro else TextoBlancoPuro,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (state.pasosActuales.isNotEmpty()) {
                    TextButton(onClick = { onAccion(CalculadoraAccion.TogglePasos) }) {
                        Text("💡 Pasos", color = ColorAcentoVerde, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(36.dp))
                }
            }

            // Pantalla de resultados responsive
            PanelPantallaPro(state = state, modifier = Modifier.fillMaxWidth().weight(if (state.mostrarCientifica) 0.28f else 0.38f))

            // Teclado adaptativo (Muestra funciones científicas adicionales cuando está activado)
            PanelTecladoAdaptativo(mostrarCientifica = state.mostrarCientifica, onAccion = onAccion, modifier = Modifier.fillMaxWidth().weight(if (state.mostrarCientifica) 0.72f else 0.62f))
        }

        if (state.mostrarHistorial) {
            ModalHistorial(historial = state.historialList, onCerrar = { onAccion(CalculadoraAccion.ToggleHistorial) })
        }
        if (state.mostrarPasos) {
            ModalPasoAPaso(pasos = state.pasosActuales, onCerrar = { onAccion(CalculadoraAccion.TogglePasos) })
        }
    }
}

@Composable
fun PanelPantallaPro(state: CalculadoraState, modifier: Modifier = Modifier) {
    val textoExpr = (state.numero1 + (state.operacion?.simbolo?.let { " $it " } ?: "") + state.numero2).ifEmpty { "0" }

    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Text(text = state.historialExpr, color = TextoGrisClaro, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = textoExpr, color = TextoBlancoPuro, fontSize = if (textoExpr.length > 10) 38.sp else 54.sp, fontWeight = FontWeight.Light, maxLines = 2, textAlign = TextAlign.End)
    }
}

@Composable
fun PanelTecladoAdaptativo(mostrarCientifica: Boolean, onAccion: (CalculadoraAccion) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {

        // FILAS CIENTÍFICAS ADICIONALES (Se despliegan exactamente como en tu foto de referencia)
        if (mostrarCientifica) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonCientifico("π", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("PI")) }
                BotonCientifico("e", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("E")) }
                BotonCientifico("x²", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("X2")) }
                BotonCientifico("x³", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("X3")) }
            }
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonCientifico("√x", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("RAIZ2")) }
                BotonCientifico("∛x", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("RAIZ3")) }
                BotonCientifico("sin", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("SIN")) }
                BotonCientifico("cos", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("COS")) }
            }
            Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BotonCientifico("tan", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("TAN")) }
                BotonCientifico("log", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("LOG")) }
                BotonCientifico("ln", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("LN")) }
                BotonCientifico("1/x", ColorBotonCientifico, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.FuncionCientifica("INVERSA")) }
            }
        }

        // TECLADO ESTÁNDAR UNIVERSAL
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonCalc("AC", ColorBotonFunc, TextoNegro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Limpiar) }
            BotonCalc("⌫", ColorBotonFunc, TextoNegro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Borrar) }
            BotonCalc("%", ColorBotonFunc, TextoNegro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Porcentaje) }
            BotonCalc("÷", ColorAcentoNaranja, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Operador(Operacion.Division)) }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonCalc("7", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(7)) }
            BotonCalc("8", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(8)) }
            BotonCalc("9", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(9)) }
            BotonCalc("×", ColorAcentoNaranja, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Operador(Operacion.Multiplicacion)) }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonCalc("4", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(4)) }
            BotonCalc("5", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(5)) }
            BotonCalc("6", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(6)) }
            BotonCalc("−", ColorAcentoNaranja, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Operador(Operacion.Resta)) }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonCalc("1", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(1)) }
            BotonCalc("2", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(2)) }
            BotonCalc("3", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(3)) }
            BotonCalc("+", ColorAcentoNaranja, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Operador(Operacion.Suma)) }
        }
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonCalc("±", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.CambiarSigno) }
            BotonCalc("0", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Numero(0)) }
            BotonCalc(".", ColorBotonNum, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Decimal) }
            BotonCalc("=", ColorAcentoVerde, TextoBlancoPuro, Modifier.weight(1f)) { onAccion(CalculadoraAccion.Calcular) }
        }
    }
}

@Composable
fun BotonCalc(simbolo: String, fondo: Color, textColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(50.dp)).background(fondo).clickable { onClick() }
    ) {
        Text(text = simbolo, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

@Composable
fun BotonCientifico(simbolo: String, fondo: Color, textColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(20.dp)).background(fondo).clickable { onClick() }
    ) {
        Text(text = simbolo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
fun ModalHistorial(historial: List<HistorialItem>, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color(0xFF1C1C1E),
        title = { Text("Historial de Cálculos", color = TextoBlancoPuro) },
        text = {
            if (historial.isEmpty()) {
                Text("No hay registros en el historial todavía.", color = TextoGrisClaro)
            } else {
                LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(historial) { item ->
                        Column(modifier = Modifier.fillMaxWidth().background(ColorBotonNum, RoundedCornerShape(10.dp)).padding(10.dp)) {
                            Text(text = "[${item.categoria}]", color = ColorAcentoNaranja, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = item.expresion, color = TextoGrisClaro, fontSize = 13.sp)
                            Text(text = item.resultado, color = ColorAcentoVerde, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar", color = ColorAcentoNaranja) } }
    )
}

@Composable
fun ModalPasoAPaso(pasos: List<String>, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = Color(0xFF1C1C1E),
        title = { Text("💡 Resolución Paso a Paso", color = ColorAcentoVerde) },
        text = {
            LazyColumn(modifier = Modifier.height(300.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(pasos) { paso ->
                    Text(text = "• $paso", color = TextoBlancoPuro, fontSize = 13.sp)
                }
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido", color = ColorAcentoVerde) } }
    )
}