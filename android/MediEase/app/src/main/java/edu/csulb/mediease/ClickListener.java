package edu.csulb.mediease;

import android.view.View;

interface ClickListener{
    public void onClick(View view, int position);
    public void onLongClick(View view,int position);
}