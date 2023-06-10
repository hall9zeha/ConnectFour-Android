package com.barryzea.connectfour.common;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.barryzea.connectfour.R;
import com.barryzea.connectfour.databinding.WinnerDialogLayoutBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


/****
 * Project ConnectFour
 * Created by Barry Zea H. on 10/06/2023
 * Copyright (c)  All rights reserved.
 ***/
public class Utils {

    public static ImageView createImageviewPiece(Context ctx, int x, int y, MListener mListener){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ctx.getResources().getDimensionPixelSize(R.dimen.width)
                ,ctx.getResources().getDimensionPixelSize(R.dimen.height));

        ImageView iv = new ImageView(ctx);
        iv.setLayoutParams(params);
        iv.setTag(String.format("%s,%s",x,y));
        iv.setPadding(4,4,4,4);
        iv.setBackgroundResource(R.drawable.box_piece);
        iv.setOnClickListener(v-> mListener.onClick(iv.getTag().toString()));
        return iv;
    }
    public static LinearLayout createLinearLayout(Context ctx){
        LinearLayout mLnColumn = new LinearLayout(ctx);
        mLnColumn.setOrientation(LinearLayout.VERTICAL);
        mLnColumn.setGravity(Gravity.CENTER);
        return mLnColumn;

    }

    public static void postDelay(long delayMillis, MCallback delayCallback){
        new Handler().postDelayed(delayCallback::onclickCallback,delayMillis);
    }
    public static void gameOverDialog(Activity ctx, int colorWinner, int victories, MCallback callback){
        WinnerDialogLayoutBinding bind = WinnerDialogLayoutBinding.inflate(ctx.getLayoutInflater());
        int pieceDrawable = colorWinner == Constants.MY_PLAYER_COLOR ? R.drawable.green_circle : R.drawable.yellow_circle;
        new MaterialAlertDialogBuilder(ctx)
                .setView(bind.getRoot())
                .setPositiveButton(R.string.accept, (d, i) -> {
                    callback.onclickCallback();
                    d.dismiss();
                })
                .setCancelable(false)
                .show();
        bind.ivColorWinner.setImageResource(pieceDrawable);
        bind.tvVictories.setText(String.valueOf(victories));
    }
    public static void showResetDialog(Context ctx,String msg, MCallback callback){
        new MaterialAlertDialogBuilder(ctx)
                .setMessage(msg)
                .setPositiveButton(R.string.accept,(d,i)->{
                    callback.onclickCallback();
                    d.dismiss();
                })
                .setNegativeButton(R.string.cancel,(d,i)-> d.dismiss())
                .show();
    }
}
