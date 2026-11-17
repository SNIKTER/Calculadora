package com.example.calculadora

data class HistorialItem(
    val id: Long = System.currentTimeMillis(),
    val expresion: String,
    val resultado: String,
    val pasos: List<String>,
    val categoria: String
)

data class CalculadoraState(
    val numero1: String = "",
    val numero2: String = "",
    val operacion: Operacion? = null,
    val historialExpr: String = "",
    val mostrarHistorial: Boolean = false,
    val mostrarPasos: Boolean = false,
    val mostrarCientifica: Boolean = false, // Controla si se muestra el teclado científico completo
    val pasosActuales: List<String> = emptyList(),
    val historialList: List<HistorialItem> = emptyList()
)

sealed class Operacion(val simbolo: String) {
    object Suma : Operacion("+")
    object Resta : Operacion("−")
    object Multiplicacion : Operacion("×")
    object Division : Operacion("÷")
    object Potencia : Operacion("^")
}

sealed class CalculadoraAccion {
    data class Numero(val numero: Int) : CalculadoraAccion()
    object Limpiar : CalculadoraAccion()
    object Borrar : CalculadoraAccion()
    object Decimal : CalculadoraAccion()
    object Calcular : CalculadoraAccion()
    object CambiarSigno : CalculadoraAccion()
    object Porcentaje : CalculadoraAccion()
    data class Operador(val operacion: Operacion) : CalculadoraAccion()
    object ToggleHistorial : CalculadoraAccion()
    object TogglePasos : CalculadoraAccion()
    object ToggleCientifica : CalculadoraAccion() // Abre/Cierra el teclado científico
    data class FuncionCientifica(val funcion: String) : CalculadoraAccion()
}