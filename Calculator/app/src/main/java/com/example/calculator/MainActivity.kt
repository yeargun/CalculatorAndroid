package com.example.calculator

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity() {

    lateinit var  realm: Realm
    val historyList = ArrayList<String>()
    var expression = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Realm
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("myrealm.realm")
            .schemaVersion(42)
            .build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
        getOldHistoryToArrayList(historyList)


        //adapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var adapter =  RecyclerAdapter(historyList)
        recyclerView.adapter=adapter

        //Numbers
        tvZero.setOnClickListener{
            tvExpression.append("0")
            appendOnPreExpression("0")
        }
        tvOne.setOnClickListener{
            tvExpression.append("1")
            appendOnPreExpression("1")
        }
        tvTwo.setOnClickListener{
            tvExpression.append("2")
            appendOnPreExpression("2")
        }
        tvThree.setOnClickListener{
            tvExpression.append("3")
            appendOnPreExpression("3")
        }
        tvFour.setOnClickListener{
            tvExpression.append("4")
            appendOnPreExpression("4")
        }
        tvFive.setOnClickListener{
            tvExpression.append("5")
            appendOnPreExpression("5")
        }
        tvSix.setOnClickListener{
            tvExpression.append("6")
            appendOnPreExpression("6")
        }
        tvSeven.setOnClickListener{
            tvExpression.append("7")
            appendOnPreExpression("7")
        }
        tvEight.setOnClickListener{
            tvExpression.append("8")
            appendOnPreExpression("8")
        }
        tvNine.setOnClickListener{
            tvExpression.append("9")
            appendOnPreExpression("9")
        }
        tvPi.setOnClickListener{
            tvExpression.append("PI")
            appendOnPreExpression("3.14159")
        }
        tvEuler.setOnClickListener{
            tvExpression.append("e")
            appendOnPreExpression("2.71828")
        }


        //Operators
        tvMinus.setOnClickListener{
            if(canAddOperator(this.expression)){
                tvExpression.append("-")
                appendOnPreExpression("-")
            }
        }
        tvPlus.setOnClickListener{
            if(canAddOperator(this.expression)){
                tvExpression.append("+")
                appendOnPreExpression("+")
            }

        }
        tvDivide.setOnClickListener{
            if(canAddOperator(this.expression)){
                tvExpression.append("/")
                appendOnPreExpression("/")
            }
        }
        tvMul.setOnClickListener{
            if(canAddOperator(this.expression)){
                tvExpression.append("x")
                appendOnPreExpression("*")
            }
        }
        tvPercentage.setOnClickListener{
            if(canAddOperator(this.expression)){
                tvExpression.append("%")
                appendOnPreExpression("/100")
                tvEquals.performClick()
            }
        }
        tvEquals.setOnClickListener {
            if(canAddOperator(this.expression)){
                try {
                    updateHistoryList()
                    tvExpression.text = tvResult.text
                    tvResult.text  =""
                    this.expression = tvExpression.text.toString()
                    val layoutManager = LinearLayoutManager(this)
                    recyclerView.layoutManager = layoutManager
                    var adapter =  RecyclerAdapter(historyList)
                    recyclerView.adapter=adapter
                }catch (e:Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT ).show()
                }
            }
        }
        tvFactorial.setOnClickListener{
            try {
                if (canAddOperator(this.expression)) {
                    if(!expression.contains(".")){
                        tvExpression.append("!")
                        expression = factorial(expression.toInt()).toString()
                        tryToEvaluate(this.expression)
                        tvEquals.performClick()
                    }else{
                        Toast.makeText(this,"Factorial defined on integers", Toast.LENGTH_SHORT ).show()
                    }
                }
            }catch (e: Exception){
                println(e.message)}
        }
        tvDot.setOnClickListener {
            if(canAddOperator(this.expression)) {
                tvExpression.append(".")
                appendOnPreExpression(".")
            }
        }
        tvOpen.setOnClickListener {
            if(canAddOperator(this.expression)) {
                tvExpression.append("(")
                appendOnPreExpression("(")
            }
        }
        tvClose.setOnClickListener {
            if(canAddOperator(this.expression)) {
                tvExpression.append(")")
                appendOnPreExpression(")")
            }
        }
        tvClear.setOnClickListener {
            tvExpression.text = ""
            tvResult.text = ""
            this.expression=""
        }
        tvBack.setOnClickListener {
            val string = tvExpression.text.toString()
            if(string.isNotEmpty()){
                tvExpression.text = string.substring(0,string.length-1)
                expression=expression.substring(0, expression.length-1)
                tryToEvaluate(expression)
            }
        }
        tvExpand.setOnClickListener{
            showHide(tvRow1)
            showHide(tvRow2)
            showHide(tvSqrt)
            showHide(tvFactorial)
            showHide(tvOneOverX)
            showHide(tvPi)
            showHide(tvEuler)
        }
    }

    private fun getOldHistoryToArrayList(historyList: ArrayList<String>) {
        var realmResults = realm.where(History::class.java).findAll()
        for (i in 0..realmResults.size-1){
            var time = realmResults.get(i)?.getTime() ?: "null"
            var calculation: String = realmResults.get(i)?.getCalculation() ?: "null"
            historyList.add("$calculation                 $time")
        }
    }

    //to avoid arbitrary operator ("--" , "+x" , ".." , "+." , ...)
    fun canAddOperator(tvExpression: String): Boolean{
        if(tvExpression.endsWith("+") || tvExpression.endsWith("-") ||
            tvExpression.endsWith("*") || tvExpression.endsWith("/") || tvExpression.endsWith("x") ||
            tvExpression.isEmpty() || tvExpression.endsWith(".")){
            return false
        }else{
            return true
        }
    }
    fun tryToEvaluate(expression: String){
        try {
            var expressionObject = ExpressionBuilder(expression).build()
            val result = expressionObject.evaluate()
            val longResult = result.toLong()
            if(result == longResult.toDouble()){
                tvResult.text = longResult.toString()}
            else{
                tvResult.text = result.toString()
            }
            tvResult.visibility = VISIBLE
        }catch (e: Exception) {tvResult.visibility = INVISIBLE}
    }
    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }
    fun factorial(number: Int):Int{
        var hold : Int = 1
        for (i in 1..number) {
            // hold = hold * i
            hold *= i
        }
        return hold
    }
    fun appendOnPreExpression(newString: String){
        this.expression += newString
        tryToEvaluate(this.expression)
    }
    fun updateHistoryList(){
        addCalculationToDB(tvExpression.text.toString())
        realmDataToArrayList()
        addCalculationToDB("="+tvResult.text.toString())
        realmDataToArrayList()
    }
    fun addCalculationToDB(calculation: String){
        realm.beginTransaction()
        try{
            val nextId: Long = realm.where(History::class.java).count()+1
            val lastCalculation = realm.createObject(History::class.java,nextId)
            val time  = showCurrentTime()
            lastCalculation.setCalculation(calculation)
            lastCalculation.setTime(showCurrentTime())
            realm.commitTransaction()
        }catch (e: RealmException){
            Log.d("Tag", e.message.toString())
        }
    }

    //adds the last element to arraylist
    fun realmDataToArrayList(){
        var realmResults = realm.where(History::class.java).findAll()
        var id: Long = realmResults.get(realmResults.size-1)?.getId() ?: 1
        var calculation: String = realmResults.get(realmResults.size-1)?.getCalculation() ?: "null"
        var time = realmResults.get(realmResults.size-1)?.getTime() ?: "null"
        val k= "$calculation                 $time"
        historyList.add(k)
    }
    private fun showCurrentTime(): String{
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        return formatted
    }
}





