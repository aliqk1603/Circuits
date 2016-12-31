package com.circuitstudio2016.circuits;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MakeActivity extends AppCompatActivity implements View.OnTouchListener, View.OnKeyListener{
    private Graph graph;
    private MakeDrawView drawView;
    private Vertex prev;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);
        graph = new Graph();
        layout = (RelativeLayout) findViewById(R.id.activity_make);
        drawView = new MakeDrawView(this, graph);
        drawView.setBackgroundColor(Color.WHITE);
        drawView.setOnTouchListener(this);
        drawView.setOnKeyListener(this);
        layout.addView(drawView);
    }

    public void testPath(View w){
        test("path");
    }

    public void testCircuit(View w){
        test("circuit");
    }

    public void test(String type){
        if(isProperGraph()) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable("graph", graph);
            intent.putExtras(bundle);
            intent.setClass(this, HamiltonTestActivity.class);
            intent.setAction(type);
            startActivity(intent);
        }
        else{
            Toast t1 = Toast.makeText(getApplicationContext(), "2 connections minimum", Toast.LENGTH_LONG);
            t1.show();
        }
    }

    public void toggleBoundary(View w){
        drawView.toggleBoundary();
        drawView.invalidate();
    }

    public int distance(float x, float y, Vertex v){
        double distX = Math.abs(x - v.getX());
        double distY = Math.abs(y - v.getY());
        return (int) Math.sqrt(distX * distX + distY * distY);
    }

    public boolean nearAnyVertex(float x, float y){
        for(Vertex v: graph.getVertices()) {
            if(distance(x, y, v) <= 200){
                return true;
            }
        }
        return false;
    }

    public boolean overVertex(float x, float y, Vertex v){
        if(distance(x, y, v) <= 100){
            return true;
        }
        return false;
    }

    public boolean isProperGraph(){
        if(graph.getVertices().isEmpty()){
            return false;
        }
        for (Vertex v: graph.getVertices()){
            if(v.getConnections().size() < 2){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!nearAnyVertex(e.getX(), e.getY())){
                    Vertex vrtx = new Vertex((int) e.getX(), (int)e.getY(), 100);
                    graph.addVertex(vrtx);
                    prev = vrtx;
                }
                for(Vertex vrtx: graph.getVertices()){
                    if(overVertex(e.getX(), e.getY(), vrtx)){
                        prev = vrtx;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for(Vertex vrtx: graph.getVertices()) {
                    if (overVertex(e.getX(), e.getY(), vrtx) && prev != null && vrtx != prev) {
                        if(!vrtx.isConnected(prev)) {
                            vrtx.connect(prev);
                        }
                        prev = vrtx;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                prev = null;
                break;
        }
        drawView.invalidate();
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent e) {
        Log.i("key","" + keyCode);
        System.out.println(keyCode);
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            if (e.getAction() == KeyEvent.ACTION_DOWN ) {
                if(graph.getVertices().size() >= 1){
                    graph.removeVertex(graph.getVertices().get(graph.getVertices().size()-1));
                    drawView.invalidate();
                }
            }
            return true;
        }
        return false;
    }
}