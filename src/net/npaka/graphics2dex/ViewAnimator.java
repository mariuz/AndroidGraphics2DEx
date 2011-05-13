package net.npaka.graphics2dex;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

//ビューアニメーター
public class ViewAnimator extends Handler {
    //定数
    public static final int NEXT=0;
    
    //変数
    private boolean running;
    private View    view;
    private long    nextTime;
    private int     diff;
        
    //コンストラクタ
    public ViewAnimator(View view) {
        this(view, -1);
    }
    
    //コンストラクタ
    public ViewAnimator(View view, int fps) {
        running  =false;
        this.view=view;
        this.diff=1000/fps;
    }
    
    //スタート
    public void start() {
        if (!running) {
            running=true;
            Message msg=obtainMessage(NEXT);
            sendMessageAtTime(msg,SystemClock.uptimeMillis());
        }
    }
    
    //ストップ
    public void stop() {
        running=false;
    }
    
    //ハンドルメッセージ
    public void handleMessage(Message msg) {
        if (running && msg.what==NEXT) {
            view.invalidate();
            msg=obtainMessage(NEXT);
            long current=SystemClock.uptimeMillis();
            if (nextTime<current) {
                nextTime=current+diff;
            }
            sendMessageAtTime(msg,nextTime);
            nextTime+=diff;
        }
    }
}