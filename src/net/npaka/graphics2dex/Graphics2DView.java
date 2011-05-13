package net.npaka.graphics2dex;
import javax.microedition.khronos.opengles.*;
import android.content.*;
import android.graphics.*;
import android.content.res.*;

//矩形の描画
public class Graphics2DView extends OpenGLView { 
    private Graphics2D g;//グラフィックス
    
    private Texture texture0;
    private Texture texture1;
    private Texture texture2;
    
    //コンストラクタ
    public Graphics2DView(Context c) {
        super(c,30);
        this.getContext();
    }

    //初期化
    protected void init(GL10 gl) {
        g=new Graphics2D(gl);
        
        //テクスチャの生成
        Resources res=getContext().getResources();        
        texture0=g.makeTexture(BitmapFactory.decodeResource(res,R.drawable.pic0));
        texture1=g.makeTexture(BitmapFactory.decodeResource(res,R.drawable.pic1));
        
        //文字列テクスチャの生成
        Paint paint=new Paint();      
        paint.setAntiAlias(true);
        paint.setTextSize(24);
        paint.setColor(Color.BLUE);
        texture2=g.makeTexture("そらみ",paint);
    }
    
    //リサイズ
    protected void resize(GL10 gl,int w,int h) {
        g.init(w,h);
        g.clear();
    }
    
    //フレームの描画
    protected void drawFrame(GL10 gl) {
        //バッファのクリア
        g.clear();
        
        //画像の描画    
        g.drawTexture(texture0,20,20);
        g.drawTexture(texture1,150,20);
        
        //文字列の描画
        g.drawTexture(texture2,180,20);
         
        //ラインの描画
        g.setColor(0,0,255);
        g.setLineWidth(2);
        g.drawLine(80,160,80,260);  
        
        //パスの描画
        float[] x={170,190,210,230,250,270};
        float[] y={160,260,160,260,160,260};
        g.setColor(255,0,0);
        g.setLineWidth(3);
        g.drawPolyline(x,y,6);
        
        //矩形の描画
        g.setColor(0,255,0);
        g.setLineWidth(1);
        g.drawRect(20,280,60,60);
        
        //矩形の塗り潰し
        g.setColor(255,255,0);
        g.setLineWidth(1);
        g.fillRect(100,280,60,60);
        
        //円の描画
        g.setColor(0,255,0);
        g.drawCircle(200,310,30);

        //円の塗り潰し
        g.setColor(0,0,255);
        g.fillCircle(270,310,30);
    }
}