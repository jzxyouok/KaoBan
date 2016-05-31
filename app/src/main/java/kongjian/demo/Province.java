package kongjian.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kongjian.db.SchoolDao;
import kongjian.pojo.*;

/**
 * Created by wenjun on 2015/7/27.
 */
public class Province extends Activity {
    private SchoolDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mian);
        dao = new SchoolDao(this);
        List<Provance> listProvance = dao.queryProvance();

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
        SimpleAdapter adapter = new SimpleAdapter(Province.this, data,
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
                String pro_provance = (String) itemAtPosition
                        .get("pro_provance");
                String pro_id = (String) itemAtPosition.get("pro_id");
                ArrayList<kongjian.pojo.City> listCity = dao.queryCityByProId(pro_id);

                Intent intent = new Intent(getApplicationContext(),
                        City.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("listCity", listCity);
                intent.putExtras(bundle);
                startActivity(intent);

                // Toast.makeText(Main.this, pro_id + pro_provance, 1).show();
            }
        });
    }

}
