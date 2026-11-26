package com.ppp.mindhouse

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF342D46)
private val Cream = Color(0xFFFFF8F3)
private val Lilac = Color(0xFFC7B2F2)
private val Pink = Color(0xFFF3B9C9)
private val Mint = Color(0xFFB9DED2)
private val Soft = Color(0xFFF7EDF6)

private enum class GamePlace(val label: String, val icon: String, val description: String) {
    BEDROOM("Dormitorio", "🛏", "Tu habitación: recuerdos, objetos y secretos."),
    BATHROOM("Baño", "🫧", "Un espejo, demasiadas preguntas y una toalla."),
    KITCHEN("Cocina", "🍓", "Elena suele preparar el desayuno aquí."),
    LIVING("Sala", "🪴", "Un lugar tranquilo para hablar o mirar alrededor."),
    STREET("Barrio", "🌷", "Calles pequeñas, tiendas y vecinos."),
    SCHOOL("Instituto", "🎒", "Clases, compañeros y rumores."),
    LIBRARY("Biblioteca", "📚", "Libros, archivos y rincones silenciosos."),
    PARK("Parque", "🦋", "Un sitio para descansar y conocer gente.")
}

private data class Person(val name: String, val role: String, val hair: Color, val clothes: Color, val note: String)
private val people = listOf(
    Person("Alex", "Tú", Color(0xFF493344), Color(0xFF8198B9), "Un chico que despierta sin recordar su vida."),
    Person("Elena", "Mamá", Color(0xFF79513F), Color(0xFF91A9B5), "Cariñosa, observadora y difícil de engañar."),
    Person("Lucía", "Hermana", Color(0xFF3C293C), Color(0xFFE7A5BD), "Sarcástica, curiosa y más lista de lo que aparenta."),
    Person("Maya", "Compañera", Color(0xFF874D49), Color(0xFFDDA3C0), "Parece saber algo sobre Alex, pero no lo cuenta.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(52, 45, 70)
        window.navigationBarColor = android.graphics.Color.rgb(52, 45, 70)
        setContent { MindHouseGame(this) }
    }
}

