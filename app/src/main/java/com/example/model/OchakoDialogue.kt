package com.example.model

enum class OchakoExpression {
    NORMAL,
    TRAINING,
    EXCITED,
    THINKING,
    BLUSHING
}

data class OchakoDialogueLine(
    val id: String,
    val title: String = "",
    val text: String,
    val expression: OchakoExpression,
    val audioMood: String, // "cheer", "intense", "warm", "combat"
    val suggestedReplies: List<String> = emptyList(),
    val triggersAction: String? = null
)

object DialogueBank {
    val incomingCallGreetings = listOf(
        OchakoDialogueLine(
            id = "call_1",
            title = "Saludo Diario",
            text = "¡Hola, Deku-kun! ¡Qué alegría verte! ¿Listo para nuestra sesión de entrenamiento de hoy? ¡Vamos a dar lo mejor de nosotros!",
            expression = OchakoExpression.NORMAL,
            audioMood = "warm",
            suggestedReplies = listOf(
                "¡Por supuesto, Uraraka-san! ¡Estoy listo!",
                "¡Vamos a entrenar duro!",
                "¿Qué ejercicio haremos primero?"
            )
        ),
        OchakoDialogueLine(
            id = "call_2",
            title = "Llegada al Gimnasio Gamma",
            text = "¡Deku-kun! Acabo de llegar al campo de entrenamiento Gamma. ¡Mis almohadillas de gravedad están calibradas y al 100%!",
            expression = OchakoExpression.TRAINING,
            audioMood = "intense",
            suggestedReplies = listOf(
                "¡Increíble! ¡Hagamos control de escombros!",
                "¡Cuidado con las náuseas, toma un descanso si lo necesitas!",
                "¡Iniciemos la sincronización por voz!"
            )
        ),
        OchakoDialogueLine(
            id = "call_3",
            title = "Movimiento Combinado",
            text = "¡Izuku! ¡Estuve practicando el movimiento combinado que ideamos! ¡Si tocas el micrófono o dices 'PLUS ULTRA' te demostraré mi flotación!",
            expression = OchakoExpression.EXCITED,
            audioMood = "cheer",
            suggestedReplies = listOf(
                "¡PLUS ULTRA!",
                "¡Vamos a probarlo en el minijuego!",
                "¡Eres asombrosa, Uraraka-san!"
            )
        ),
        OchakoDialogueLine(
            id = "call_4",
            title = "Estrategia de Rescate",
            text = "¡Deku-kun! El profesor Aizawa dijo que debemos perfeccionar la evacuación en gravedad cero y el trabajo en equipo.",
            expression = OchakoExpression.THINKING,
            audioMood = "warm",
            suggestedReplies = listOf(
                "¡Entendido! ¡Coordinemos con el látigo negro y flotación!",
                "¡Hagamos una simulación de rescate rápida!"
            )
        ),
        OchakoDialogueLine(
            id = "call_5",
            title = "Motivación de Héroe",
            text = "¡Verte esforzarte tanto todos los días siempre me llena de valor, Deku-kun! ¡Juntos llegaremos a ser héroes increíbles que salven a todos con una sonrisa!",
            expression = OchakoExpression.BLUSHING,
            audioMood = "warm",
            suggestedReplies = listOf(
                "¡Gracias, Uraraka-san! ¡Tú también eres mi gran inspiración!",
                "¡Vamos con todo por el futuro!"
            )
        )
    )

    val combatCheeringLines = listOf(
        OchakoDialogueLine(
            id = "cheer_1",
            title = "Impulso Aéreo",
            text = "¡Excelente reflejo, Deku-kun! ¡Ese impulso aéreo estuvo perfecto!",
            expression = OchakoExpression.EXCITED,
            audioMood = "cheer"
        ),
        OchakoDialogueLine(
            id = "cheer_2",
            title = "Liberación Release",
            text = "¡Liberación de gravedad... RELEASE! ¡Los escombros están flotando, atrápala antes de que caiga!",
            expression = OchakoExpression.TRAINING,
            audioMood = "combat"
        ),
        OchakoDialogueLine(
            id = "cheer_3",
            title = "Determinación",
            text = "¡Esa determinación en tus ojos siempre me inspira a no rendirme jamás! ¡Vamos con más fuerza!",
            expression = OchakoExpression.NORMAL,
            audioMood = "warm"
        ),
        OchakoDialogueLine(
            id = "cheer_4",
            title = "Potencia Vocal Máxima",
            text = "¡Tu potencia vocal activó la señal de héroe! ¡Eso merece 100 puntos extra de entrenamiento!",
            expression = OchakoExpression.EXCITED,
            audioMood = "cheer"
        ),
        OchakoDialogueLine(
            id = "cheer_5",
            title = "Combo Clase 1-A",
            text = "¡Increíble sincronización! ¡Si All Might nos estuviera viendo, estaría muy orgulloso de nosotros!",
            expression = OchakoExpression.EXCITED,
            audioMood = "cheer"
        )
    )

    val tipsAndMotivation = listOf(
        OchakoDialogueLine(
            id = "tip_1",
            title = "Respiración y Equilibrio",
            text = "Recuerda respirar hondo al canalizar tu energía. ¡El equilibrio entre fuerza y precisión es la clave!",
            expression = OchakoExpression.THINKING,
            audioMood = "warm"
        ),
        OchakoDialogueLine(
            id = "tip_2",
            title = "Técnica de Flotación",
            text = "Si juntamos las yemas de mis dedos... ¡Cero gravedad! Y con tus patadas aéreas, ¡somos un dúo imparable!",
            expression = OchakoExpression.TRAINING,
            audioMood = "cheer"
        ),
        OchakoDialogueLine(
            id = "tip_3",
            title = "Registro en el Cuaderno",
            text = "Registremos esta práctica en nuestro cuaderno diario de héroes. ¡El progreso constante nos llevará a la cima!",
            expression = OchakoExpression.NORMAL,
            audioMood = "warm"
        ),
        OchakoDialogueLine(
            id = "tip_4",
            title = "Sonreír al Salvar",
            text = "Incluso cuando todo parezca difícil, sonreír nos ayuda a salvar no solo a otros, sino a nosotros mismos.",
            expression = OchakoExpression.BLUSHING,
            audioMood = "warm"
        )
    )

    val availableVoiceCommands = listOf(
        "PLUS ULTRA" to "Desata la energía máxima de la UA y añade 100 HP.",
        "SMASH" to "Activa el ataque One For All de Deku y la ovación de Ochako.",
        "GRAVEDAD CERO / RELEASE" to "Inicia el minijuego de escombros flotantes.",
        "REFLEJOS" to "Activa el modo de entrenamiento de reacción rápida.",
        "REGISTRAR" to "Anota la sesión actual en el registro diario con Room.",
        "OPCIONES" to "Ochako te narra en voz alta todas las opciones disponibles."
    )
}
