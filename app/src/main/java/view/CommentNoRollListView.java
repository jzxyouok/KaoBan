package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

/**
 * Created by zy on 15-5-8.
 *
 * scrollview 和 listview嵌套时禁止listview滑动，这样不会冲突
 * 直接从网上抄下来的代码
 */
public class CommentNoRollListView extends ListView{

        public CommentNoRollListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /**
         * 设置不滚动
         */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    View.MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

        }
}
