package com.example.myapplicationtemp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplicationtemp.ui.theme.MyApplicationTempTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTempTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PruebaModificacionLazy(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// 1. CLASE DE DATOS CORREGIDA (más usable)
data class Tarea(
    val id: Int,
    val nombreInicial: String, // Recibimos el valor inicial como String
    // Creamos el MutableState internamente con el valor inicial
    var nombre: MutableState<String> = mutableStateOf(nombreInicial)
)
// =====================================================================
// 2. COMPOSABLE PRINCIPAL (Vista de Prueba)
// =====================================================================

@Composable
fun PruebaModificacionLazy(modifier: Modifier = Modifier) {
    // Lista principal de datos: la fuente de la verdad para la LazyColumn.
    val listaTareas = remember {
        mutableStateListOf(
            Tarea(101, "Estudiar IDNP"),
            Tarea(102, "Investigar Compose"),
            Tarea(103, "Diseñar Mockup"),
            Tarea(104, "Revisar Apuntes")
        ).apply {
            // Inicializar el MutableState con el valor del constructor
            this.forEach { it.nombre.value = it.nombre.value }
        }
    }

    // Estados para el formulario de interacción
    var idTexto by remember { mutableStateOf("") }
    var nuevoNombreTexto by remember { mutableStateOf("") }
    var mensajeFeedback by remember { mutableStateOf("Ingrese ID y nuevo nombre.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- FORMULARIO DE INTERACCIÓN ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = idTexto,
                onValueChange = { idTexto = it.filter { char -> char.isDigit() } }, // Solo dígitos
                label = { Text("ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.3f),
                singleLine = true
            )
            OutlinedTextField(
                value = nuevoNombreTexto,
                onValueChange = { nuevoNombreTexto = it },
                label = { Text("Nuevo Nombre") },
                modifier = Modifier.weight(0.7f),
                singleLine = true
            )
        }

        Button(
            onClick = {
                val id = idTexto.toIntOrNull()
                val nuevoNombre = nuevoNombreTexto.trim()

                if (id != null && nuevoNombre.isNotEmpty()) {
                    val tareaEncontrada = listaTareas.find { it.id == id }
                    if (tareaEncontrada != null) {
                        // 💡 Dispara la Recomposición: Actualizamos el MutableState del objeto Tarea
                        tareaEncontrada.nombre.value = nuevoNombre
                        mensajeFeedback = "Tarea #${id} modificada a '${nuevoNombre}'."
                    } else {
                        mensajeFeedback = "Error: ID $id no encontrado."
                    }
                } else {
                    mensajeFeedback = "Error: Complete ID y Nuevo Nombre."
                }
                // Limpiar campos
                idTexto = ""
                nuevoNombreTexto = ""
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Modificar Nombre")
        }

        // Mensaje de feedback
        Text(
            text = mensajeFeedback,
            color = if (mensajeFeedback.startsWith("Error")) Color.Red else Color.Green,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Divider(Modifier.padding(vertical = 8.dp))

        // --- LISTA DINÁMICA (LazyColumn) ---
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaTareas, key = { it.id }) { tarea ->
                TareaItem(tarea)
            }
        }
    }
}

// =====================================================================
// 3. COMPOSABLE DE CADA ÍTEM (La Card observable)
// =====================================================================

@Composable
fun TareaItem(tarea: Tarea) {
    // 💡 Lectura del Estado: Solo esta línea se recompone cuando tarea.nombre cambia.
    val nombreActual = tarea.nombre.value

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ID (Fijo)
            Text(
                text = "ID: ${tarea.id}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )

            Spacer(Modifier.width(16.dp))

            // Nombre (La parte que se Recompone)
            Text(
                text = nombreActual, // Lee el estado observable
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Para usar este ejemplo, simplemente llama a PruebaRecomposicionLazy() en tu MainActivity.

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTempTheme {
        PruebaModificacionLazy(
            modifier = Modifier.padding(2.dp)
        )
    }
}