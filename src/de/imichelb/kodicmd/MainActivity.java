package de.imichelb.kodicmd;

import de.imichelb.kodicmd.adapter.NavDrawerListAdapter;
import de.imichelb.kodicmd.fragments.MusicLibFragment;
import de.imichelb.kodicmd.fragments.OptionsFragment;
import de.imichelb.kodicmd.fragments.RemoteFragment;
import de.imichelb.kodicmd.fragments.TwitchFragment;
import de.imichelb.kodicmd.fragments.YoutubeFragment;
import de.imichelb.kodicmd.model.NavDrawerItem;
import de.imichelb.kodicmd.R;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private int currentFragId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		initMenu();		
		initActionBar();
		initOptions();
		
		//Set the App title
		mTitle = mDrawerTitle = getTitle();

		//Display "Start" Fragment
		if (savedInstanceState == null) {

			displayView(0);
		}
	}
	
	/*
	 * Sets up the Drawer Menu
	 */
	private void initMenu() {
		
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// Remote
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// MusicLib
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Twitch
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Youtube
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Options
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));	

		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);
	}
	
	/*
	 * Sets up the ActionBar
	 */
	private void initActionBar() {
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, 
				R.string.app_name, 
				R.string.app_name
		) {
			public void onDrawerClosed(View view) {
				
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	/*
	 * This method should be the first to create the Options Object
	 */
	private void initOptions(){
		
		Options opt = Options.getInstance();
		Persistance pers = new PersitanceImpl(this);
		
		opt.setPersistanceManager(pers);
		opt.init();
	}
	
	/*
	 * Click Listener for the Menu
	 */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			displayView(position);
		}
	}
	
	/*
	 * Displays the fragment
	 */
	private void displayView(int position) {

		Fragment fragment = null;
		
		switch (position) {
		
			case 0:
				fragment = new RemoteFragment(this);
				break;
			case 1:
				fragment = new MusicLibFragment(this);
				break;
			case 2:
				fragment = new TwitchFragment(this);
				break;
			case 3:
				fragment = new YoutubeFragment();
				break;
			case 4:
				fragment = new OptionsFragment(this);
				break;
			default:
				break;
		}

		if (fragment != null) {
		
			FragmentManager fragmentManager = getFragmentManager();
			
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
			
			currentFragId = position;

		} else {
			
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			
			return true;
		}

		switch (item.getItemId()) {
			
			//Display Options Fragment
			case R.id.action_settings:
				displayView(4);
				return true;
				
			//Close the App
			case R.id.action_close:
				finish();
				return true;
				
			//Refresh current active fragment
			case R.id.action_refresh:
				displayView(currentFragId);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		
		//hide optionmenu items if the drawer menu is open
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.action_close).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void setTitle(CharSequence title) {
	
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		
		//Do nothing
	}
	
}
