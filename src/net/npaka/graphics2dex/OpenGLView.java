package net.npaka.graphics2dex;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//OpenGLのビュー
public abstract class OpenGLView extends SurfaceView 
    implements SurfaceHolder.Callback, Runnable {
    private ViewAnimator  animator; //アニメーター
    private SurfaceHolder holder;   //ホルダー
    private Thread        thread;   //スレッド
    private boolean       running;  //スレッドフラグ
    private int           width;    //幅
    private int           height;   //高さ
    private boolean       resize;   //リサイズフラグ
    private int           fps;      //FPS
    
//====================
//初期化
//====================
    //コンストラクタ
    public OpenGLView(Context c) {
        this(c,-1);
    }

    //コンストラクタ
    public OpenGLView(Context c,int fps) {
        super(c);
        holder=getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        this.fps=fps;
    }

    //ウィンドウのアタッチ
    @Override
    protected void onAttachedToWindow() {
        if (animator!=null) {
            animator.start();
        }
        super.onAttachedToWindow();
    }

    //ウィンドウのデタッチ
    @Override
    protected void onDetachedFromWindow() {
        if (animator!=null) {
            animator.stop();
        }
        super.onDetachedFromWindow();
    }

    //サーフェイスの変更
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {
        synchronized (this) {
            this.width =width;
            this.height=height;
            this.resize=true;
        }
    }

    //サーフェイスの生成
    public void surfaceCreated(SurfaceHolder holder) {
        thread=new Thread(this);
        thread.start();
    }

    //サーフェイスの破棄
    public void surfaceDestroyed(SurfaceHolder arg0) {
        running=false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
        }
        thread=null;
    }
    
//====================
//定期処理
//====================
    //定期処理
    public void run() {
    	//EAGLオブジェクト
    	EGL10 egl=(EGL10)EGLContext.getEGL();
        EGLDisplay dpy=egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        
        //バージョン
        int[] version=new int[2];
        egl.eglInitialize(dpy,version);
        
        //コンフィグ
        int[] configSpec={
            EGL10.EGL_RED_SIZE,   5,
            EGL10.EGL_GREEN_SIZE, 6,
            EGL10.EGL_BLUE_SIZE,  5,
            EGL10.EGL_DEPTH_SIZE,16,
            EGL10.EGL_NONE
        };        
        EGLConfig[] configs=new EGLConfig[1];
        int[] num_config=new int[1];
        egl.eglChooseConfig(dpy,configSpec,configs,1,num_config);
        EGLConfig config=configs[0];        
        EGLContext context=egl.eglCreateContext(dpy,config,
            EGL10.EGL_NO_CONTEXT,null);        
        EGLSurface surface=egl.eglCreateWindowSurface(dpy,config,holder,null);
        egl.eglMakeCurrent(dpy,surface,surface,context);
        
        //描画オブジェクト
        GL10 gl=(GL10)context.getGL();
        init(gl);
        
        //インターバル
        int delta=-1;
        if (fps>0) {
            delta=1000/fps;
        }
        long time=System.currentTimeMillis();
        
        running=true;
        while (running) {
            //サイズ
        	int w,h;
            synchronized(this) {
                w=width;
                h=height;
            }
            
            //インターバル
            if (System.currentTimeMillis()-time<delta) {
                try {
                    Thread.sleep(System.currentTimeMillis()-time);
                } catch (InterruptedException ex) {
                }
            }
            
            //描画
            drawFrame(gl,w,h);
            egl.eglSwapBuffers(dpy,surface);

            //エラー
            if (egl.eglGetError()==EGL11.EGL_CONTEXT_LOST) {
                Context c=getContext();
                if (c instanceof Activity) {
                    ((Activity)c).finish();
                }
            }
            time=System.currentTimeMillis();
        }
        //後処理
        egl.eglMakeCurrent(dpy,EGL10.EGL_NO_SURFACE,EGL10.
        	EGL_NO_SURFACE,EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy,surface);
        egl.eglDestroyContext(dpy,context);
        egl.eglTerminate(dpy);
    }    
   
    //初期化
    protected void init(GL10 gl) {}
    
    //フレームの描画
    protected abstract void drawFrame(GL10 gl);

    //フレームの描画
    private void drawFrame(GL10 gl,int w,int h) {
        if (resize) {
            resize(gl,w,h);
            resize=false;
        }
        drawFrame(gl);
    }
    
    //リサイズ
    protected void resize(GL10 gl,int w,int h) {}
}