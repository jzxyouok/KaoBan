package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.SimplePeopleBean;
import roundimgview.RoundImgView;
import view.view.NoScrollGridView;


/**
 * Created by Administrator on 2015/7/29.
 */
public class SimilarPeopleAdapter extends BaseAdapter {
    List<SimplePeopleBean> simple_list;
    Context context;
    private LayoutInflater mInflater;
    private HashMap<String, SoftReference<Bitmap>> imageCache = null;
    BitmapFactory.Options option = new BitmapFactory.Options();

    public SimilarPeopleAdapter(Context context, List<SimplePeopleBean> simple_list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.simple_list = simple_list;

    }

    @Override
    public int getCount() {
            return simple_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        view = mInflater.inflate(R.layout.simple_item, null);
        viewHolder.userImg = (RoundImgView) view.findViewById(R.id.simple_list_head_img);
        viewHolder.birthday = (TextView) view.findViewById(R.id.simple_list_item_birthday);
        viewHolder.name = (TextView) view.findViewById(R.id.name);
        viewHolder.school = (TextView) view.findViewById(R.id.school);
        viewHolder.examsGridView=(NoScrollGridView)view.findViewById(R.id.exams_gridview);
        viewHolder.examsGridViewLit=(NoScrollGridView)view.findViewById(R.id.exams_gridviewlit);
        viewHolder.examsGridViewMid=(NoScrollGridView)view.findViewById(R.id.exams_gridview_mid);
        viewHolder.examsGridViewMax=(NoScrollGridView)view.findViewById(R.id.exams_gridview_max);
        viewHolder.sign = (TextView) view.findViewById(R.id.simple_item_sign);
        viewHolder.exam_list = (LinearLayout) view.findViewById(R.id.exam_list_min);
        viewHolder.name.setText(simple_list.get(i).getName());
        viewHolder.school.setText(simple_list.get(i).getSchool());
        String examListStr = simple_list.get(i).getExam();
        viewHolder.sign.setText(simple_list.get(i).getSign());
        String[] exams = examListStr.split(",");
        int flag = 0;

        if (simple_list.get(i).getBirthday() != null)
            viewHolder.birthday.setText(simple_list.get(i).getBirthday());
//        Bitmap bitmap = loadBitmap(simple_list.get(i).getHeadUrl());
//        if (bitmap != null) {
//            viewHolder.userImg.setImageBitmap(bitmap);
//        } else {
//            LoadBitmapAsyn loadBitmapAsyn = new LoadBitmapAsyn(viewHolder.userImg);
//            loadBitmapAsyn.execute(simple_list.get(i).getHeadUrl());
//        }
        Glide.with(context).load(simple_list.get(i).getHeadUrl()).into( viewHolder.userImg);

        List<String> examsLit=new ArrayList<String>();
        List<String> examsMin = new ArrayList<String>();
        List<String> examsMax = new ArrayList<String>();
        List<String> examsMid=new ArrayList<>();
        if(!examListStr.equals("")) {
            for (int j = 0; j < exams.length; j++) {
                String changeStr = exams[j].split("/")[1];
                try {
                    changeStr = changeStr.split(" ")[1];
                } catch (Exception e) {

                }
                if (changeStr.length() <= 4) {
                    examsLit.add(changeStr);

                }
                if (changeStr.length() > 4 && changeStr.length() <= 6) {
                    examsMin.add(changeStr);
                }
                if (changeStr.length() > 6 && changeStr.length() <= 9) {
                    examsMid.add(changeStr);
                }
                if (changeStr.length() > 9) {
                    examsMax.add(changeStr);
                }

            }
            ExamsGridViewAdapter adapter4=new ExamsGridViewAdapter(context,examsLit);
            viewHolder.examsGridViewLit.setAdapter(adapter4);
            ExamsGridViewAdapter adapter=new ExamsGridViewAdapter(context,examsMin);
            viewHolder.examsGridView.setAdapter(adapter);
            ExamsGridViewAdapter adapter2=new ExamsGridViewAdapter(context,examsMid);
            viewHolder.examsGridViewMid.setAdapter(adapter2);
            ExamsGridViewAdapter adapter3=new ExamsGridViewAdapter(context,examsMax);
            viewHolder.examsGridViewMax.setAdapter(adapter3);

        }

        return view;
    }

    public final class ViewHolder {
        public TextView name, school, address, sign, birthday;
        public LinearLayout exam_list;
        public RoundImgView userImg;
        public NoScrollGridView examsGridViewLit,examsGridView,examsGridViewMid,examsGridViewMax;
    }


}
