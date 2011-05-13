
package net.npaka.graphics2dex;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Paint;

//OpenGLによる2Dグラフィックスオブジェクト
public class Graphics2D {
    //テクスチャ頂点情報
    private final static float[] panelVertices=new float[]{
         0,  0, //左上
         0, -1, //左下
         1,  0, //右上
         1, -1, //右下
    };

    //テクスチャUV情報
    private final static float[] panelUVs=new float[]{
        0.0f, 0.0f, //左上
        0.0f, 1.0f, //左下
        1.0f, 0.0f, //右上
        1.0f, 1.0f, //右下
    }; 

    //情報
    private GL10  gl;               //グラフィックス    
    private int[] color={0,0,0,255};//色

    //ワーク
    private float[] vertexs=new float[256*3];//頂点    
    private float[] colors =new float[256*4];//色    

//====================
//初期化
//====================
    //コンストラクタ
    public Graphics2D(GL10 gl) {
        this.gl=gl;
    }
    
    //初期化
    public void init(int w,int h) {
        //ビューポート変換
        gl.glViewport(0,0,w,h);
        
        //投影変換
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-w/2,w/2,-h/2,h/2,-100,100);    
        gl.glTranslatef(-w/2,h/2,0);
        
        //モデリング変換    
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //クリア色の設定
        gl.glClearColor(1,1,1,1);
        
        //頂点配列の設定
        gl.glVertexPointer(2,GL10.GL_FLOAT,0,makeFloatBuffer(panelVertices));
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        //UVの設定
        gl.glTexCoordPointer(2,GL10.GL_FLOAT,0,makeFloatBuffer(panelUVs));
            
        //テクスチャの設定
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
            
        //ブレンドの設定
        gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL10.GL_BLEND);

        //ポイントの設定
        gl.glEnable(GL10.GL_POINT_SMOOTH);        
    }

