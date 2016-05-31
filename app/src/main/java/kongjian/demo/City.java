package kongjian.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dezhou.lsy.projectdezhoureal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kongjian.db.SchoolDao;
import kongjian.pojo.School;
import kongjian.EduActivity;

/**
 * Created by wenjun on 2015/7/27.
 */
public class City extends Activity {
    private SchoolDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);
        dao = new SchoolDao(this);
        ArrayList<kongjian.pojo.City> listCity = (ArrayList<kongjian.pojo.City>) getIntent().getExtras()
                .getSerializable("listCity");


        ListView lv_provance = (ListView) findViewById(R.id.listview);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        String[] from = { "ci_city" };
        int[] to = { R.id.tv_item };
        for (kongjian.pojo.City city : listCity) {
            Map<String, Object> map = new HashMap<String, Object>();
            // "ci_id", "ci_province", "ci_city"
            map.put("ci_id", city.getCi_id());
            map.put("ci_city", city.getCi_city());
            map.put("ci_province", city.getCi_province());
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(City.this, data,
                R.layout.listview_item, from, to);
        lv_provance.setAdapter(adapter);
        lv_provance.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                    Intent intent = new Intent(City.this,
                            EduActivity.class);
                    intent.putExtra("provice",city);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
