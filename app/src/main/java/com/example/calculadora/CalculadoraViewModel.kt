package com.example.calculadora

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import kotlin.math.*

class CalculadoraViewModel : ViewModel() {

    var state by mutableStateOf(CalculadoraState())
        private set

    private val df = DecimalFormat("#.########")

    fun onAccion(accion: CalculadoraAccion) {
        when (accion) {
            is CalculadoraAccion.Numero -> ingresarNumero(accion.numero)
            is CalculadoraAccion.Decimal -> ingresarDecimal()
            is CalculadoraAccion.Limpiar -> state = state.copy(numero1 = "", numero2 = "", operacion = null, historialExpr = "", pasosActuales = emptyList())
            is CalculadoraAccion.Operador -> ingresarOperacion(accion.operacion)
            is CalculadoraAccion.Calcular -> calcularResultado()
            is CalculadoraAccion.Borrar -> borrarUltimo()
            is CalculadoraAccion.CambiarSigno -> cambiarSigno()
            is CalculadoraAccion.Porcentaje -> aplicarPorcentaje()
            is CalculadoraAccion.ToggleHistorial -> state = state.copy(mostrarHistorial = !state.mostrarHistorial)
            is CalculadoraAccion.TogglePasos -> state = state.copy(mostrarPasos = !state.mostrarPasos)
            is CalculadoraAccion.ToggleCientifica -> state = state.copy(mostrarCientifica = !state.mostrarCientifica)
            is CalculadoraAccion.FuncionCientifica -> ejecutarFuncionCientifica(accion.funcion)
        }
    }

    private fun ingresarOperacion(operacion: Operacion) {
        if (state.numero1.isNotBlank() && state.numero1 != "Error") {
            if (state.numero2.isNotBlank()) {
                calcularResultado()
            }
            state = state.copy(operacion = operacion)
        }
    }

    private fun calcularResultado() {
        val num1 = state.numero1.toDoubleOrNull()
        val num2 = state.numero2.toDoubleOrNull()

        if (num1 != null && num2 != null && state.operacion != null) {
            val opSimbolo = state.operacion?.simbolo ?: ""
            val expresion = "${state.numero1} $opSimbolo ${state.numero2}"
            val pasos = mutableListOf<String>()

            pasos.add("1. Identificar operandos: a = $num1, b = $num2")
            pasos.add("2. Aplicar operación aritmética")

            val resultado = when (state.operacion) {
                is Operacion.Suma -> num1 + num2
                is Operacion.Resta -> num1 - num2
                is Operacion.Multiplicacion -> num1 * num2
                is Operacion.Division -> if (num2 != 0.0) num1 / num2 else Double.NaN
                is Operacion.Potencia -> num1.pow(num2)
                null -> return
            }

            val resStr = if (resultado.isNaN()) "Error" else df.format(resultado).replace(",", ".")
            pasos.add("3. Resultado obtenido: $resStr")

            val item = HistorialItem(expresion = "$expresion =", resultado = resStr, pasos = pasos, categoria = if (state.mostrarCientifica) "Científica" else "Básica")
            state = state.copy(numero1 = resStr, numero2 = "", operacion = null, historialExpr = "$expresion =", pasosActuales = pasos, historialList = listOf(item) + state.historialList)
        }
    }

