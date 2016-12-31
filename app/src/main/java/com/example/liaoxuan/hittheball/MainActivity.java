package com.example.liaoxuan.hittheball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackStage back = new BackStage();
        back.MapGenerated();
        int [][] map = new int[back.m][back.n];
        for(int i=0; i<back.m; i++)
        {
            for(int j=0; j<back.n; j++)
            {
                System.out.print(back.map[i][j]+" ");
            }
            System.out.print("\n");
        }
    }

}
