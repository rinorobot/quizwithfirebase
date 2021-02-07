package com.rinosystems.quizwithfirebase

class Pregunta {


    private var correcta: String = ""
    private var linkA: String = ""
    private var linkB: String = ""
    private var linkC: String = ""
    private var linkD: String = ""
    private var linkPregunta: String = ""
    private var pregunta: String = ""
    private var respuestaA: String = ""
    private var respuestaB: String = ""
    private var respuestaC: String = ""
    private var respuestaD: String = ""

    constructor(
        correcta: String,
        linkA: String,
        linkB: String,
        linkC: String,
        linkD: String,
        linkPregunta: String,
        pregunta: String,
        respuestaA: String,
        respuestaB: String,
        respuestaC: String,
        respuestaD: String
    ) {
        this.correcta = correcta
        this.linkA = linkA
        this.linkB = linkB
        this.linkC = linkC
        this.linkD = linkD
        this.linkPregunta = linkPregunta
        this.pregunta = pregunta
        this.respuestaA = respuestaA
        this.respuestaB = respuestaB
        this.respuestaC = respuestaC
        this.respuestaD = respuestaD
    }

    constructor()


    fun getCorrecta():String{
        return correcta
    }
    fun getLinkA():String{
        return linkA
    }
    fun getLinkB():String{
        return linkB
    }
    fun getLinkC():String{
        return linkC
    }
    fun getLinkD():String{
        return linkD
    }
    fun getLinkPregunta():String{
        return linkPregunta
    }
    fun getPregunta():String{
        return pregunta
    }
    fun getRespuestaA():String{
        return respuestaA
    }
    fun getRespuestaB():String{
        return respuestaB
    }
    fun getRespuestaC():String{
        return respuestaC
    }
    fun getRespuestaD():String{
        return respuestaD
    }







}