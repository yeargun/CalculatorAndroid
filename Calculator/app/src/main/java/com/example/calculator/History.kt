package com.example.calculator

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class History: RealmObject() {
    @PrimaryKey
    private var id: Long = 0
    private lateinit var calculation: String
    private lateinit var time: String

    fun setTime(time:String){
        this.time = time
    }
    fun getTime(): String{
        return this.time
    }

    fun setId(id:Long){
        this.id = id
    }
    fun getId(): Long{
        return this.id
    }

    fun setCalculation(calculation:String){
        this.calculation= calculation
    }
    fun getCalculation(): String{
        return this.calculation
    }
}