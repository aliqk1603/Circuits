package com.circuitstudio2016.circuits;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EulerTestActivity extends HamiltonActivity {

//    private EulerPath path;
//    private DrawView drawView;
//    private RelativeLayout layout;
//    int screenX, screenY;
//    String message;

    private RelativeLayout relativeLayout;
    private int currentNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_graph_test);
        relativeLayout = (RelativeLayout) findViewById(R.id.graph_test_relative);
        init(new HamiltonCircuit(getGraph()), relativeLayout);

        message = "";
        if (this.getIntent().hasExtra("message")) {
            message = this.getIntent().getStringExtra("message");
            currentNum =  this.getIntent().getIntExtra("currentNum", 1);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        getLayout().addView(endButton, lp);

        //make message
        TextView messageView = new TextView(this);

        String text = "(E)" + message;
        messageView.setText(text);
        messageView.setTextSize(Math.round(screenX/43.2));
        messageView.setX(Math.round(screenX/33.75));
        messageView.setY(Math.round(screenY/38.4));
        messageView.setTextColor(getResources().getColor(R.color.neon_green));
        relativeLayout.addView(messageView, lp);
        super.getDrawView().setEuler();
    }


    @Override
    public void activateVertex(float x, float y){
        for(Vertex vrtx: super.getPath().getGraph().getVertices()){
            if(nearVertex(x,y, vrtx)){
                super.getPath().tryEulerActivate(vrtx);
                checkWon();
            }
        }
    }

    @Override
    public void checkWon(){
        if(super.getPath().isEulerFinished()){
            super.getDrawView().beDone();

//            Button endButton = new Button(this);
//            endButton.setText("Go Back");
//            endButton.setX(screenX/3);
//            endButton.setY(screenY*2/5);
//            endButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenX/3, screenY/12);
//            relativeLayout.addView(endButton, lp);
            Toast t1 = Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_LONG);
            t1.show();
        }
    }

    public void switchMode(View w) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        System.out.println(currentNum);
        bundle.putParcelable("graph", new Graph(getGraph()));
        intent.putExtras(bundle);
        String message = "Graph " + (Integer.toString(currentNum));
        intent.putExtra("message", message);
        intent.setClass(this, HamiltonTestActivity.class);
        //intent.setAction("circuit");
        startActivity(intent);
        finish();

    }

    public int getCurrentNum() {
        return currentNum;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(!super.getPath().isEulerFinished()) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    super.getDrawView().setMouseLocation(e.getX(), e.getY());
                    activateVertex(e.getX(), e.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    super.getDrawView().setMouseLocation(e.getX(), e.getY());
                    activateVertex(e.getX(), e.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    super.getPath().reset();
                    break;
            }
            super.getDrawView().invalidate();
        }
        return true;
    }
}
