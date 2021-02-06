package com.rinosystems.quizwithfirebase

class Pregunta {


    var correcta: String = ""
    private var pregunta: String = ""
        var respuestaA: String = ""
        var respuestaB: String = ""
        var respuestaC: String = ""
        var respuestaD: String = ""

    constructor(
        correcta: String,
        pregunta: String,
        respuestaA: String,
        respuestaB: String,
        respuestaC: String,
        respuestaD: String
    ) {
        this.correcta = correcta
        this.pregunta = pregunta
        this.respuestaA = respuestaA
        this.respuestaB = respuestaB
        this.respuestaC = respuestaC
        this.respuestaD = respuestaD
    }

    constructor()

    fun getPregunta():String{
        return pregunta
    }


}