package adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dezhou.lsy.projectdezhoureal.BaseActivity;
import com.dezhou.lsy.projectdezhoureal.R;

import java.util.List;

/**
 * Created by guochunpeng on 15/9/7.
 */
public class ExamsGridViewAdapter extends BaseAdapter {
    List<String> exams;
    Context context;
    LayoutInflater inflater;
    public ExamsGridViewAdapter(Context context,List<String> exams){
        this.exams=exams;
        this.context=context;

        inflater= LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return exams.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.exams_list_item_max, null);
            TextView textView = (TextView) view.findViewById(R.id.exams_layout_max);
            textView.setText(exams.get(i));

        return view;
    }
}
