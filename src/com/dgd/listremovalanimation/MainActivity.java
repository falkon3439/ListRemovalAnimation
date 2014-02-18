package com.dgd.listremovalanimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener
{
	private ListView mListView;
	private AnimatedArrayAdapter mAnimatedArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the listview from the layout
		mListView = (ListView) findViewById(R.id.listView);

		// initialize and fill the adapter
		mAnimatedArrayAdapter = new AnimatedArrayAdapter(this, mListView, R.layout.list_item, R.id.listItemTextView);
		fillAdapter();

		// set the adapter
		mListView.setAdapter(mAnimatedArrayAdapter);

		// set the click listener
		mListView.setOnItemClickListener(this);
	}

	public void fillAdapter()
	{
		mAnimatedArrayAdapter.clear();
		for (int i = 0; i < 30; i++)
		{
			mAnimatedArrayAdapter.add(new ListItem("Test Item " + i));
		}
	}

	// When we click on an item, remove it
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		mAnimatedArrayAdapter.animateRemoval(position);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.action_refresh)
		{
			fillAdapter();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
