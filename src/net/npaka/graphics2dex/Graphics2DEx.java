package net.npaka.graphics2dex;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

//OpenGLによる2Dグラフィックス
public class Graphics2DEx extends Activity {
    //アプリの初期化
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new Graphics2DView(this));
    }
}

