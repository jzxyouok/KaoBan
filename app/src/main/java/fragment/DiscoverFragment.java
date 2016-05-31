package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.ArticalListActivity;
import com.dezhou.lsy.projectdezhoureal.DocActvity;
import com.dezhou.lsy.projectdezhoureal.MainActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.dezhou.lsy.projectdezhoureal.TopicsMoreActivity;
import com.gallery.ImageAdapter;
import com.gallery.MyGallery;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.DiscoverAdapter;
import bean.Classification;
import kaobanxml.XmlParser;
import kongjian.PullToRefreshView;
import network.GalleryBean;
import network.MyHttpClient;
import tempvalue.MyArtical;
import tempvalue.UserNum;
import utils.URLSet;

/**
 * 发现主界面--话题列表
 */
public class DiscoverFragment extends Fragment  implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener{
    View v;
    ListView list;
    private MyGallery gallery;
    private ImageAdapter imageAdapter;
    private ArrayList<String> data;
    private int current = 0;
    private LinearLayout bottom;
    private static Map<String,String> simpleFlag=new HashMap<String,String>();
    private DiscoverAdapter faxianAdapter;
    List<Classification> topicList;
    private List<GalleryBean> list_gallery=new ArrayList<GalleryBean>();
    PullToRefreshView mPullToRefreshView;
    private ScrollView scrollView;
    Map<String,String> map;
    Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x122:
                    //接收到数据
                    Bundle bundle=msg.getData();
                    String titleStr=bundle.getString("faxian");
                    topicList=new ArrayList<Classification>();
                    if(titleStr.equals("")){
                        Toast.makeText(getActivity(),"数据连接有误",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        topicList = XmlParser.xmlClassificationGet(titleStr);
                        faxianAdapter = new DiscoverAdapter(getActivity(), topicList);
                        list.setAdapter(faxianAdapter);
                        setListViewHeight(list);

                    }
                    break;
                case 1:
                    Bundle bundle_gallery=msg.getData();
                    String title=bundle_gallery.getString("gallery");
                    if(title.equals(""))
                        break;
                    list_gallery=XmlParser.xmlGallery(title);
                    setgallery(list_gallery);
                    break;

            }
        }
    };

    private void setgallery(List<GalleryBean> list_gallery) {
        getData();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        imageAdapter = new ImageAdapter(getActivity(), data, width);
        gallery.setAdapter(imageAdapter);
        scrollView.smoothScrollTo(0,0);
        for (int i = 0; i < data.size(); i++) {
            ImageView point = new ImageView(getActivity());
            if (i == 0) {
                point.setBackgroundResource(R.drawable.feature_point_cur);
            } else {
                point.setBackgroundResource(R.drawable.feature_point);
            }
            bottom.addView(point);
        }
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                View _view = bottom.getChildAt(position);
                View _currentview = bottom.getChildAt(current);
                if (_currentview != null && _view != null) {
                    ImageView pointView = (ImageView) _view;
                    ImageView curpointView = (ImageView) _currentview;
                    curpointView
                            .setBackgroundResource(R.drawable.feature_point);
                    pointView
                            .setBackgroundResource(R.drawable.feature_point_cur);
                    current = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_faxian,null);
        initView();
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPullToRefreshView = (PullToRefreshView)getView().findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        gallery = (MyGallery) getView().findViewById(R.id.gy);
        bottom = (LinearLayout)getView().findViewById(R.id.ll);
        scrollView=(ScrollView)getView().findViewById(R.id.scrollview);
        scrollView.smoothScrollTo(0,0);
        data = new ArrayList<String>();
        if(MainActivity.isNetworkConnected(getActivity())) {
            initNetThread();
        }
        else{
            Toast.makeText(getActivity(),"当前无网络连接",Toast.LENGTH_SHORT).show();
        }



    }
    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void initView(){
        list=(ListView)v.findViewById(R.id.discover_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                for (int i = 0; i < topicList.size(); i++) {
                    if (topicList.get(i).getSerial() == position) {
                        Intent intent = new Intent(getActivity(), TopicsMoreActivity.class);
                        intent.putExtra("title", topicList.get(i));
                        startActivity(intent);
                    } else {
                        for (int m = 0; m < topicList.get(i).getTopics().size(); m++) {

                            if (topicList.get(i).getTopics().get(m).getSerial() == position) {
                                String topicName = topicList.get(i).getTopics().get(m).getTopicName();
                                if (topicName.equals("机密资料")) {
                                    Intent intent = new Intent(getActivity(), DocActvity.class);
                                    intent.putExtra("topic", topicName);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(getActivity(), ArticalListActivity.class);
                                    intent.putExtra("topic", topicList.get(i).getTopics().get(m));
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }
            }
        });

    }
    public void initNetThread(){


        initTopic();
        netWorkGallery();
    }
public void initTopic(){
    map=new HashMap<String,String>();
    map.put("userNum", UserNum.userNum);
    netWorkThread(URLSet.getClassificationURL, map, 0x122);
}
    public void measure(){
        scrollView.smoothScrollTo(0,0);
    }

    private void netWorkGallery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String gallery = MyHttpClient.doGet(client, URLSet.getGallery, map, true);
                Message message=new Message();
                message.what=1;
                Bundle bundle=new Bundle();
                if(gallery==null)
                    gallery="";
                bundle.putString("gallery",gallery);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    private void netWorkThread(final String url,final Map<String, String> mapTmp,final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();

                String topicStr = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();

                if(topicStr==null)
                    topicStr="";
                bundle.putString("faxian",topicStr);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }
    private void getData() {
        for (int i = 0; i < list_gallery.size(); i++) {
           String url_path = URLSet.serviceUrl+list_gallery.get(i).getTopicPic();
           String str2 = url_path.replaceAll(" ", "");
            data.add(str2.toString());
        }
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPullToRefreshView.onFooterRefreshComplete();
//                netWorkThread(URLSet.getClassificationURL, 0x122);
                initTopic();
            }
        }, 2000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {

                mPullToRefreshView.onHeaderRefreshComplete();
//                netWorkThread(URLSet.getClassificationURL, 0x122);
                initTopic();
            }
        }, 2000);

    }


    public void onResume(){
        super.onResume();
        if (MyArtical.fresh){
            initTopic();
        }
    }
}

