package com.barryzea.connectfour

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.barryzea.connectfour.common.Constants
import com.barryzea.connectfour.common.createImageViewPiece
import com.barryzea.connectfour.common.createLinearLayout
import com.barryzea.connectfour.common.postDelay
import com.barryzea.connectfour.common.showGameOverDialog
import com.barryzea.connectfour.common.showResetMessage
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
    private var coordinatesWinner:MutableList<Pair<Int, Int>> = arrayListOf(Pair(0,0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        populateGameBoard()
        setUpListeners()

    }
    private fun populateGameBoard(){
        loadOrClearStats(true)
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
            showResetMessage(getString(R.string.resetGameMsg)) {
                currentPlayColor = 0
                switchTurn = true
                populateGameBoard()
            }
        }
        bind.btnResetStatistics.setOnClickListener {
            showResetMessage(getString(R.string.resetStats)){
            loadOrClearStats(false)}
        }
    }
    private fun loadOrClearStats(load:Boolean){
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

            coordinatesWinner.forEach{
                val lnColumn= bind.lnContent[it.first] as LinearLayout
                val img= lnColumn[it.second] as ImageView
                img.setBackgroundResource(R.drawable.bg_win_piece)
            }
       postDelay(2000) {
           showGameOverDialog(colorWinn, victories) {
               populateGameBoard()
               loadOrClearStats(true)
               beginPiecesTurn =
                   !beginPiecesTurn//alternamos el orden de las  piezas de color al final de cada partida
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
            //********************************************************************************************
            postDelay(count * 110){
                when(color){
                    Constants.MY_PLAYER_COLOR->img.setImageResource(R.drawable.green_circle)
                    Constants.OTHER_PLAYER_COLOR->img.setImageResource(R.drawable.yellow_circle)
                }
            }
            count++
        }
    }
    private fun addImageViewToArray(column:Int, row:Int,  iv:ImageView){
        arrayPieces[column][row]= Piece(iv)
    }
    private fun setPieceOnCLickListener(tagCoordinates:String){
        val column= tagCoordinates.substringBeforeLast(",").toInt()
        val row=tagCoordinates.substringAfterLast(",").toInt()
        for(i in 5 downTo 0){
            if(arrayPieces[column][i].color==Constants.WITHOUT_COLOR){
                setColorPiece(column,i)
                checkWinner(column,i)
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
    private fun checkWinner(col:Int, row:Int):Boolean{
        var countPieces=0;
        coordinatesWinner.add( Pair(col,row))//cargamos las coordenadas de la pieza principal
            for(j in 1 until 4){
                if(checkCoordinates(col+j,row)){coordinatesWinner.add( Pair(col+j,row)); countPieces++}//horizontal derecha
                else if(checkCoordinates(col-j,row)){ coordinatesWinner.add( Pair(col-j,row));countPieces++}//horizontal izquierda
                else if(checkCoordinates(col,row+j)) {coordinatesWinner.add( Pair(col,row+j));countPieces++}//vertical superior
                else if(checkCoordinates(col,row-j)) {coordinatesWinner.add( Pair(col,row-j));countPieces++}//vertical inferior
                else if(checkCoordinates(col+j,row+j)){coordinatesWinner.add( Pair(col+j,row+j)) ;countPieces++}//diagonal superior derecha
                else if(checkCoordinates(col-j,row+j)) {coordinatesWinner.add( Pair(col-j,row+j));countPieces++}//diagonal superior izquierda
                else if(checkCoordinates(col+j,row-j)) {coordinatesWinner.add( Pair(col+j,row-j));countPieces++}//diagonal inferior derecha
                else if(checkCoordinates(col-j,row-j)) {coordinatesWinner.add( Pair(col-j,row-j));countPieces++}//diagonal inferior izquierda
                if(countPieces == 3) {
                    when (currentPlayColor) {
                        Constants.MY_PLAYER_COLOR -> {MY_PLAYER_SCORE++;showGameOverDialog(currentPlayColor,MY_PLAYER_SCORE)}
                        Constants.OTHER_PLAYER_COLOR -> {OTHER_PLAYER_SCORE++;showGameOverDialog(currentPlayColor,OTHER_PLAYER_SCORE)}
                    }
                }
            }
        coordinatesWinner.clear()//limpiamos las coordenadas de la pieza principal si no se cumple ninguna condición
        return false
    }
    private fun checkCoordinates(col:Int, row:Int):Boolean{
        return checkIfOutIndexRange(col,row) && arrayPieces[col][row].color==currentPlayColor
    }
   private fun checkIfOutIndexRange(col:Int,row:Int):Boolean{
       return try {arrayPieces[col][row].color==currentPlayColor} catch(e:ArrayIndexOutOfBoundsException){false}
   }
}