package com.rinosystems.quizwithfirebase

class Examen {

    private var nombre_examen : String = ""

    constructor(nombre_examen: String) {
        this.nombre_examen = nombre_examen
    }

    constructor()

    public fun getNombreExamen(): String{
        return nombre_examen
    }

    public fun setNombre_Examen(nombre: String){
        nombre_examen = nombre
    }


}