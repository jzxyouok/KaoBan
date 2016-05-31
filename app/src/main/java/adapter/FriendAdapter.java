package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dezhou.lsy.projectdezhoureal.R;

import java.util.ArrayList;

import network.Friend;

/**
 * Created by zy on 15-2-18.
 */
public class FriendAdapter extends BaseExpandableListAdapter {

    private String[] group;
    //    private String [][] friend;
    private ArrayList<ArrayList<Friend>> friend;
    private Context context;
    LayoutInflater layoutInflater;

    public FriendAdapter(Context context, String[] group, ArrayList<ArrayList<Friend>> friend) {
        this.context = context;
        this.group = group;
        this.friend = friend;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {

        return group.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return friend.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return group[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return friend.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.friend_group_item, null);

        TextView textView = (TextView) convertView.findViewById(R.id.text_group);
        textView.setText(group[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.friend_friend_item, null);

        Bitmap headImg = string2Bitmap(friend.get(groupPosition).get(childPosition).getHeadImg());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_friend_headimg);
        imageView.setImageBitmap(headImg);

        TextView textView = (TextView) convertView.findViewById(R.id.text_friend_name);
        textView.setText(friend.get(groupPosition).get(childPosition).getUserName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    private Bitmap string2Bitmap(String imgString) {
        byte[] imgByte = Base64.decode(imgString, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }
}
