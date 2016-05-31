package fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.ArticalInfoActivity;
import com.dezhou.lsy.projectdezhoureal.ArticalListActivity;
import com.dezhou.lsy.projectdezhoureal.PhotoActivity;
import com.dezhou.lsy.projectdezhoureal.R;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.ArticalBean;
import kaobanxml.XmlParser;
import network.MyHttpClient;
import utils.URLSet;


public class ArticalFragment extends Fragment {

    LinearLayout myArticalLayout;
    public int page=1;
    Map<String,String> map;
    private DemoApplication app;
    private String username="";
    private ListView list;
    List<ArticalBean> articals;
    Handler handler=new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x222:
                    Bundle bundle=new Bundle();
                    bundle=msg.getData();
                    String storeStringXML=bundle.getString("myArticals");

                    if(storeStringXML.equals("")||storeStringXML==null){

                    }
                    else {
                        articals = XmlParser.xmlArticalsGet(storeStringXML);
                        ArticalAdapter adapter = new ArticalAdapter(getActivity(),articals);
                        list.setAdapter(adapter);
                        setListViewHeight(list);
                    }
                    break;
            }
        }
    };

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


    private class ListImgItemAdaper extends ArrayAdapter<String>
    {

        public ListImgItemAdaper(Context context, int resource, List<String> datas)
        {
            super(getActivity(), 0, datas);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            Glide.with(getActivity()).load(getItem(position)).into(imageview);
            //mImageLoader.loadImage(getItem(position), imageview, true);
            return convertView;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artical, null);
        myArticalLayout=(LinearLayout)view.findViewById(R.id.myartical_layout);
        list=(ListView)view.findViewById(R.id.artical);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), ArticalInfoActivity.class);
                intent.putExtra("artical", articals.get(position));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myArticalLayout.setOrientation(LinearLayout.VERTICAL);
        app=(DemoApplication)getActivity().getApplication();
        username = app.getName();
        initInfo();
    }
    public void initInfo() {

        map = new HashMap<String, String>();
        map.put("userNum", username);
        map.put("page", page + "");
        netWorkThread(URLSet.getMyArtical, map, 0x222);

    }
    private void netWorkThread(final String url, final Map<String, String> mapTmp,final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String storeString = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message=new Message();
                message.what=msgWhat;
                Bundle bundle=new Bundle();
                if(storeString==null){
                    storeString="";
                }
                bundle.putString("myArticals",storeString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    private class ArticalAdapter extends BaseAdapter {

        List<ArticalBean> articals;
        private LayoutInflater inflater;
        private int examType = 0;

        public ArticalAdapter(Context context, List<ArticalBean> articals) {
            this.articals = articals;
            inflater = LayoutInflater.from(context);
            Log.d("cout", "ExamAdapter");
        }

        @Override
        public int getCount() {
            return articals.size();
        }

        @Override
        public Object getItem(int position) {
            return articals.get(position).getUserNum();
        }

        @Override
        public long getItemId(int position) {
            Log.d("cout", "getItemId  " + position);
            return position;
        }

        @Override
        public View getView(final int i, View view, ViewGroup parent) {

            if (view == null) {
                view = this.inflater.inflate(R.layout.artical_list_item, null);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                //  layoutParams.height = 100;
                view.setLayoutParams(layoutParams);
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.userHead = (ImageView) view.findViewById(R.id.artical_list_user_img);
            viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
            viewHolder.school = (TextView) view.findViewById(R.id.artical_list_school);
            viewHolder.time = (TextView) view.findViewById(R.id.artical_time);
            viewHolder.content = (TextView) view.findViewById(R.id.text);
            viewHolder.likeNum = (TextView) view.findViewById(R.id.artical_list_like_num);
            viewHolder.storeNum = (TextView) view.findViewById(R.id.artical_list_shoucang_num);
            viewHolder.commandNum = (TextView) view.findViewById(R.id.artical_list_pinglun_num);
            viewHolder.location_artical=(TextView)view.findViewById(R.id.location_artical);
            viewHolder.location = (LinearLayout)view.findViewById(R.id.location_linear);
            viewHolder.deleteArtical = (TextView)view.findViewById(R.id.delete_artical);
            viewHolder.deleteArtical.setVisibility(View.GONE);

            //ImageView ifImg = (ImageView) view.findViewById(R.id.artical_list_item_if_img);
            viewHolder.imgsGrid=(GridView)view.findViewById(R.id.articalfragment_info_top_imgs);
            Glide.with(getActivity()).load(articals.get(i).getImgHeader()).into(viewHolder.userHead);
            viewHolder.userName.setText(articals.get(i).getUserName());
            viewHolder.school.setText(articals.get(i).getUserSchool());

            try {
                Date date = ArticalListActivity.ConverToDate(articals.get(i).getArticalTime());
                String timeago = ArticalListActivity.getTimeAgo(getActivity(),date);
                viewHolder.time.setText(timeago);
            } catch (Exception e) {
                e.printStackTrace();
            }

            viewHolder.content.setText(articals.get(i).getArticalcontent());


            if (articals.get(i).getLikeNum().equals("0")){
                viewHolder.likeNum.setText("点赞");
            }
            else {
                viewHolder.likeNum.setText(articals.get(i).getLikeNum());
            }
            if (articals.get(i).getStoreNum().equals("0")){
                viewHolder.storeNum.setText("收藏");
            }
            else {
                viewHolder.storeNum.setText(articals.get(i).getStoreNum());
            }

            if (articals.get(i).getCommendNum().equals("0")){
                viewHolder.commandNum.setText("评论");
            }
            else {
                viewHolder.commandNum.setText(articals.get(i).getCommendNum());
            }

            if (articals.get(i).getPostion().equals("")){
                viewHolder.location.setVisibility(View.GONE);
            }else{
                viewHolder.location_artical.setText(articals.get(i).getPostion());
            }
            if(!articals.get(i).getImg().equals("")) {
                final String imagePath = articals.get(i).getSmimg();
                final String[] imagePaths = imagePath.split(";");
                List<String> imagePathlist = new ArrayList<String>();
                for (int m = 0; m < imagePaths.length; m++) {
                    String imgpath = URLSet.serviceUrl + imagePaths[m];
                    imagePathlist.add(imgpath);
                }

                // mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
                if (imagePath != null) {
                    viewHolder.imgsGrid.setAdapter(new ListImgItemAdaper(getActivity(), 0,
                            imagePathlist));
                } else {
                    viewHolder.imgsGrid.setAdapter(null);
                }

                viewHolder.imgsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), PhotoActivity.class);

                        intent.putExtra("imgPaths", imagePaths);
                        intent.putExtra("imgflag", position);
                        startActivity(intent);
                    }
                });
            }
            return view;
        }
    }
    private class ViewHolder {
        public ImageView userHead;
        public TextView userName,school,time,content,likeNum,storeNum,commandNum,location_artical,deleteArtical;
        public LinearLayout location;
        public GridView imgsGrid;
    }

}
