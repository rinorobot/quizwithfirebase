package com.rinosystems.quizwithfirebase

class Examen {

    private var nombre_examen : String = ""
    private var area : String = ""

    constructor(nombre_examen: String, area: String) {
        this.nombre_examen = nombre_examen
        this.area = area
    }

    constructor()


    fun getArea(): String{
        return area
    }


    public fun getNombreExamen(): String{
        return nombre_examen
    }

    public fun setNombre_Examen(nombre: String){
        nombre_examen = nombre
    }

    public fun setArea(areas: String){
        area = areas
    }


}