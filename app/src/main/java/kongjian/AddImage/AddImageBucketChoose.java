package kongjian.AddImage;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.AddNewArticalActivity;
import com.dezhou.lsy.projectdezhoureal.PinglunAddActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bean.ArticalBean;
import bean.Comment;
import bean.SecondComment;
import bean.Topic;
import lc.com.nui.multiphotopicker.adapter.ImageBucketAdapter;
import lc.com.nui.multiphotopicker.model.ImageBucket;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.ImageFetcher;
import lc.com.nui.multiphotopicker.util.IntentConstants;


/**
 * 选择相册
 *
 */

public class AddImageBucketChoose extends Activity
{
    private ImageFetcher mHelper;
    private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
    private ListView mListView;
    private ImageBucketAdapter mAdapter;
    private int availableSize;
    private DemoApplication app;
    private Topic topic;
    int flag=0;
    String commentStr;
    Comment comment;
    ArticalBean artical;
    SecondComment secondComment;
    Intent intent;
    String type;//1代表一级评论，2代表回复评论的评论
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_image_bucket_choose);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);

        mHelper = ImageFetcher.getInstance(getApplicationContext());
        app=(DemoApplication)getApplicationContext();
        topic=app.getTopic();
        Intent intent=getIntent();
        flag=intent.getIntExtra("flag", 0);
        comment= (Comment) intent.getSerializableExtra("comment");
        artical=(ArticalBean)intent.getSerializableExtra("artical");
        type=intent.getStringExtra("type");
        secondComment=(SecondComment)intent.getSerializableExtra("secondComment");
        commentStr=(String)intent.getStringExtra("commentStr");
        initData();
        initView();
    }

    private void initData()
    {
        mDataList = mHelper.getImagesBucketList(false);
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);
    }

    private void initView()
    {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ImageBucketAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
        TextView titleTv  = (TextView) findViewById(R.id.title);
        titleTv.setText("相册");
        mListView.setOnItemClickListener(new OnItemClickListener()
        {


            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                selectOne(position);

                intent = new Intent(AddImageBucketChoose.this,
                        AddImageChoose.class);
                intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) mDataList.get(position).imageList);
                intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
                        mDataList.get(position).bucketName);
                intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                        availableSize);
                intent.putExtra("flag",flag);
                intent.putExtra("type",type);
                intent.putExtra("artical",(Serializable)artical);
                intent.putExtra("comment",(Serializable)comment);
                intent.putExtra("secondComment",(Serializable)secondComment);
                intent.putExtra("commentStr",commentStr);
                startActivity(intent);
                ////////////////////////
                finish();
            }
        });

        TextView cancelTv = (TextView) findViewById(R.id.action);
        cancelTv.setOnClickListener(new OnClickListener()
        {


            public void onClick(View v)
            {

                if(flag==1){
                    intent=new Intent(AddImageBucketChoose.this, PinglunAddActivity.class);
                    intent.putExtra("artical",(Serializable)artical);
                    intent.putExtra("comment",(Serializable)comment);
                    intent.putExtra("secondComment",(Serializable)secondComment);
                    intent.putExtra("flag",type);
                    intent.putExtra("commentStr",commentStr);
                }
                else {
                    intent = new Intent(AddImageBucketChoose.this,
                            AddNewArticalActivity.class);
                }
                intent.putExtra("topic", topic);

                intent.putExtra("commentStr",commentStr);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(flag==1){
            intent=new Intent(AddImageBucketChoose.this, PinglunAddActivity.class);
            intent.putExtra("artical",(Serializable)artical);
            intent.putExtra("comment",(Serializable)comment);
            intent.putExtra("secondComment",(Serializable)secondComment);
            intent.putExtra("flag",type);
            intent.putExtra("commentStr",commentStr);
        }
        else {
            intent = new Intent(AddImageBucketChoose.this,
                    AddNewArticalActivity.class);
        }
        intent.putExtra("topic", topic);

        intent.putExtra("commentStr",commentStr);
        startActivityForResult(intent, 1);
        finish();
    }

    private void selectOne(int position)
    {
        int size = mDataList.size();
        for (int i = 0; i != size; i++)
        {
            if (i == position) mDataList.get(i).selected = true;
            else
            {
                mDataList.get(i).selected = false;
            }
        }
        mAdapter.notifyDataSetChanged();
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
