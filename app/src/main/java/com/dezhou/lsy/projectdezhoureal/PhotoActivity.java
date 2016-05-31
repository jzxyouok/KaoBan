package com.dezhou.lsy.projectdezhoureal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import utils.URLSet;

public class PhotoActivity extends Activity {
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    String[] imgPaths;
    ImageView photo;
    int flag=0;

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private ProgressDialog pd;
    PagerAdapter mPagerAdapter = new PagerAdapter(){

        @Override

        public int getCount() {
            // TODO Auto-generated method stub
            return pageview.size();
        }

        @Override

        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageview.get(arg1));
        }

        public Object instantiateItem(View arg0, int arg1){
            ((ViewPager)arg0).addView(pageview.get(arg1));
            return pageview.get(arg1);
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);
        Intent intent=getIntent();
        imgPaths=intent.getStringArrayExtra("imgPaths");
        flag=intent.getIntExtra("imgflag", -1);
        init();
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(flag);

    }
    public void init(){

        pd = ProgressDialog.show(PhotoActivity.this, "标题", "加载中，请稍后……");
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        LayoutInflater inflater =getLayoutInflater();
        pageview =new ArrayList<View>();
        for(int i=0;i<imgPaths.length;i++) {
            View view = inflater.inflate(R.layout.photo_item, null);
            String path= URLSet.serviceUrl +imgPaths[i];
//            Bitmap bitmap=null;
//            bitmap = loadBitmap(path);
            photo = (ImageView) view.findViewById(R.id.photo_img);
            try {
                URL url2 = new URL(path);
                Glide.with(getApplicationContext()).load(url2).into(photo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

//            finalBitmap = FinalBitmap.create(this);
//            finalBitmap.display(photo,path);


            photo.setOnTouchListener(new TouchListener());

            pageview.add(view);
        }
        pd.dismiss();

    }





    private final class TouchListener implements View.OnTouchListener {

        private int mode = 0;

        private static final int MODE_DRAG = 1;

        private static final int MODE_ZOOM = 2;

        private PointF startPoint = new PointF();

        private Matrix matrix = new Matrix();

        private Matrix currentMatrix = new Matrix();

        private float startDis;

        private PointF midPoint;
        @Override

        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:

                    mode = MODE_DRAG;

                    currentMatrix.set(photo.getImageMatrix());

                    startPoint.set(event.getX(), event.getY());

                    break;

                case MotionEvent.ACTION_MOVE:


                    if (mode == MODE_DRAG) {

                        float dx = event.getX() - startPoint.x;

                        float dy = event.getY() - startPoint.y;

                        matrix.set(currentMatrix);

                        matrix.postTranslate(dx, dy);

                    }

                    else if (mode == MODE_ZOOM) {

                        float endDis = distance(event);

                        if (endDis > 10f) {

                            float scale = endDis / startDis;

                            matrix.set(currentMatrix);

                            matrix.postScale(scale, scale,midPoint.x,midPoint.y);

                        }

                    }

                    break;

                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_POINTER_UP:

                    mode = 0;

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:

                    mode = MODE_ZOOM;


                    startDis = distance(event);


                    if (startDis > 10f) {

                        midPoint = mid(event);

                        currentMatrix.set(photo.getImageMatrix());

                    }

                    break;

            }

            photo.setImageMatrix(matrix);

            return true;

        }

        private float distance(MotionEvent event) {

            float dx = event.getX(1) - event.getX(0);

            float dy = event.getY(1) - event.getY(0);



            return FloatMath.sqrt(dx * dx + dy * dy);

        }





        private PointF mid(MotionEvent event) {

            float midX = (event.getX(1) + event.getX(0)) / 2;

            float midY = (event.getY(1) + event.getY(0)) / 2;

            return new PointF(midX, midY);

        }

    }
}
