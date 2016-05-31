package kongjian.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.com.easemob.chatuidemo.DemoApplication;
import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kongjian.db.SchoolDao;
import kongjian.pojo.City;
import kongjian.pojo.Provance;

public class ProvanceActivity extends Activity {
    private SchoolDao dao;
    private TextView title;
    private DemoApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.blue);
        final String pro=getIntent().getStringExtra("school_info");
        dao = new SchoolDao(this);
        List<Provance> listProvance = dao.queryProvance();
        app=(DemoApplication)getApplicationContext();

        title=(TextView)findViewById(R.id.title_school);
        title.setText("省份");

        ListView lv_provance = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        String[] from = { "pro_provance" };
        int[] to = { R.id.tv_item };
        for (Provance provance : listProvance) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("pro_provance", provance.getPr_province());
            map.put("pro_id", provance.getPr_id());
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(ProvanceActivity.this, data,
                R.layout.listview_item, from, to);
        lv_provance.setAdapter(adapter);
        lv_provance.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                ListView lv = (ListView) parent;
                HashMap<String, Object> itemAtPosition = (HashMap<String, Object>) lv
                        .getItemAtPosition(position);
                String pro_provance = (String) itemAtPosition
                        .get("pro_provance");
                String pro_id = (String) itemAtPosition.get("pro_id");
                ArrayList<City> listCity = dao.queryCityByProId(pro_id);
                app.setProvice(pro_provance);
                Intent intent = new Intent(getApplicationContext(),
                        CityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listCity", listCity);
                bundle.putSerializable("pro",pro);

                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
            // Toast.makeText(Main.this, pro_id + pro_provance, 1).show();

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
}