//====================
//アクセス
//====================
    //クリア
    public void clear() {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
    
    //ライン幅の指定
    public void setLineWidth(float lineWidth) {
        gl.glLineWidth(lineWidth);
        gl.glPointSize(lineWidth*0.6f);
    }

    //色の指定
    public void setColor(int r,int g,int b,int a) {
        color[0]=r;
        color[1]=g;
        color[2]=b;
        color[3]=a;
    }

    //色の指定
    public void setColor(int r,int g,int b) {
        setColor(r,g,b,255);
    }

    //平行移動の指定　
    public void setTranslate(float x,float y) {
        gl.glTranslatef(x,-y,0);
    }

    //回転角度の指定
    public void setRotate(float rotate) {
        gl.glRotatef((float)(rotate*(-180)/Math.PI),0,0,1);
    }

    //スケールの指定
    public void setScale(float w,float h) {
        gl.glScalef(w,h,1);
    }
    
    //行列のプッシュ
    public void pushMatrix() {
        gl.glPushMatrix();
    }

    //行列のポップ
    public void popMatrix() {
        gl.glPopMatrix();
    }

//====================
//描画
//====================
    //ラインの描画
    public void drawLine(float x0,float y0,float x1,float y1) {
         //頂点配列情報
         vertexs[0]= x0;vertexs[1]=-y0;vertexs[2]=0;
         vertexs[3]= x1;vertexs[4]=-y1;vertexs[5]=0;     
         
         //カラー配列情報
         for (int i=0;i<2;i++) {
             colors[i*4  ]=color[0];
             colors[i*4+1]=color[1];
             colors[i*4+2]=color[2];
             colors[i*4+3]=color[3];
         }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_STRIP,0,2);
        gl.glPopMatrix();
    }

    //ポリラインの描画
    public void drawPolyline(float[] x,float y[],int length) {
        //頂点配列情報
        for (int i=0;i<length;i++) {
            vertexs[i*3+0]= x[i];
            vertexs[i*3+1]=-y[i];
            vertexs[i*3+2]=0;
        }
         
        //カラー配列情報
        for (int i=0;i<length;i++) {
            colors[i*4  ]=color[0];
            colors[i*4+1]=color[1];
            colors[i*4+2]=color[2];
            colors[i*4+3]=color[3];
        }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_STRIP,0,length);
            gl.glDrawArrays(GL10.GL_POINTS,0,length);
        gl.glPopMatrix();
    }
    
    //矩形の描画
    public void drawRect(float x,float y,float w,float h) {
         //頂点配列情報
         vertexs[0]= x;  vertexs[1] =-y;  vertexs[2] =0;
         vertexs[3]= x;  vertexs[4] =-y-h;vertexs[5] =0;  
         vertexs[6]= x+w;vertexs[7] =-y-h;vertexs[8] =0;
         vertexs[9]= x+w;vertexs[10]=-y;  vertexs[11]=0;  
         
         //カラー配列情報
         for (int i=0;i<4;i++) {
             colors[i*4  ]=color[0];
             colors[i*4+1]=color[1];
             colors[i*4+2]=color[2];
             colors[i*4+3]=color[3];
         }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_LOOP,0,4);
            gl.glDrawArrays(GL10.GL_POINTS,0,4);
        gl.glPopMatrix();
    }

    //矩形の塗り潰し
    public void fillRect(float x,float y,float w,float h) {
         //頂点配列情報
         vertexs[0]= x;  vertexs[1] =-y;  vertexs[2] =0;
         vertexs[3]= x;  vertexs[4] =-y-h;vertexs[5] =0;  
         vertexs[6]= x+w;vertexs[7] =-y;  vertexs[8] =0;
         vertexs[9]= x+w;vertexs[10]=-y-h;vertexs[11]=0;  
         
         //カラー配列情報
         for (int i=0;i<4;i++) {
             colors[i*4  ]=color[0];
             colors[i*4+1]=color[1];
             colors[i*4+2]=color[2];
             colors[i*4+3]=color[3];
         }

        //三角形の描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        gl.glPopMatrix();
    }

    //円の描画
    public void drawCircle(float x,float y,float r) {
        int length=100;
        
        //頂点配列情報
        for (int i=0;i<length;i++) {
            float angle=(float)(2*Math.PI*i/length);
            vertexs[i*3+0]=(float)( x+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-y+Math.sin(angle)*r);
            vertexs[i*3+2]=0;
        }
         
        //カラー配列情報
        for (int i=0;i<length;i++) {
            colors[i*4  ]=color[0];
            colors[i*4+1]=color[1];
            colors[i*4+2]=color[2];
            colors[i*4+3]=color[3];
        }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_LOOP,0,length);
            gl.glDrawArrays(GL10.GL_POINTS,0,length);
        gl.glPopMatrix();
    }

    //円の塗り潰し
    public void fillCircle(float x,float y,float r) {
        int length=100+2;
        
        //頂点配列情報
        vertexs[0]= x;
        vertexs[1]=-y;
        vertexs[2]=0;
        for (int i=1;i<length;i++) {
            float angle=(float)(2*Math.PI*i/(length-2));
            vertexs[i*3+0]=(float)( x+Math.cos(angle)*r);
            vertexs[i*3+1]=(float)(-y+Math.sin(angle)*r);
            vertexs[i*3+2]=0;
        }
         
        //カラー配列情報
        for (int i=0;i<length;i++) {
            colors[i*4  ]=color[0];
            colors[i*4+1]=color[1];
            colors[i*4+2]=color[2];
            colors[i*4+3]=color[3];
        }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN,0,length);
        gl.glPopMatrix();
    }

    //三角形の描画
    public void drawTriangle(float[] x,float[] y) {
         //頂点配列情報
         int length=3;
         for (int i=0;i<length;i++) {
             vertexs[i*3+0]= x[i];
             vertexs[i*3+1]=-y[i];
             vertexs[i*3+2]=0;
         }
         
         //カラー配列情報
         for (int i=0;i<length;i++) {
             colors[i*4  ]=color[0];
             colors[i*4+1]=color[1];
             colors[i*4+2]=color[2];
             colors[i*4+3]=color[3];
         }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_LOOP,0,length);
            gl.glDrawArrays(GL10.GL_POINTS,0,length);
        gl.glPopMatrix();
    }

    //三角形の塗り潰し
    public void fillTriangle(float[] x,float[] y) {
         //頂点配列情報
         int length=3;
         for (int i=0;i<length;i++) {
             vertexs[i*3+0]= x[i];
             vertexs[i*3+1]=-y[i];
             vertexs[i*3+2]=0;
         }
         
         //カラー配列情報
         for (int i=0;i<length;i++) {
             colors[i*4  ]=color[0];
             colors[i*4+1]=color[1];
             colors[i*4+2]=color[2];
             colors[i*4+3]=color[3];
         }

        //ラインの描画
        gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glVertexPointer(3,GL10.GL_FLOAT,0,makeFloatBuffer(vertexs));
        gl.glColorPointer(4,GL10.GL_FLOAT,0,makeFloatBuffer(colors));
        gl.glPushMatrix();
            gl.glDrawArrays(GL10.GL_LINE_LOOP,0,length);
            gl.glDrawArrays(GL10.GL_TRIANGLES,0,length);
            gl.glDrawArrays(GL10.GL_POINTS,0,length);
        gl.glPopMatrix();
    }    

    //テクスチャの描画
    public void drawTexture(Texture texture,float x,float y) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D,texture.name);
        gl.glVertexPointer(2,GL10.GL_FLOAT,0,makeFloatBuffer(panelVertices));
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glPushMatrix();
            gl.glTranslatef(x,-y,0);
            gl.glScalef(texture.size,texture.size,1);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        gl.glPopMatrix();
    }

    //テクスチャの描画
    public void drawTexture(Texture texture,float x,float y,float w,float h) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D,texture.name);
        gl.glVertexPointer(2,GL10.GL_FLOAT,0,makeFloatBuffer(panelVertices));
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glPushMatrix();
            gl.glTranslatef(x,-y,0);
            gl.glScalef(w*texture.size/texture.width,h*texture.size/texture.height,1);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,4);
        gl.glPopMatrix();
    }    
    
