package com.barryzea.connectfour

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.barryzea.connectfour.common.Constants
import com.barryzea.connectfour.common.createImageViewPiece
import com.barryzea.connectfour.common.createLinearLayout
import com.barryzea.connectfour.common.postDelay
import com.barryzea.connectfour.common.showGameOverDialog
import com.barryzea.connectfour.common.showResetDialog
import com.barryzea.connectfour.databinding.ActivityMainBinding
import com.barryzea.connectfour.entities.Piece

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private lateinit var arrayPieces:Array<Array<Piece>>
    private var linearLayoutColumn:LinearLayout?=null
    private var switchTurn:Boolean=true
    private val ROWS=6
    private val COLUMNS=7
    private var currentPlayColor=0
    private var beginPiecesTurn:Boolean=true
    private var MY_PLAYER_SCORE=0
    private var OTHER_PLAYER_SCORE=0
    private var winningCoords:MutableList<MutableList<Pair<Int, Int>>> = arrayListOf(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        populateGameBoard()
        setUpListeners()

    }
    private fun populateGameBoard(){
        loadOrClearScore(true)
        bind.lnContent.removeAllViews()
        linearLayoutColumn?.removeAllViews()
        arrayPieces= Array(COLUMNS){ Array(ROWS){Piece()} }

        for(column in  0  until COLUMNS){
           linearLayoutColumn = createLinearLayout()
            for(row in 0 until ROWS){
                val imageView = createImageViewPiece(column,row,::setPieceOnCLickListener)
                linearLayoutColumn?.addView(imageView)
                addImageViewToArray(column,row,imageView)
            }
            bind.lnContent.addView(linearLayoutColumn)
        }
    }
    private fun setUpListeners(){
        bind.btnResetGame.setOnClickListener {
            showResetDialog(getString(R.string.resetGameMsg)) {
                currentPlayColor = 0
                switchTurn = true
                populateGameBoard()
            }
        }
        bind.btnResetStatistics.setOnClickListener {
            showResetDialog(getString(R.string.resetScore)){
            loadOrClearScore(false)}
        }
    }
    private fun loadOrClearScore(load:Boolean){
        if(load) {
            bind.btnMyPlayer.setIconResource(R.drawable.green_circle)
            bind.btnOtherPlayer.setIconResource(R.drawable.yellow_circle)
            bind.btnMyPlayer.text = MY_PLAYER_SCORE.toString()
            bind.btnOtherPlayer.text = OTHER_PLAYER_SCORE.toString()
        }else{
            MY_PLAYER_SCORE=0
            OTHER_PLAYER_SCORE=0
            bind.btnMyPlayer.text = MY_PLAYER_SCORE.toString()
            bind.btnOtherPlayer.text = OTHER_PLAYER_SCORE.toString()
        }
        //cargamos por primera vez el estado de los botones que corresponden a cada jugador
        bind.btnMyPlayer.isEnabled=switchTurn ; bind.btnOtherPlayer.isEnabled=!switchTurn
    }
    private fun showGameOverDialog(colorWinn:Int, victories:Int){
       winningCoords.forEach{
           Log.e("TAG", it.size.toString() )
            if(it.size>=4) {
               it.forEach { pair->
                   val lnColumn = bind.lnContent[pair.first] as LinearLayout
                   val img = lnColumn[pair.second] as ImageView
                   img.setBackgroundResource(R.drawable.bg_win_piece)
               }
           }
        }
       postDelay(1300) {
           showGameOverDialog(colorWinn, victories) {
               populateGameBoard()
               loadOrClearScore(true)
               beginPiecesTurn = !beginPiecesTurn//alternamos el orden de las  piezas de color al final de cada partida
               switchTurn = beginPiecesTurn//seteamos ese orden para la nueva partida

           }
       }

    }

    private fun updateGameBoard(column:Int){
        val lnCol= bind.lnContent[column] as LinearLayout
        var count =1L
        val colorRes = if(!switchTurn)R.drawable.green_circle else R.drawable.yellow_circle

        for (i in 0 until ROWS){
            val img = lnCol[i] as ImageView
            img.setPadding(8,8,8,8)
            val color = arrayPieces[column][i].color
            // simulamos el efecto de caida de las piezas de color, es opcional
            postDelay(count * 90){if(color == Constants.WITHOUT_COLOR)img.setImageResource(colorRes)}
            postDelay(count * 110){if(color == Constants.WITHOUT_COLOR)img.setImageResource(0)}
            postDelay(count * 110){
                when(color){
                    Constants.MY_PLAYER_COLOR->img.setImageResource(R.drawable.green_circle)
                    Constants.OTHER_PLAYER_COLOR->img.setImageResource(R.drawable.yellow_circle)
                }

            }
            //********************************************************************************************
            count++
        }

    }
    private fun addImageViewToArray(column:Int, row:Int,  iv:ImageView){
        arrayPieces[column][row]= Piece(iv)
    }
    private fun setPieceOnCLickListener(tagCoordinates:String){
        val column= tagCoordinates.substringBeforeLast(",").toInt()
        for(i in ROWS-1 downTo 0){
            if(arrayPieces[column][i].color==Constants.WITHOUT_COLOR){
                setColorPiece(column,i)
                checkWinner()
                break
            }
        }
       updateGameBoard(column)
   }
    private fun setColorPiece(column:Int, row:Int){
        if(switchTurn){ arrayPieces[column][row].color = Constants.MY_PLAYER_COLOR;changeTurn()}
        else { arrayPieces[column][row].color = Constants.OTHER_PLAYER_COLOR; changeTurn()}

    }
    private fun changeTurn(){
        currentPlayColor = if(switchTurn)Constants.MY_PLAYER_COLOR else Constants.OTHER_PLAYER_COLOR
        switchTurn = !switchTurn
        bind.btnMyPlayer.isEnabled=switchTurn ; bind.btnOtherPlayer.isEnabled=!switchTurn
    }
    private fun checkWinner():Boolean{
        var winn=false
        for(i in 0 until COLUMNS ){
            for(j in 0 until ROWS) {
               if (checkEachDirection(i, j)) {
                    winn = true ; break
                }
            }
        }
        if(winn) {
            when (currentPlayColor) {
                Constants.MY_PLAYER_COLOR -> {MY_PLAYER_SCORE++;showGameOverDialog(currentPlayColor,MY_PLAYER_SCORE)}
                Constants.OTHER_PLAYER_COLOR -> {OTHER_PLAYER_SCORE++;showGameOverDialog(currentPlayColor,OTHER_PLAYER_SCORE)}
            }
        }
        winningCoords.clear()//limpiamos las coordenadas  ganadoras para la próxima partida
        return false
    }
    private fun checkEachDirection(col:Int,row:Int):Boolean{
        return (checkCoordinates(col,row,1,0) or //horizontal derecha
                checkCoordinates(col,row,-1,0) or //horizontal izquierda
                checkCoordinates(col,row,0,1) or //vertical inferior
                checkCoordinates(col,row,0,-1) or //vertical superior
                checkCoordinates(col,row,1,1) or  //diagonal inferior derecha
                checkCoordinates(col,row,1,-1) or //diagonal superior derecha
                checkCoordinates(col,row,-1,1) or //diagonal inferior izquierda
                checkCoordinates(col,row,-1,-1))  //diagonal superior izquierda
    }
    private fun checkCoordinates(col:Int, row:Int, col1:Int, row1:Int):Boolean{
        var cordList= mutableListOf<Pair<Int,Int>>()//creamos una lista para agregar las coordenadas
        cordList.add(Pair(col,row))//Agregamos la primera coordenada origen, para sumar con ella 4 piezas si conectan cuatro
        for(i in 1 until 4){
            if(checkNextPiece(col,row,col+(i*col1),row+(i*row1))) cordList.add(Pair(col+(i*col1),row+(i*row1)))
            if(!checkNextPiece(col,row,col+(i*col1),row+(i*row1))) break
            else if(i>=3){
                winningCoords.add(cordList)//Opcional: Agregamos las coordenadas para ponerle un background que las remarque si resulta ganador
                return true
            }
        }
        return false
    }
    private fun checkNextPiece(col:Int,row:Int, col1: Int, row1: Int):Boolean{
        return (checkIfNotOutIndexRange(col1, row1) && arrayPieces[col][row].color==arrayPieces[col1][row1].color)
       }
   private fun checkIfNotOutIndexRange(col:Int, row:Int):Boolean{
       return try {arrayPieces[col][row].color==currentPlayColor} catch(e:ArrayIndexOutOfBoundsException){false}
   }
}