@Composable
private fun MindHouseGame(context: Context) {
    val save = remember { context.getSharedPreferences("quiet_room_save", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf("home") }
    var place by remember { mutableStateOf(GamePlace.BEDROOM) }
    var day by remember { mutableIntStateOf(save.getInt("day", 1)) }
    var hour by remember { mutableIntStateOf(save.getInt("hour", 7)) }
    var outfit by remember { mutableIntStateOf(save.getInt("outfit", 0)) }
    var energy by remember { mutableIntStateOf(save.getInt("energy", 5)) }
    var coins by remember { mutableIntStateOf(save.getInt("coins", 12)) }
    var dialogue by remember { mutableStateOf("Despiertas con un dolor de cabeza. La habitación no te resulta familiar. Puedes explorar a tu ritmo.") }
    var speaker by remember { mutableStateOf("NARRACIÓN") }
    var clueCount by remember { mutableIntStateOf(save.getInt("clues", 0)) }
    var trust by remember { mutableIntStateOf(save.getInt("trust", 0)) }
    var explored by remember { mutableStateOf(save.getStringSet("explored", emptySet())?.toMutableSet() ?: mutableSetOf()) }
    var inventory by remember { mutableStateOf(save.getStringSet("inventory", setOf("Teléfono bloqueado"))?.toMutableSet() ?: mutableSetOf("Teléfono bloqueado")) }
    var journal by remember { mutableStateOf(save.getStringSet("journal", setOf("No recuerdas quién eres."))?.toMutableSet() ?: mutableSetOf("No recuerdas quién eres.")) }
    fun addClue(label: String) { if (journal.add(label)) { clueCount += 1; journal = journal.toMutableSet() } }
    fun addJournal(label: String) { journal = journal.toMutableSet().apply { add(label) } }
    fun addItem(label: String) { inventory = inventory.toMutableSet().apply { add(label) } }

    LaunchedEffect(day, hour, outfit, energy, coins, clueCount, trust, explored.size, inventory.size, journal.size) {
        save.edit().putInt("day", day).putInt("hour", hour).putInt("outfit", outfit)
            .putInt("energy", energy).putInt("coins", coins).putInt("clues", clueCount)
            .putInt("trust", trust).putStringSet("explored", explored.toSet())
            .putStringSet("inventory", inventory.toSet()).putStringSet("journal", journal.toSet()).apply()
    }

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Soft) {
            when (page) {
                "home" -> HomeScreen(onPlay = { page = "world" }, onWardrobe = { page = "wardrobe" })
                "wardrobe" -> Wardrobe(outfit, onChoose = { outfit = it }, onBack = { page = "world" })
                "journal" -> CollectionScreen("DIARIO Y PISTAS", journal.toList(), "Cada pista que descubras se guarda aquí.", { page = "world" })
                "bag" -> CollectionScreen("MOCHILA", inventory.toList(), "Objetos que llevas contigo.", { page = "world" })
                "people" -> PeopleScreen { page = "world" }
                else -> WorldScreen(
                    place = place, day = day, hour = hour, outfit = outfit, energy = energy,
                    coins = coins, clueCount = clueCount, speaker = speaker, dialogue = dialogue,
                    explored = explored, onMove = { place = it; speaker = "NARRACIÓN"; dialogue = it.description; },
                    onAction = { action ->
                        when (action) {
                            "mirror" -> { speaker = "ALEX"; dialogue = "Te miras fijamente. Ese rostro no es el tuyo. «Diablo... pero qué cambio». Te cubres con una toalla y respiras hondo."; addClue("El rostro del espejo"); addItem("Foto familiar") }
                            "photo" -> { speaker = "NARRACIÓN"; dialogue = "En la foto aparecen Elena, Lucía y Alex. Detrás hay una fecha escrita: hace tres meses."; addClue("Foto familiar con una fecha"); inventory.add("Foto familiar") }
                            "phone" -> { speaker = "NARRACIÓN"; dialogue = "El teléfono pide un patrón. En la pantalla bloqueada hay varios mensajes sin leer y una notificación: «No vuelvas a ese lugar»."; addClue("Mensaje: No vuelvas a ese lugar"); addItem("Teléfono bloqueado") }
                            "mom" -> { speaker = "ELENA"; dialogue = "Cariño, tienes mala cara. ¿Te encuentras bien? Si necesitas quedarte en casa, dímelo."; trust += 1; addJournal("Elena se preocupa por ti.") }
                            "sister" -> { speaker = "LUCÍA"; dialogue = "¿Qué te pasa hoy? Estás rarísimo. Si quieres hablar, estoy en mi cuarto... pero no toques mis cosas."; trust += 1; addJournal("Lucía nota que algo ha cambiado.") }
                            "maya" -> { speaker = "MAYA"; dialogue = "No esperaba verte aquí, Alex. Si de verdad no recuerdas nada, busca la libreta azul. Y no confíes en la primera versión que te cuenten."; addClue("Maya menciona una libreta azul"); trust += 1 }
                            "class" -> { speaker = "PROFESORA"; dialogue = "Llegas justo a tiempo. Puedes sentarte, revisar el tablón o hablar con tus compañeros. No todo en el instituto gira alrededor de las clases."; addJournal("El instituto ofrece nuevas pistas y amistades.") }
                            "rest" -> { energy = (energy + 2).coerceAtMost(5); speaker = "NARRACIÓN"; dialogue = "Te tomas un descanso. Recuperas energía y observas el lugar con más calma." }
                            "snack" -> { if (coins >= 3) { coins -= 3; energy = (energy + 1).coerceAtMost(5); dialogue = "Compras un dulce. Está buenísimo. Recuperas un poco de energía." } else dialogue = "No tienes suficientes monedas." }
                            else -> { speaker = "NARRACIÓN"; dialogue = "Miras alrededor con atención. No tienes que apresurarte: puedes volver cuando quieras."; }
                        }
                        explored = explored.toMutableSet().apply { add(place.name + ":" + action) }
                    },
                    onTime = { if (energy > 0) { hour = if (hour >= 21) { day += 1; 7 } else hour + 1; energy -= 1 } else dialogue = "Estás agotado. Descansa antes de seguir." },
                    onWardrobe = { page = "wardrobe" },
                    onJournal = { page = "journal" },
                    onBag = { page = "bag" },
                    onPeople = { page = "people" }
                )
            }
        }
    }

}