//====================
//テクスチャの生成
//===================
    //テクスチャの読み込み
    public Texture makeTexture(Bitmap bitmap) {
        //リサイズ
        int w=(bitmap.getWidth()>bitmap.getHeight())?bitmap.getWidth():bitmap.getHeight();
        int size=32;
        for (;size<1024;size*=2) {
            if (w<=size) break;
        }
        Bitmap result=resizeBitmap(bitmap,size,size);
        
        //画像データの生成
        ByteBuffer bb=ByteBuffer.allocateDirect(size*size*4);
        bb.order(ByteOrder.BIG_ENDIAN);
        IntBuffer ib=bb.asIntBuffer();
        for (int y=result.getHeight()-1;y>-1;y--) {
            for (int x=0;x<result.getWidth();x++) {
                int pix=result.getPixel(x,result.getHeight()-y-1);
                int alpha=((pix>>24)&0xFF);
                int red  =((pix>>16)&0xFF);
                int green=((pix>>8)&0xFF);
                int blue =((pix)&0xFF);
                ib.put((red<<24)+(green<<16)+(blue<<8)+alpha);            
            }
        }
        ib.position(0);
        bb.position(0);
        int[] textureName=new int[1];
        
        //テクスチャの設定
        gl.glGenTextures(1,textureName,0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D,textureName[0]);
        gl.glTexImage2D(GL10.GL_TEXTURE_2D,0,GL10.GL_RGBA,                
            result.getWidth(),result.getHeight(),
            0,GL10.GL_RGBA,GL10.GL_UNSIGNED_BYTE,bb);        
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR); 
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);        
        
        //テクスチャオブジェクトの生成
        Texture texture=new Texture();
        texture.name=textureName[0];
        texture.width=result.getWidth();
        texture.height=result.getHeight();
        texture.size=size;
        return texture;
    }

    //文字列テクスチャの生成
    public Texture makeTexture(String text,Paint paint) {
        int w=(int)paint.measureText(text);
        int h=(int)(paint.descent()-paint.ascent());
        Bitmap result=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
        canvas.drawText(text,0,(int)-paint.ascent(),paint);
        return makeTexture(result);   
    }
    
    //ビットマップのリサイズ
    private Bitmap resizeBitmap(Bitmap bmp,int w,int h) {
        Bitmap result=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(result);
        BitmapDrawable drawable=new BitmapDrawable(bmp);
        drawable.setBounds(0,0,bmp.getWidth(),bmp.getHeight());
        drawable.draw(canvas);
        return result;
    }    
    
//====================
//バッファの生成
//====================
    //Floatバッファの生成
    public static FloatBuffer makeFloatBuffer(float[] arr) {
        ByteBuffer bb=ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb=bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }

    //Floatバッファの生成
    public static IntBuffer makeFloatBuffer(int[] arr) {
        ByteBuffer bb=ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        IntBuffer ib=bb.asIntBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }
}