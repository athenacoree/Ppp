package com.ppp.mindhouse

/**
 * Canonical story data for The Quiet Room.
 * Keep the protagonist and all school-age characters non-explicitly depicted.
 */
enum class Place { BEDROOM, BATHROOM, KITCHEN, HALL, STREET, SCHOOL_GATE, CLASSROOM }

data class CastMember(
    val id: String,
    val displayName: String,
    val role: String,
    val hair: Long,
    val skin: Long,
    val outfit: Long,
    val personality: String
)

object Cast {
    val members = listOf(
        CastMember("alex", "Alex", "Protagonista", 0xFF302A35, 0xFFFFD9C8, 0xFF697F9C, "Desorientado, observador; intenta actuar con normalidad."),
        CastMember("elena", "Elena", "Madre", 0xFF76513E, 0xFFEFC7AD, 0xFF8A9DAB, "Cariñosa, práctica y muy perceptiva."),
        CastMember("lucia", "Lucía", "Hermana", 0xFF3B2938, 0xFFFFD9C8, 0xFFCB91A8, "Sarcástica, ingeniosa y protectora."),
        CastMember("maya", "Maya", "Compañera", 0xFF6A4037, 0xFFF0C8B7, 0xFFE3A0B6, "Reservada; parece reconocer detalles que Alex no recuerda.")
    )
}

data class StoryBeat(
    val place: Place,
    val speaker: String,
    val text: String,
    val choices: List<String> = emptyList(),
    val clue: String? = null
)

object OpeningChapter {
    val beats = listOf(
        StoryBeat(Place.BEDROOM, "Narración", "Un dolor agudo te despierta. El reloj marca las 6:42. El techo no te resulta familiar."),
        StoryBeat(Place.BEDROOM, "Alex", "¿Dónde estoy...? Esta habitación no es mía. Espera... ¿qué me pasó?"),
        StoryBeat(Place.BEDROOM, "Narración", "En la mesita hay una foto familiar, un horario escolar y un teléfono bloqueado con un nombre: Alex."),
        StoryBeat(Place.BATHROOM, "Alex", "Te miras al espejo y te quedas helado. Ese rostro no es el que recuerdas. DIABLO... ¿¡QUIÉN ES ESTE TIPO!?"),
        StoryBeat(Place.BATHROOM, "Narración", "Te cubres rápidamente con una toalla. No es momento de entrar en pánico: primero hay que averiguar quién eres."),
        StoryBeat(Place.BEDROOM, "Alex", "El uniforme tiene el mismo nombre que el teléfono. Alex. Tendré que aprender a responder a eso."),
        StoryBeat(Place.KITCHEN, "Elena", "¡Alex! El desayuno se enfría. Y dile a tu hermana que deje el móvil un segundo."),
        StoryBeat(Place.KITCHEN, "Lucía", "Mírate la cara. Pareces un fantasma con sueño. ¿Otra noche jugando hasta tarde?"),
        StoryBeat(Place.KITCHEN, "Alex", "Tienes que improvisar. Estas personas creen conocerte de toda la vida.", listOf("Sonreír y seguirles la corriente.", "Preguntar qué día es.", "Decir que te duele muchísimo la cabeza.")),
        StoryBeat(Place.HALL, "Narración", "Sales con una mochila que no recuerdas haber preparado. Dentro hay libros, una tarjeta escolar y una foto doblada.", clue = "TARJETA ESCOLAR: Instituto Northbridge"),
        StoryBeat(Place.STREET, "Narración", "De camino al instituto chocas con una muchacha. Sus libros caen al suelo. Ella te mira con una calma desconcertante."),
        StoryBeat(Place.STREET, "Maya", "Mira por dónde vas... Alex, ¿verdad?", listOf("Disculparte y ayudarla.", "Preguntarle cómo sabe tu nombre.", "Quedarte callado y observar su reacción.")),
        StoryBeat(Place.STREET, "Narración", "Maya se marcha. Te queda una sensación extraña, pero decides no seguirla. Si quieres respuestas, tendrás que encontrarlas hablando con la gente y prestando atención.")
    )
}