@Composable
private fun HomeScreen(onPlay: () -> Unit, onWardrobe: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF342D46)).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("✦ THE QUIET ROOM ✦", color = Cream, fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Text("Un misterio. Muchas vidas posibles.", color = Color(0xFFE1D5F1), fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        CharacterArt(people[0], 0, Modifier.fillMaxWidth().height(250.dp))
        Text("Explora a tu manera", color = Pink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Conoce personas, descubre secretos, cambia tu estilo y decide cómo vivir cada día.", color = Cream, textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onPlay, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Lilac), shape = RoundedCornerShape(18.dp)) { Text("ENTRAR AL MUNDO  ✿", color = Ink, fontWeight = FontWeight.Bold) }
        OutlinedButton(onClick = onWardrobe, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Text("PERSONALIZAR PERSONAJE", color = Cream) }
        Text("La historia no te obliga a seguir un camino.", color = Color(0xFFC9BED7), fontSize = 11.sp)
    }
}

@Composable
private fun WorldScreen(
    place: GamePlace, day: Int, hour: Int, outfit: Int, energy: Int, coins: Int, clueCount: Int,
    speaker: String, dialogue: String, explored: Set<String>, onMove: (GamePlace) -> Unit,
    onAction: (String) -> Unit, onTime: () -> Unit, onWardrobe: () -> Unit,
    onJournal: () -> Unit, onBag: () -> Unit, onPeople: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Soft)) {
        Row(Modifier.fillMaxWidth().background(Ink).padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("DÍA " + day + " · " + hour.toString().padStart(2, '0') + ":00", color = Cream, fontWeight = FontWeight.Bold, fontSize = 12.sp); Text("⚡ " + energy + "   ♡ " + coins + "   ✧ " + clueCount, color = Lilac, fontSize = 11.sp) }
            Text("THE QUIET ROOM", color = Cream, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Box(Modifier.fillMaxWidth().weight(1f)) {
            SceneArt(place, outfit, Modifier.fillMaxSize())
            Column(Modifier.align(Alignment.TopStart).padding(12.dp).background(Color(0xCCFFFFFF), RoundedCornerShape(12.dp)).padding(10.dp)) {
                Text(place.icon + "  " + place.label, color = Ink, fontWeight = FontWeight.Bold)
                Text(place.description, color = Ink, fontSize = 10.sp)
            }
            Text("✦", Modifier.align(Alignment.TopEnd).padding(18.dp), color = Color(0xFFFFD77A), fontSize = 25.sp)
        }
        Column(Modifier.fillMaxWidth().heightIn(max = 320.dp).background(Ink).padding(12.dp)) {
            Text(speaker, color = Pink, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(dialogue, color = Cream, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(Modifier.height(9.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SmallAction("Explorar", "🔎") { onAction("look") }
                SmallAction("Descansar", "☁️") { onAction("rest") }
                SmallAction("Avanzar hora", "⏳", onTime)
                SmallAction("Ropa", "👗", onWardrobe)
            }
        }
        Text("¿A DÓNDE QUIERES IR?", Modifier.fillMaxWidth().background(Color(0xFFE9DDF0)).padding(start = 12.dp, top = 8.dp), color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Black)
        Row(Modifier.fillMaxWidth().background(Color(0xFFE9DDF0)).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GamePlace.values().take(4).forEach { p -> PlaceButton(p, place == p, Modifier.weight(1f)) { onMove(p) } }
        }
        Row(Modifier.fillMaxWidth().background(Color(0xFFE9DDF0)).padding(horizontal = 8.dp).padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            GamePlace.values().drop(4).forEach { p -> PlaceButton(p, place == p, Modifier.weight(1f)) { onMove(p) } }
        }
        Row(Modifier.fillMaxWidth().background(Ink).padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            BottomTab("Personas", "♡", onPeople); BottomTab("Mochila", "🎒", onBag); BottomTab("Diario", "📖", onJournal)
        }
        when (place) {
            GamePlace.BEDROOM -> Row(Modifier.fillMaxWidth().background(Color(0xFFF0E5F0)).padding(7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) { MiniAction("Mirar foto", "photo", onAction); MiniAction("Revisar móvil", "phone", onAction) }
            GamePlace.BATHROOM -> Row(Modifier.fillMaxWidth().background(Color(0xFFF0E5F0)).padding(7.dp)) { MiniAction("Mirarte al espejo", "mirror", onAction) }
            GamePlace.KITCHEN -> Row(Modifier.fillMaxWidth().background(Color(0xFFF0E5F0)).padding(7.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) { MiniAction("Hablar con mamá", "mom", onAction); MiniAction("Hablar con Lucía", "sister", onAction); MiniAction("Tomar algo", "snack", onAction) }
            GamePlace.SCHOOL -> Row(Modifier.fillMaxWidth().background(Color(0xFFF0E5F0)).padding(7.dp)) { MiniAction("Entrar a clase", "class", onAction); MiniAction("Buscar a Maya", "maya", onAction) }
            GamePlace.STREET, GamePlace.PARK -> Row(Modifier.fillMaxWidth().background(Color(0xFFF0E5F0)).padding(7.dp)) { MiniAction("Buscar a Maya", "maya", onAction); MiniAction("Descansar", "rest", onAction) }
            else -> Unit
        }
    }
}

@Composable
private fun SceneArt(place: GamePlace, outfit: Int, modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFFEADFED))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val wall = when (place) {
                GamePlace.BATHROOM -> Color(0xFFD7E9E7)
                GamePlace.KITCHEN -> Color(0xFFF4E3D4)
                GamePlace.STREET, GamePlace.PARK -> Color(0xFFDCEEDC)
                GamePlace.SCHOOL, GamePlace.LIBRARY -> Color(0xFFE4E1F2)
                else -> Color(0xFFEADFED)
            }
            drawRect(wall, size = Size(w, h))
            drawRect(Color(0xFFD2BED6), Offset(0f, h * .78f), Size(w, h * .22f))
            if (place == GamePlace.STREET || place == GamePlace.PARK) {
                drawCircle(Color(0xFFFFE6A8), w * .09f, Offset(w * .82f, h * .16f))
                for (i in 0..4) {
                    val x = w * (0.08f + i * .2f)
                    drawCircle(Color(0xFF78A987), w * .075f, Offset(x, h * .55f))
                    drawRect(Color(0xFF9A755D), Offset(x - w * .015f, h * .55f), Size(w * .03f, h * .23f))
                }
                drawRect(Color(0xFFC9B9A5), Offset(0f, h * .74f), Size(w, h * .08f))
            } else {
                drawRoundRect(Color(0xFFFFF7F0), Offset(w * .08f, h * .14f), Size(w * .28f, h * .28f), androidx.compose.ui.geometry.CornerRadius(18f))
                drawLine(Color(0xFFB8A6C4), Offset(w * .22f, h * .14f), Offset(w * .22f, h * .42f), 4f)
                drawLine(Color(0xFFB8A6C4), Offset(w * .08f, h * .28f), Offset(w * .36f, h * .28f), 4f)
                drawRoundRect(Color(0xFFB99EC5), Offset(w * .52f, h * .55f), Size(w * .38f, h * .23f), androidx.compose.ui.geometry.CornerRadius(22f))
                drawRoundRect(Color(0xFFFFF7F0), Offset(w * .54f, h * .51f), Size(w * .34f, h * .10f), androidx.compose.ui.geometry.CornerRadius(18f))
                if (place == GamePlace.BATHROOM) {
                    drawRoundRect(Color(0xFF9FC9C5), Offset(w * .60f, h * .16f), Size(w * .22f, h * .32f), androidx.compose.ui.geometry.CornerRadius(15f), style = Stroke(5f))
                }
            }
        }
        CharacterArt(people[0].copy(clothes = outfitColor(outfit)), outfit, Modifier.align(Alignment.BottomCenter).fillMaxWidth(.48f).height(205.dp))
    }
}

@Composable
private fun CharacterArt(person: Person, outfit: Int, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val cx = size.width / 2f; val cy = size.height * .40f
        val head = size.width * .25f
        drawOval(Color(0x22342D46), Offset(cx - head * 1.2f, size.height * .82f), Size(head * 2.4f, size.height * .12f))
        drawRoundRect(person.clothes, Offset(cx - head * 1.05f, cy + head * .72f), Size(head * 2.1f, size.height * .48f), androidx.compose.ui.geometry.CornerRadius(head * .55f))
        drawCircle(person.hair, head * 1.03f, Offset(cx, cy - head * .10f))
        drawCircle(Color(0xFFFFDCCB), head * .83f, Offset(cx, cy))
        drawArc(person.hair, 180f, 180f, false, Offset(cx - head * .84f, cy - head * .76f), Size(head * 1.68f, head * 1.05f))
        drawCircle(person.hair, head * .23f, Offset(cx - head * .72f, cy + head * .18f))
        drawCircle(person.hair, head * .23f, Offset(cx + head * .72f, cy + head * .18f))
        val eyeY = cy + head * .10f
        drawCircle(Ink, head * .075f, Offset(cx - head * .30f, eyeY))
        drawCircle(Ink, head * .075f, Offset(cx + head * .30f, eyeY))
        drawCircle(Color(0xFFFFA9B9), head * .11f, Offset(cx - head * .48f, cy + head * .31f))
        drawCircle(Color(0xFFFFA9B9), head * .11f, Offset(cx + head * .48f, cy + head * .31f))
        drawLine(Color(0xFFB86E7C), Offset(cx - head * .10f, cy + head * .42f), Offset(cx + head * .10f, cy + head * .42f), 2f)
        drawCircle(Color.White, head * .035f, Offset(cx - head * .32f, eyeY - head * .03f))
        drawCircle(Color.White, head * .035f, Offset(cx + head * .28f, eyeY - head * .03f))
    }
}

