package hit.treasure.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private int[] imageIDs = new int[] { R.drawable.calculator,
			R.drawable.ball, R.drawable.qq, R.drawable.map };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GridView gridView = (GridView) findViewById(R.id.gridView);

		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int imageID : imageIDs) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", imageID);
			listItems.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, listItems,
				R.layout.grid, new String[] { "image" },
				new int[] { R.id.imageItem });
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				Log.d("case", "" + arg2);
				switch (arg2) {
				case 0:
					startActivity(new Intent(MainActivity.this, 
							CalculatorActivity.class));
					break;
				case 1:
					
					break;
				case 2:
					startActivity(new Intent(MainActivity.this, 
							ClientActivity.class));
					break;
				case 3:
					startActivity(new Intent(MainActivity.this, 
							MapActivity.class));
					break;
				default:
					break;
				}
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid, menu);
		return true;
	}

}
