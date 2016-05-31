package kongjian.db;

import java.util.ArrayList;
import java.util.List;

import kongjian.pojo.City;
import kongjian.pojo.Provance;
import kongjian.pojo.School;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SchoolDao {
	SchoolSqlitOpenHelper helper;

	public SchoolDao(Context context) {
		helper = new SchoolSqlitOpenHelper(context);
	}

	/**
	 * 查询所有省 包括特区直辖市
	 * 
	 * @return
	 */
	public List<Provance> queryProvance() {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			String[] columns = { "pr_id", "pr_province" };
			String selection = null;
			String[] selectionArgs = null;
			String groupBy = null;
			String having = null;
			String orderBy = null;
			Cursor cursor = db.query("province_info", columns, selection,
					selectionArgs, groupBy, having, orderBy);
			if (cursor != null && cursor.getCount() > 0) {
				List<Provance> list = new ArrayList<Provance>();
				String s1;
				String s2;
				while (cursor.moveToNext()) {
					s1 = cursor.getString(0);
					s2 = cursor.getString(1);
					list.add(new Provance(s1, s2));
				}
				if (cursor != null) {
					cursor.close();
				}
				return list;
			}
		}
		return null;
	}

	/**
	 * 根据pro_id 查找省内城市
	 * 
	 * @param pro_id
	 * @return
	 */
	public ArrayList<City> queryCityByProId(String pro_id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		if (db.isOpen()) {
			String[] columns = { "ci_id", "ci_province", "ci_city" };
			String selection = "ci_province=?";
			String[] selectionArgs = { pro_id };
			String groupBy = null;
			String having = null;
			String orderBy = null;
			Cursor cursor = db.query("city_info", columns, selection,
					selectionArgs, groupBy, having, orderBy);
			if (cursor != null && cursor.getCount() > 0) {
				ArrayList<City> listCity = new ArrayList<City>();
				String s1;
				String s2;
				String s3;
				while (cursor.moveToNext()) {
					s1 = cursor.getString(0);
					s2 = cursor.getString(1);
					s3 = cursor.getString(2);
					listCity.add(new City(s1, s2, s3));
				}
				if (cursor != null) {
					cursor.close();
				}
				return listCity;
			}
		}
		return null;
	}

	/**
	 * 根据ci_id 查找该城市所有的高校
	 * 
	 * @param ci_id
	 * @return
	 */
	public ArrayList<School> querySchoolByCiId(String ci_id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		if (db.isOpen()) {
			String[] columns = { "sh_id", "sh_city", "sh_shool" };
			String selection = "sh_city=?";
			String[] selectionArgs = { ci_id };
			Cursor cursor = db.query("shool_info", columns, selection,
					selectionArgs, null, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				ArrayList<School> listSchool = new ArrayList<School>();
				String sh_id;
				String sh_city;
				String sh_shool;
				while (cursor.moveToNext()) {
					sh_id = cursor.getString(0);
					sh_city = cursor.getString(1);
					sh_shool = cursor.getString(2);
					listSchool.add(new School(sh_id, sh_city, sh_shool));
				}
				if (cursor != null) {
					cursor.close();
				}
				return listSchool;
			}
		}
		return null;
	}
}
