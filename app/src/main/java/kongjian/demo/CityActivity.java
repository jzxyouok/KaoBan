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

import com.dezhou.lsy.projectdezhoureal.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kongjian.db.SchoolDao;
import kongjian.pojo.City;
import kongjian.pojo.School;


public class CityActivity extends Activity {
    private SchoolDao dao;

    private TextView title;
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
        final String ct=getIntent().getStringExtra("pro");
        dao = new SchoolDao(this);
        ArrayList<City> listCity = (ArrayList<City>) getIntent().getExtras()
                .getSerializable("listCity");

        title=(TextView)findViewById(R.id.title_school);
        title.setText("市区");

        ListView lv_provance = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        String[] from = { "ci_city" };
        int[] to = { R.id.tv_item };
        for (City city : listCity) {
            Map<String, Object> map = new HashMap<String, Object>();
            // "ci_id", "ci_province", "ci_city"
            map.put("ci_id", city.getCi_id());
            map.put("ci_city", city.getCi_city());
            map.put("ci_province", city.getCi_province());
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(CityActivity.this, data,
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
                String city = (String) itemAtPosition.get("ci_city");
                String ci_id = (String) itemAtPosition.get("ci_id");
                ArrayList<School> list_school = dao.querySchoolByCiId(ci_id);
                if (list_school != null && list_school.size() > 0) {
                    Intent intent = new Intent(getApplicationContext(),
                            SchoolActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list_school", list_school);
                    bundle.putSerializable("city",ct);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
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
