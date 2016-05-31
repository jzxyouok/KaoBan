package kongjian;

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

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lc.com.nui.multiphotopicker.adapter.ImageBucketAdapter;
import lc.com.nui.multiphotopicker.model.ImageBucket;
import lc.com.nui.multiphotopicker.util.CustomConstants;
import lc.com.nui.multiphotopicker.util.ImageFetcher;
import lc.com.nui.multiphotopicker.util.IntentConstants;
import tempvalue.UserNum;


/**
 * 选择相册
 *
 */

public class ImageBucketChoose extends Activity
{
    private ImageFetcher mHelper;
    private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
    private ListView mListView;
    private ImageBucketAdapter mAdapter;
    private int availableSize;

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

                Intent intent = new Intent(ImageBucketChoose.this,
                        ImageChoose.class);
                intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) mDataList.get(position).imageList);
                intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
                        mDataList.get(position).bucketName);
                intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                        availableSize);

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
                Intent intent = new Intent(ImageBucketChoose.this,
                        SelfActivity.class);
                intent.putExtra("userNum", UserNum.userNum);
                startActivity(intent);
                finish();
            }
        });
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
