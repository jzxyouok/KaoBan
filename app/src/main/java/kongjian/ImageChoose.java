package kongjian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lc.com.nui.multiphotopicker.adapter.ImageGridAdapter;
import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.IntentConstants;
import tempvalue.UserNum;

/**
 * ͼƬѡ��
 *
 */
public class ImageChoose extends Activity
{
    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String mBucketName;
    private int availableSize;
    private GridView mGridView;
    private TextView mBucketNameTv;
    private TextView cancelTv;
    private ImageGridAdapter mAdapter;
    private Button mFinishBtn;
    private HashMap<String, ImageItem> selectedImgs = new HashMap<String, ImageItem>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_image_choose);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);

        mDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                IntentConstants.EXTRA_IMAGE_LIST);
        if (mDataList == null) mDataList = new ArrayList<ImageItem>();
        mBucketName = getIntent().getStringExtra(
                IntentConstants.EXTRA_BUCKET_NAME);

        if (TextUtils.isEmpty(mBucketName))
        {
            mBucketName = "请选择";
        }
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);

        initView();
        initListener();

    }

    private void initView()
    {
        mBucketNameTv = (TextView) findViewById(R.id.title);
        mBucketNameTv.setText(mBucketName);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImageGridAdapter(ImageChoose.this, mDataList);
        mGridView.setAdapter(mAdapter);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        cancelTv = (TextView) findViewById(R.id.action);


        mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                + availableSize + ")");
        mAdapter.notifyDataSetChanged();
    }

    private void initListener()
    {
        mFinishBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(ImageChoose.this,
                        SelfActivity.class);
                intent.putExtra("userNum", UserNum.userNum);
                intent.putExtra(
                        IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) new ArrayList<ImageItem>(selectedImgs
                                .values()));
                startActivity(intent);
                finish();
            }

        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ImageItem item = mDataList.get(position);
                if (item.isSelected) {
                    item.isSelected = false;
                    selectedImgs.remove(item.imageId);
                } else {
                    if (selectedImgs.size() >= availableSize) {
                        Toast.makeText(ImageChoose.this,
                                "最多选择" + availableSize + "张图片",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.isSelected = true;
                    selectedImgs.put(item.imageId, item);
                }

                mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                        + availableSize + ")");
                mAdapter.notifyDataSetChanged();
            }

        });

        cancelTv.setOnClickListener(new OnClickListener()
        {


            public void onClick(View v)
            {
                Intent intent = new Intent(ImageChoose.this,
                        SelfActivity.class);
                intent.putExtra("userNum", UserNum.userNum);
                startActivity(intent);
                finish();
            }
        });

    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}