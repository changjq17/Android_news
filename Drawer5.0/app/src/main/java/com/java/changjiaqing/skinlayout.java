package com.java.changjiaqing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import skin.support.SkinCompatManager;

public class skinlayout extends AppCompatActivity implements View.OnClickListener{
    private Button button_yellow,button_default,button_black,button_purple,button_green,button_blue,button_pink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skinlayout);
        Toolbar toolbar = findViewById(R.id.toolbarinskin);
        setSupportActionBar(toolbar);
        button_default=findViewById(R.id.button_default);
        button_yellow=findViewById(R.id.button_yellow);
        button_black=findViewById(R.id.button_black);
        button_purple=findViewById(R.id.button_purple);
        button_green=findViewById(R.id.button_green);
        button_blue=findViewById(R.id.button_blue);
        button_pink=findViewById(R.id.button_pink);
        button_default.setOnClickListener(this);
        button_yellow.setOnClickListener(this);
        button_black.setOnClickListener(this);
        button_purple.setOnClickListener(this);
        button_green.setOnClickListener(this);
        button_blue.setOnClickListener(this);
        button_pink.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_default:
                SkinCompatManager.getInstance().restoreDefaultTheme();
                break;
            case R.id.button_yellow:
                SkinCompatManager.getInstance().loadSkin("yellow", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
            case R.id.button_black:
                SkinCompatManager.getInstance().loadSkin("black", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
            case R.id.button_purple:
                SkinCompatManager.getInstance().loadSkin("purple", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
            case R.id.button_green:
                SkinCompatManager.getInstance().loadSkin("green", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
            case R.id.button_blue:
                SkinCompatManager.getInstance().loadSkin("blue", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
            case R.id.button_pink:
                SkinCompatManager.getInstance().loadSkin("pink", null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
                break;
        }
    }
}
