# The Quiet Room — diseño de juego y sistemas

## Identidad
Aventura narrativa 2D nativa para Android, con estética kawaii/chibi original, paleta pastel, misterio psicológico y libertad de exploración. No utiliza WebView, HTML ni una web empaquetada.

## Regla principal de diseño
El jugador decide qué hacer y cuándo. El misterio es una línea narrativa opcional y persistente, no una secuencia que bloquea el mundo. Las ubicaciones están disponibles desde el comienzo; el jugador puede ignorar pistas, descansar, conversar, visitar lugares o cambiar su ropa.

## Mundo jugable actual
- Casa: dormitorio, baño, cocina y sala.
- Exterior: barrio y parque.
- Instituto: escuela y biblioteca.
- Reloj por horas, días, energía y monedas.
- Acciones por ubicación, pistas, diario y mochila.
- Guardado local con SharedPreferences.
- Galería de personajes conectada a Cast.
- Armario con cinco paletas de atuendo.
- Interacciones con Elena, Lucía, Maya, Noah, Sora e Iris; Vera forma parte del reparto.

## Reparto
Alex es el protagonista. Elena y Lucía son su familia. Maya parece conocer información sobre su pasado. Noah, Sora, Iris y Vera amplían las relaciones del instituto y el barrio. Los retratos se dibujan con Canvas de Compose y tienen rasgos/accesorios kawaii originales.

## Sistemas implementados en esta etapa
- Navegación libre entre ocho lugares.
- Acciones de inspección y descanso.
- Interacciones y conversaciones por lugar.
- Descubrimiento de objetos y pistas.
- Inventario, diario y galería.
- Compra sencilla de un snack con monedas.
- Cambio de atuendo.
- Guardado de día, hora, energía, monedas, confianza, pistas, ropa, objetos y diario.

## Próximas capas de producción
Esta base todavía necesita sprites ilustrados de alta resolución, animaciones cuadro a cuadro, movimiento libre del personaje, colisiones, mapas de habitaciones más ricos, más diálogos ramificados, sistema de relaciones con consecuencias, actividades secundarias, sonido, música, pruebas de accesibilidad y pruebas de APK en dispositivos reales. No debe describirse como juego terminado hasta completar y verificar esas capas.