    private fun ejecutarFuncionCientifica(fn: String) {
        val valorActual = state.numero1.toDoubleOrNull() ?: 0.0
        val pasos = mutableListOf<String>()
        val resultado: String

        when (fn) {
            "PI" -> {
                resultado = df.format(PI)
                pasos.add("Constante matemática Pi (π)")
                pasos.add("1. Valor aproximado cargado: $resultado")
            }
            "E" -> {
                resultado = df.format(E)
                pasos.add("Constante de Euler (e)")
                pasos.add("1. Valor aproximado cargado: $resultado")
            }
            "X2" -> {
                val res = valorActual.pow(2)
                resultado = df.format(res)
                pasos.add("Función Cuadrática (x²)")
                pasos.add("1. Elevar $valorActual al cuadrado: ${valorActual}² = $resultado")
            }
            "X3" -> {
                val res = valorActual.pow(3)
                resultado = df.format(res)
                pasos.add("Función Cúbica (x³)")
                pasos.add("1. Elevar $valorActual al cubo: ${valorActual}³ = $resultado")
            }
            "RAIZ2" -> {
                val res = sqrt(valorActual)
                resultado = if (res.isNaN()) "Error" else df.format(res)
                pasos.add("Raíz Cuadrada (√x)")
                pasos.add("1. Extraer raíz cuadrada de $valorActual = $resultado")
            }
            "RAIZ3" -> {
                val res = cbrt(valorActual)
                resultado = df.format(res)
                pasos.add("Raíz Cúbica (∛x)")
                pasos.add("1. Extraer raíz cúbica de $valorActual = $resultado")
            }
            "SIN" -> {
                val res = sin(Math.toRadians(valorActual))
                resultado = df.format(res)
                pasos.add("Trigonometría: Seno sin(x)")
                pasos.add("1. Convertir $valorActual° a radianes y calcular sin = $resultado")
            }
            "COS" -> {
                val res = cos(Math.toRadians(valorActual))
                resultado = df.format(res)
                pasos.add("Trigonometría: Coseno cos(x)")
                pasos.add("1. Convertir $valorActual° a radianes y calcular cos = $resultado")
            }
            "TAN" -> {
                val res = tan(Math.toRadians(valorActual))
                resultado = df.format(res)
                pasos.add("Trigonometría: Tangente tan(x)")
                pasos.add("1. Convertir $valorActual° a radianes y calcular tan = $resultado")
            }
            "LOG" -> {
                val res = log10(if (valorActual > 0) valorActual else 1.0)
                resultado = df.format(res)
                pasos.add("Logaritmo Base 10 log₁₀(x)")
                pasos.add("1. log₁₀($valorActual) = $resultado")
            }
            "LN" -> {
                val res = ln(if (valorActual > 0) valorActual else 1.0)
                resultado = df.format(res)
                pasos.add("Logaritmo Natural ln(x)")
                pasos.add("1. ln($valorActual) = $resultado")
            }
            "INVERSA" -> {
                val res = if (valorActual != 0.0) 1.0 / valorActual else Double.NaN
                resultado = if (res.isNaN()) "Error" else df.format(res)
                pasos.add("Inversa (1/x)")
                pasos.add("1. Calcular recíproco: 1 / $valorActual = $resultado")
            }
            else -> {
                resultado = valorActual.toString()
                pasos.add("Función ejecutada")
            }
        }

        val item = HistorialItem(expresion = "$fn($valorActual)", resultado = resultado, pasos = pasos, categoria = "Científica")
        state = state.copy(numero1 = resultado, historialExpr = "$fn($valorActual) =", pasosActuales = pasos, historialList = listOf(item) + state.historialList, mostrarPasos = true)
    }

    private fun borrarUltimo() {
        when {
            state.numero2.isNotBlank() -> state = state.copy(numero2 = state.numero2.dropLast(1))
            state.operacion != null -> state = state.copy(operacion = null)
            state.numero1.isNotBlank() -> {
                if (state.numero1 == "Error") state = CalculadoraState()
                else state = state.copy(numero1 = state.numero1.dropLast(1))
            }
        }
    }

    private fun ingresarDecimal() {
        if (state.operacion == null) {
            if (!state.numero1.contains(".") && state.numero1 != "Error") {
                state = state.copy(numero1 = if (state.numero1.isBlank()) "0." else "${state.numero1}.")
            }
        } else {
            if (!state.numero2.contains(".")) {
                state = state.copy(numero2 = if (state.numero2.isBlank()) "0." else "${state.numero2}.")
            }
        }
    }

    private fun ingresarNumero(n: Int) {
        if (state.numero1 == "Error") state = CalculadoraState()
        if (state.operacion == null) {
            if (state.numero1.length >= 12) return
            state = state.copy(numero1 = if (state.numero1 == "0") n.toString() else state.numero1 + n)
        } else {
            if (state.numero2.length >= 12) return
            state = state.copy(numero2 = if (state.numero2 == "0") n.toString() else state.numero2 + n)
        }
    }

    private fun cambiarSigno() {
        if (state.operacion == null && state.numero1.isNotBlank() && state.numero1 != "0") {
            val n = if (state.numero1.startsWith("-")) state.numero1.drop(1) else "-${state.numero1}"
            state = state.copy(numero1 = n)
        } else if (state.numero2.isNotBlank() && state.numero2 != "0") {
            val n = if (state.numero2.startsWith("-")) state.numero2.drop(1) else "-${state.numero2}"
            state = state.copy(numero2 = n)
        }
    }

    private fun aplicarPorcentaje() {
        val v = state.numero1.toDoubleOrNull() ?: return
        state = state.copy(numero1 = df.format(v / 100.0).replace(",", "."))
    }
}