private fun outfitColor(index: Int) = listOf(Color(0xFF8198B9), Color(0xFFE7A5BD), Color(0xFF9E8ACB), Color(0xFF668E88), Color(0xFF3D3B4D))[index.coerceIn(0, 4)]

@Composable
private fun Wardrobe(outfit: Int, onChoose: (Int) -> Unit, onBack: () -> Unit) {
    val names = listOf("Uniforme azul nube", "Cárdigan rosa", "Vestido lavanda", "Look menta", "Negro elegante")
    Column(Modifier.fillMaxSize().background(Ink).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ARMARIO KAWAII", color = Cream, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text("Elige cómo quieres vestir hoy.", color = Lilac)
        CharacterArt(people[0].copy(clothes = outfitColor(outfit)), outfit, Modifier.fillMaxWidth().weight(1f))
        names.forEachIndexed { i, name ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(if (i == outfit) Color(0xFF635579) else Color(0xFF443C55), RoundedCornerShape(14.dp)).clickable { onChoose(i) }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (i == outfit) "✿  " else "♡  ", color = Pink)
                Text(name, color = Cream)
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Lilac)) { Text("LISTO", color = Ink, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PeopleScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Soft).padding(16.dp)) {
        Text("PERSONAJES", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text("Conócelos poco a poco. Cada uno tiene sus propios secretos.", color = Ink, fontSize = 12.sp)
        LazyColumn(Modifier.weight(1f)) {
            items(people.size) { i ->
                val p = people[i]
                Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).background(Color.White, RoundedCornerShape(18.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    CharacterArt(p, i, Modifier.size(92.dp))
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text(p.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(p.role, color = Color(0xFF9C759B), fontSize = 11.sp)
                        Text(p.note, color = Ink, fontSize = 11.sp)
                    }
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("VOLVER") }
    }
}

@Composable
private fun CollectionScreen(title: String, entries: List<String>, subtitle: String, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Soft).padding(20.dp)) {
        Text(title, color = Ink, fontSize = 23.sp, fontWeight = FontWeight.Black)
        Text(subtitle, color = Ink, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        entries.forEach { entry -> Text("✧  " + entry, Modifier.fillMaxWidth().padding(vertical = 5.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(13.dp), color = Ink, fontSize = 13.sp) }
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("VOLVER AL MUNDO") }
    }
}

@Composable
private fun PlaceButton(place: GamePlace, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier.background(if (selected) Lilac else Color.White, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(place.icon, fontSize = 17.sp)
        Text(place.label, color = Ink, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SmallAction(label: String, icon: String, onClick: () -> Unit) {
    Column(Modifier.width(76.dp).background(Color(0xFF51465F), RoundedCornerShape(10.dp)).clickable { onClick() }.padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 15.sp); Text(label, color = Cream, fontSize = 9.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun MiniAction(label: String, action: String, onAction: (String) -> Unit) {
    Text(label, Modifier.padding(3.dp).background(Color.White, RoundedCornerShape(10.dp)).clickable { onAction(action) }.padding(horizontal = 10.dp, vertical = 8.dp), color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun BottomTab(label: String, icon: String, onClick: () -> Unit) {
    Column(Modifier.clickable { onClick() }.padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 17.sp); Text(label, color = Cream, fontSize = 10.sp)
    }
}
