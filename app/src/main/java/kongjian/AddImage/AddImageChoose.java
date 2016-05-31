package kongjian.AddImage;

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

import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.AddNewArticalActivity;
import com.dezhou.lsy.projectdezhoureal.PinglunAddActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.ArticalBean;
import bean.Comment;
import bean.SecondComment;
import bean.Topic;
import lc.com.nui.multiphotopicker.adapter.ImageGridAdapter;
import lc.com.nui.multiphotopicker.model.ImageItem;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.IntentConstants;

/**
 * ͼƬѡ��
 *
 */
public class AddImageChoose extends Activity
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
    private DemoApplication app;
    private Topic topic;
    int flag=0;
    Intent intent=null;
    ArticalBean artical;
    Comment comment;
    SecondComment secondComment;
    String type;
    String commentStr;
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

        Intent intent=getIntent();
        flag=intent.getIntExtra("flag",0);
        commentStr=intent.getStringExtra("commentStr");
        comment= (Comment) intent.getSerializableExtra("comment");
        artical=(ArticalBean)intent.getSerializableExtra("artical");
        secondComment=(SecondComment)intent.getSerializableExtra("secondComment");
        type=intent.getStringExtra("type");
        app=(DemoApplication)getApplicationContext();
        topic=app.getTopic();
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
        mAdapter = new ImageGridAdapter(AddImageChoose.this, mDataList);
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
                if(flag==1){
                    intent=new Intent(AddImageChoose.this, PinglunAddActivity.class);
                    intent.putExtra("artical",(Serializable)artical);
                    intent.putExtra("comment",(Serializable)comment);
                    intent.putExtra("secondComment",(Serializable)secondComment);
                    intent.putExtra("flag",type);
                    intent.putExtra("commentStr",commentStr);
                }
                else {
                    intent = new Intent(AddImageChoose.this,
                            AddNewArticalActivity.class);
                }
                intent.putExtra("topic", topic);
                intent.putExtra(
                        IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) new ArrayList<ImageItem>(selectedImgs
                                .values()));
                intent.putExtra("commentStr",commentStr);
                startActivityForResult(intent,1);
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
                        Toast.makeText(AddImageChoose.this,
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
                selectedImgs=new HashMap<String, ImageItem>();
                if(flag==1){
                    intent=new Intent(AddImageChoose.this, PinglunAddActivity.class);
                    intent.putExtra("artical",(Serializable)artical);
                    intent.putExtra("comment",(Serializable)comment);
                    intent.putExtra("secondComment",(Serializable)secondComment);
                    intent.putExtra("flag",type);
                    intent.putExtra("commentStr",commentStr);
                }
                else {
                    intent = new Intent(AddImageChoose.this,
                            AddNewArticalActivity.class);
                }
                intent.putExtra("topic", topic);
                intent.putExtra(
                        IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) new ArrayList<ImageItem>(selectedImgs
                                .values()));
                intent.putExtra("commentStr",commentStr);
                startActivityForResult(intent, 1);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        selectedImgs=new HashMap<String, ImageItem>();
        if(flag==1){
            intent=new Intent(AddImageChoose.this, PinglunAddActivity.class);
            intent.putExtra("artical",(Serializable)artical);
            intent.putExtra("comment",(Serializable)comment);
            intent.putExtra("secondComment",(Serializable)secondComment);
            intent.putExtra("flag",type);
            intent.putExtra("commentStr",commentStr);
        }
        else {
            intent = new Intent(AddImageChoose.this,
                    AddNewArticalActivity.class);
        }
        intent.putExtra("topic", topic);
        intent.putExtra(
                IntentConstants.EXTRA_IMAGE_LIST,
                (Serializable) new ArrayList<ImageItem>(selectedImgs
                        .values()));
        intent.putExtra("commentStr",commentStr);
        startActivityForResult(intent, 1);
        finish();
    }
}