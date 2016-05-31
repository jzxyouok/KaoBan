package kongjian.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SchoolSqlitOpenHelper extends SQLiteOpenHelper {
	private Context context;

	public SchoolSqlitOpenHelper(Context context) {
		super(context, "school", null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		executeAssetsSQL(db, "city_info.sql");
		executeAssetsSQL(db, "province_info.sql");
		executeAssetsSQL(db, "shool_info.sql");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * 读取数据库文件（.sql），并执行sql语句
	 * */
	private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(context.getAssets()
					.open(schemaName)));

			// System.out.println("路径:"+Configuration.DB_PATH + "/" +
			// schemaName);
			String line;
			String buffer = "";
			while ((line = in.readLine()) != null) {
				buffer += line;
				if (line.trim().endsWith(";")) {
					db.execSQL(buffer.replace(";", ""));
					buffer = "";
				}
			}
		} catch (IOException e) {
			Log.e("db-error", e.toString());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				Log.e("db-error", e.toString());
			}
		}
	}
}
