package com.barryzea.connectfour.entities;

import android.widget.ImageView;

/****
 * Project ConnectFour
 * Created by Barry Zea H. on 10/06/2023
 * Copyright (c)  All rights reserved.
 ***/

public class Piece {
    private ImageView imageView;
    private int color;

    public Piece(){};

    public Piece(ImageView imageView, int color){
        this.imageView=imageView;
        this.color=color;
    }
    public Piece(ImageView imageview){
        this.imageView=imageview;
    }

    public ImageView getImageView(){return this.imageView;}
    public void setImageView(ImageView imageView){ this.imageView=imageView;}
    public int getColor(){ return this.color;}
    public void setColor(int color){this.color=color;}

}
