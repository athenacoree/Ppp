package com.ppp.mindhouse

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF282638)
private val Cream = Color(0xFFFFF7F0)
private val Lilac = Color(0xFFB9A4E8)
private val Rose = Color(0xFFE9A9B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(35, 31, 48)
        window.navigationBarColor = android.graphics.Color.rgb(35, 31, 48)
        setContent { MindHouseApp(this) }
    }
}

private data class StoryLine(val speaker: String, val text: String, val options: List<String> = emptyList())

@Composable
private fun MindHouseApp(context: Context) {
    val prefs = remember { context.getSharedPreferences("mindhouse_save", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf(if (prefs.getBoolean("started", false)) "story" else "title") }
    var day by remember { mutableIntStateOf(prefs.getInt("day", 1)) }
    var choice by remember { mutableIntStateOf(prefs.getInt("choice", 0)) }
    var outfit by remember { mutableIntStateOf(prefs.getInt("outfit", 0)) }
    var lineIndex by remember { mutableIntStateOf(prefs.getInt("line", 0).coerceIn(0, 5)) }
    var calm by remember { mutableIntStateOf(prefs.getInt("calm", 2)) }

    val lines = remember(day, choice) {
        if (day == 1) listOf(
            StoryLine("...", "El despertador suena a las 6:42. No reconoces el techo."),
            StoryLine("MAMÁ", "¡Arriba! Vas a llegar tarde otra vez. Te dejé el desayuno."),
            StoryLine("TÚ", "¿Otra vez...? Mi voz suena igual, pero el nombre en el uniforme no es el mío."),
            StoryLine("LUCÍA", "Buenos días. ¿Dormiste bien? Tienes una cara rarísima.", listOf("Decir que sí y sonreír.", "Preguntarle quién es.", "No responder todavía.")),
            StoryLine("LUCÍA", if (choice == 1) "Soy tu hermana, genio. ¿Te golpeaste la cabeza?" else "Ah... vale. Nos vemos abajo.", listOf("Bajar a desayunar.", "Mirar el cuarto un poco más.")),
            StoryLine("...", "En el escritorio hay una foto escolar. Alguien ha dibujado un pequeño círculo alrededor de una chica.")
        ) else listOf(
            StoryLine("...", "Día $day. La casa parece igual, aunque algo en ella se siente distinto."),
            StoryLine("MAMÁ", "No olvides tus cosas para la escuela."),
            StoryLine("TÚ", "Intentas recordar qué se supone que debes hacer hoy."),
            StoryLine("LUCÍA", "¿Vienes? Te están esperando.", listOf("Ir con ella.", "Inventar una excusa.", "Preguntar por la chica de la foto.")),
            StoryLine("...", "Por un instante, alguien te observa desde el otro lado de la calle."),
            StoryLine("...", "El día continúa. Algunas respuestas tendrán que esperar.")
        )
    }
    val current = lines[lineIndex.coerceIn(0, lines.lastIndex)]

    LaunchedEffect(day, choice, outfit, lineIndex, calm) {
        prefs.edit().putBoolean("started", page != "title").putInt("day", day)
            .putInt("choice", choice).putInt("outfit", outfit)
            .putInt("line", lineIndex).putInt("calm", calm).apply()
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Cream) {
            AnimatedContent(page, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "screen") { screen ->
                when (screen) {
                    "title" -> TitleScreen(
                        onStart = { page = "story"; lineIndex = 0 },
                        onWardrobe = { page = "wardrobe" }
                    )
                    "wardrobe" -> WardrobeScreen(outfit, onSelect = { outfit = it }, onBack = { page = "title" })
                    else -> StoryScreen(
                        day = day, outfit = outfit, line = current,
                        onChoice = { selected ->
                            choice = selected
                            calm = (calm + if (selected == 0) 1 else 0).coerceIn(0, 5)
                            if (lineIndex < lines.lastIndex) lineIndex++ else {
                                day++
                                lineIndex = 0
                            }
                        },
                        onNext = {
                            if (current.options.isEmpty()) {
                                if (lineIndex < lines.lastIndex) lineIndex++ else { day++; lineIndex = 0 }
                            }
                        },
                        onWardrobe = { page = "wardrobe" }
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleScreen(onStart: () -> Unit, onWardrobe: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Color(0xFF292638)).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("THE QUIET ROOM", color = Cream, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        Text("A story that doesn't tell you everything.", color = Color(0xFFD4C9E8), fontSize = 13.sp)
        Spacer(Modifier.height(30.dp))
        CharacterPortrait(0, 0, Modifier.fillMaxWidth().height(245.dp))
        Spacer(Modifier.height(30.dp))
        Button(onClick = onStart, colors = ButtonDefaults.buttonColors(containerColor = Lilac), modifier = Modifier.fillMaxWidth()) {
            Text("COMENZAR HISTORIA", color = Ink, fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onWardrobe) { Text("PERSONALIZAR PERSONAJE", color = Cream) }
        Spacer(Modifier.height(12.dp))
        Text("Capítulo 1 · Una mañana cualquiera", color = Color(0xFFBDB5CC), fontSize = 11.sp)
    }
}

@Composable
private fun StoryScreen(day: Int, outfit: Int, line: StoryLine, onChoice: (Int) -> Unit, onNext: () -> Unit, onWardrobe: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF7EEF1))) {
        Row(Modifier.fillMaxWidth().background(Color(0xFF292638)).padding(horizontal = 18.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("DÍA $day  ·  6:42 AM", color = Cream, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("ARMARIO", color = Lilac, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onWardrobe() })
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            BedroomScene(outfit, Modifier.fillMaxSize())
            Text("HOGAR  /  DORMITORIO", Modifier.align(Alignment.TopStart).padding(14.dp).background(Color(0xAA292638), RoundedCornerShape(8.dp)).padding(9.dp), color = Cream, fontSize = 10.sp)
        }
        Column(Modifier.fillMaxWidth().background(Color(0xFF292638)).padding(horizontal = 18.dp, vertical = 16.dp)) {
            Text(line.speaker, color = Rose, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(6.dp))
            Text(line.text, color = Cream, fontSize = 16.sp, lineHeight = 23.sp)
            Spacer(Modifier.height(14.dp))
            if (line.options.isNotEmpty()) {
                line.options.forEachIndexed { index, option ->
                    OutlinedButton(onClick = { onChoice(index) }, modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Cream)) {
                        Text(option, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Lilac), shape = RoundedCornerShape(12.dp)) {
                    Text("CONTINUAR  →", color = Ink, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun BedroomScene(outfit: Int, modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFFE8DCEB))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawRect(Color(0xFFEADDEB), size = Size(w, h))
            drawRect(Color(0xFFD7C6DE), topLeft = Offset(0f, h * .72f), size = Size(w, h * .28f))
            drawRect(Color(0xFFB6C5D9), topLeft = Offset(w * .08f, h * .12f), size = Size(w * .32f, h * .30f))
            drawRect(Color(0xFFFFEBC2), topLeft = Offset(w * .095f, h * .135f), size = Size(w * .29f, h * .27f))
            drawLine(Color.White, Offset(w * .24f, h * .13f), Offset(w * .24f, h * .40f), 5f)
            drawLine(Color.White, Offset(w * .09f, h * .27f), Offset(w * .38f, h * .27f), 5f)
            drawRoundRect(Color(0xFF9C7E9F), Offset(w * .48f, h * .51f), Size(w * .43f, h * .27f), androidx.compose.ui.geometry.CornerRadius(22f))
            drawRoundRect(Color(0xFFFFF4F0), Offset(w * .50f, h * .48f), Size(w * .40f, h * .12f), androidx.compose.ui.geometry.CornerRadius(18f))
            drawRoundRect(Color(0xFF8B7298), Offset(w * .53f, h * .63f), Size(w * .35f, h * .18f), androidx.compose.ui.geometry.CornerRadius(14f))
            drawRoundRect(Color(0xFF8C6D56), Offset(w * .05f, h * .60f), Size(w * .24f, h * .22f), androidx.compose.ui.geometry.CornerRadius(8f))
            drawCircle(Color(0xFFFFD9C8), radius = w * .075f, center = Offset(w * .29f, h * .61f))
            val clothes = listOf(Color(0xFF8E85C8), Color(0xFF6F9FA0), Color(0xFFE28C9E), Color(0xFF303747))
            drawRoundRect(clothes[outfit % clothes.size], Offset(w * .22f, h * .67f), Size(w * .14f, h * .17f), androidx.compose.ui.geometry.CornerRadius(18f))
            drawCircle(Color(0xFF382D38), w * .08f, Offset(w * .29f, h * .55f))
            drawCircle(Color(0xFFFFF8F2), w * .012f, Offset(w * .315f, h * .61f))
            drawCircle(Color(0xFF382D38), w * .006f, Offset(w * .315f, h * .61f))
            drawLine(Color(0xFF6B4A58), Offset(w * .30f, h * .64f), Offset(w * .32f, h * .645f), 2f)
            // Small details make the room feel lived in.
            drawCircle(Color(0xFFFFD66B), w * .018f, Offset(w * .80f, h * .18f))
            drawLine(Color(0xFF77627F), Offset(w * .80f, h * .20f), Offset(w * .80f, h * .34f), 3f)
        }
    }
}

@Composable
private fun CharacterPortrait(outfit: Int, character: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cx = size.width / 2
        val cy = size.height * .48f
        val hair = if (character == 0) Color(0xFF342A35) else Color(0xFF704B42)
        val skin = if (character == 0) Color(0xFFFFD8C6) else Color(0xFFFFD2BD)
        val shirts = listOf(Color(0xFF8E85C8), Color(0xFF6F9FA0), Color(0xFFE28C9E), Color(0xFF303747))
        drawCircle(Color(0xFF51465F), size.width * .39f, Offset(cx, cy + 8f))
        drawRoundRect(shirts[outfit % shirts.size], Offset(cx - size.width * .20f, cy + size.height * .06f), Size(size.width * .40f, size.height * .43f), androidx.compose.ui.geometry.CornerRadius(65f))
        drawCircle(hair, size.width * .17f, Offset(cx, cy - size.height * .05f))
        drawCircle(skin, size.width * .135f, Offset(cx, cy))
        drawArc(hair, 180f, 180f, false, Offset(cx - size.width * .14f, cy - size.height * .11f), Size(size.width * .28f, size.height * .23f))
        drawCircle(Ink, size.width * .012f, Offset(cx - size.width * .045f, cy))
        drawCircle(Ink, size.width * .012f, Offset(cx + size.width * .045f, cy))
        drawLine(Color(0xFF9B5965), Offset(cx - 7f, cy + 25f), Offset(cx + 7f, cy + 25f), 3f)
    }
}

@Composable
private fun WardrobeScreen(outfit: Int, onSelect: (Int) -> Unit, onBack: () -> Unit) {
    val names = listOf("Suéter lavanda", "Uniforme verde", "Chaqueta rosa", "Negro clásico")
    Column(Modifier.fillMaxSize().background(Color(0xFF292638)).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("TU ESTILO", color = Cream, fontSize = 25.sp, fontWeight = FontWeight.Black)
        Text("Cambia la ropa del protagonista", color = Color(0xFFD4C9E8), fontSize = 13.sp)
        CharacterPortrait(outfit, 0, Modifier.fillMaxWidth().weight(1f))
        names.forEachIndexed { index, name ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(if (outfit == index) Color(0xFF51465F) else Color(0xFF393448), RoundedCornerShape(12.dp)).clickable { onSelect(index) }.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (outfit == index) "●  " else "○  ", color = Lilac)
                Text(name, color = Cream)
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Lilac)) {
            Text("LISTO", color = Ink, fontWeight = FontWeight.Bold)
        }
    }
}
