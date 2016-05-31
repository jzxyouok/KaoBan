package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.dezhou.lsy.projectdezhoureal.R;


public class PaginationListView extends ListView implements OnScrollListener{
	//�ײ�View
	private View footerView;
	//ListView item����
	int totalItemCount = 0;
	//���ɼ���Item
	int lastVisibleItem = 0;
	//�Ƿ���ر�ʾ
	boolean isLoading = false;
	
	public PaginationListView(Context context) {
		super(context);
		initView(context);
	}
	
	public PaginationListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
	}

	public PaginationListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	/**
	 * ��ʼ��ListView
	 */
	private void initView(Context context){
		LayoutInflater mInflater = LayoutInflater.from(context);
		footerView = mInflater.inflate(R.layout.footer, null);
		footerView.setVisibility(View.GONE);
		this.setOnScrollListener(this);
		//��ӵײ�View
		this.addFooterView(footerView);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//���������׶ˣ�������״̬Ϊ not scrolling
		if(lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE){
			if(!isLoading){
				isLoading = true;
				//���ÿɼ�
				footerView.setVisibility(View.VISIBLE);
				//��������
				onLoadListener.onLoad();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}
	
	private OnLoadListener onLoadListener;
	public void setOnLoadListener(OnLoadListener onLoadListener){
		this.onLoadListener = onLoadListener;
	}
	
	/**
	 * �������ݽӿ�
	 * @author Administrator
	 *
	 */
	public interface OnLoadListener{
		void onLoad();
	}
	
	/**
	 * ���ݼ������
	 */
	public void loadComplete(){
		footerView.setVisibility(View.GONE);
		isLoading = false;
		this.invalidate();
	}

}
