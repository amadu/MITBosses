package com.example.tabactionbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends Activity {
	
	static ArrayList<String> testObjects = new ArrayList<String>();
	private final String todo = "Tasks";
	private Dialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
//		Intent fromLoginIntent = getIntent();
//		String email = fromLoginIntent.getStringExtra(LogInActivity.USERNAME);
		
		setupPushNotifications();
		
		ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		String todoLabel = getResources().getString(R.string.todo);
		Tab tab = actionBar.newTab();
		tab.setText(todoLabel);
		TabListener<ToDoFragment> tl = new TabListener<ToDoFragment>(this,
				todoLabel, ToDoFragment.class);
		tab.setTabListener(tl);
		actionBar.addTab(tab);

		String meetLabel = getResources().getString(R.string.meet);
		tab = actionBar.newTab();
		tab.setText(meetLabel);
		TabListener<MeetFragment> tl2 = new TabListener<MeetFragment>(this,
				meetLabel, MeetFragment.class);
		tab.setTabListener(tl2);
		actionBar.addTab(tab);
		
		String mapLabel = getResources().getString(R.string.map);
		tab = actionBar.newTab();
		tab.setText(mapLabel);
		TabListener<MapFragment> tl3 = new TabListener<MapFragment>(this,
				mapLabel, MapFragment.class);
		tab.setTabListener(tl3);
		actionBar.addTab(tab);
		
		String scheduleLabel = getResources().getString(R.string.schedule);
		tab = actionBar.newTab();
		tab.setText(scheduleLabel);
		TabListener<ScheduleFragment> tl4 = new TabListener<ScheduleFragment>(this,
				scheduleLabel, ScheduleFragment.class);
		tab.setTabListener(tl4);
		actionBar.addTab(tab);

	}
	
//	public static void removeObject(View v) {
//		ParseQuery query = new ParseQuery("TestObject");
//		query.getInBackground(testObjects.get(0), new GetCallback() {
//		  public void done(ParseObject object, ParseException e) {
//		    if (e == null) {
//		      object.deleteInBackground();
//		    } else {
//		      // something went wrong
//		    	System.out.println("Something went wrong");
//		    }
//		  }
//		});
//		testObjects.remove(0);
//	}

	private void setupPushNotifications() {
		PushService.subscribe(this, "", MainActivity.class);
		PushService.setDefaultPushCallback(this, MainActivity.class);
	}

	private class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}

	/**
	 * Popups the dialog in order to create a new task
	 * @param v
	 */
	public void createTask(View v) {
		Log.e("MainActivity", "The createTask method was called in this class");
		Dialog dialog = getTaskDialog();
		dialog.show();
	}
	
	/**
	 * Create Task Dialog Window
	 * @return
	 */
	private Dialog getTaskDialog() {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.create_task_dialog);
		dialog.setTitle(R.string.createTask);
		
		// Add button functionality to create
		Button newTaskButton = (Button) dialog.findViewById(R.id.submit_new_task);
		newTaskButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				submitNewTask(v);
			}
			
		});
		return dialog;
	}
	
	/**
	 * Persists the task in the Database and calls the ListFragment
	 * in order to update the list of activities
	 * @param v
	 */
	private void submitNewTask(View v) {
		Log.e("MainActivity", "The submitNewTask method was called in this class");

		HashMap<String, String> map = new HashMap<String,String>();
		
		// get the name from the editText of the popup window
		EditText taskName = (EditText) dialog.findViewById(R.id.edit_task_name_text);
		String name = taskName.getText().toString();
		taskName.setText("");
		dialog.dismiss();
		Log.e("MainActivity", "The value of taskName:" + name);

		
		// Map only a title/name of task
		map.put("name", name);
		map.put("creator", ParseUser.getCurrentUser().getUsername());
		ToDoFragment.updateListFragment(todo, map);
	}
	
	/**
	 * Called through the Meet fragment, and pops up a dialog window
	 * in order to select a course to add
	 * @param v
	 */
	public void showCourseAdder(View v) {
		final View view = v;
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_course_dialog);
		dialog.setTitle(R.string.addCourse);
		Button mButton = (Button) dialog.findViewById(R.id.add_course_button);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("Main", "addCourse");
				addCourseToUser(view);
			}
		});
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, MeetFragment.COURSES);        
		AutoCompleteTextView coursePick = (AutoCompleteTextView) dialog.findViewById(R.id.auto_complete_course);
		coursePick.setThreshold(1);
		coursePick.setAdapter(adapter);
		dialog.show();

	}
	
	/**
	 * Adds the course to the User's course list and makes a new course
	 * with
	 * @param v
	 */
	public void addCourseToUser(View v) {
		ParseUser currentUser = ParseUser.getCurrentUser();
		JSONArray courses = new JSONArray();
		for (Object course : currentUser.getList("courses")) {
			courses.put(course);
		}
		Log.e("Main", "Course List: "+ courses.toString());
		String courseNum = ((AutoCompleteTextView) dialog.findViewById(R.id.auto_complete_course)).getText().toString();
    	currentUser.put("courses", courses.put(courseNum));
		Log.e("Main", "Course List: "+ courses.toString());
    	currentUser.saveInBackground();
    	addUserToCourse(courseNum);
	}
	
	/**
	 * Adds the User to the course list of Users
	 */
	private void addUserToCourse(final String courseNum) {
		ParseQuery query = new ParseQuery(MeetFragment.COURSE_FIELD);
		query.whereEqualTo("number", courseNum);
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> courseList, ParseException e) {
		        if (e == null && !courseList.isEmpty()) {
		            //update the User object
		        	ParseObject object = courseList.get(0);
		        	JSONArray users = new JSONArray();
		        	for (Object user : object.getList("users")) {
		        		users.put(user);
		        	}
//		        	Log.e("adding course", users.toString());
		        	object.put("users", users.put(ParseUser.getCurrentUser().getUsername()));
		        	object.saveInBackground();
		        } else if (e == null && courseList.isEmpty()){
		        	Log.e("adding course", "made it");

		        	if (MeetFragment.COURSES.contains(courseNum)) {
		        		//create new course in DB
		        		ParseObject object = new ParseObject(MeetFragment.COURSE_FIELD);
		        		object.put("number", courseNum);
		        		object.put("users", new JSONArray().put(ParseUser.getCurrentUser().getUsername()));
		        		object.put("groups", new JSONArray());
		        		object.saveInBackground();
//			        	Log.e("adding course", object.getList("users").toString());

		        	}
		        } else {
		        	Log.d("course", "Error: " + e.getMessage());
		        }
		        MeetCourseFragment.updateCourseGridView();
	        	dialog.dismiss();
		    }
		});
	}
	
	/**
	 * Display a dialog box for creating a group
	 * @param v
	 */
	public void showCourseGroupDialog(View v) {
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.add_group_dialog);
		dialog.setTitle(R.string.addGroup);
		Button mButton = (Button) dialog.findViewById(R.id.create_group_dialog_button);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.e("Main", "addGroup");
				addGroupToCourse();
			}
		});
		dialog.show();
	}
	
	private void addGroupToCourse() {
		final EditText gNameField = (EditText) dialog.findViewById(R.id.group_name);
		final MeetGroupFragment frag = MeetFragment.getCurrentGroupFrag();
		
		if (!frag.getGroupList().contains(gNameField.getText().toString())) {
			// add the group to the course group list
			Log.e("Main", "Inside the addGroupToCourse");
			ParseQuery query = new ParseQuery(MeetFragment.COURSE_FIELD);
			query.whereEqualTo("number", frag.getCourseNum());
			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> tList, ParseException e) {
					if (e == null) {
						if (!tList.isEmpty()) {
							JSONArray groupList = new JSONArray();
							ParseObject course = tList.get(0);
							for (Object obj:course.getList("groups")) {
								groupList.put((String) obj);
							}
							groupList.put(gNameField.getText().toString());
							course.put("groups", groupList);
							course.saveInBackground();
							addGroup(gNameField.getText().toString());
							dialog.dismiss();
						}
					}
					else {
						// do something if the query is not successful
					}
				}
				
			});
		}
		else {
			// present an alert dialog stating the inability to create an identical group
			dialog.dismiss();
			new AlertDialog.Builder(this)
		    .setTitle("Duplicate Group")
		    .setMessage("Can Not Create Duplicate Group Name")
		    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // simply close the alert dialog
		        	dialog.dismiss();
		        }
		     })
		    .show();
		}
	}
	
	private void addGroup(String gName) {
		final MeetGroupFragment frag = MeetFragment.getCurrentGroupFrag();
		ParseObject group = new ParseObject(MeetFragment.GROUP_FIELD);
		group.put("name", gName);
		group.put("coursenum", frag.getCourseNum());
		group.saveInBackground();
		frag.updateGroupList();
	}

	public void deleteCourse(View v) {
		deleteCourseFromUser();
		deleteUserFromCourse();
	}

	private void deleteUserFromCourse() {
		final MeetGroupFragment frag = MeetFragment.getCurrentGroupFrag();
		
		ParseQuery query = new ParseQuery(MeetFragment.COURSE_FIELD);
		query.whereEqualTo("number", frag.getCourseNum());
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> courseList, ParseException e) {
		        if (e == null && !courseList.isEmpty()) {
		            //delete the User object
		        	ParseObject obj = courseList.get(0);
		        	ParseUser user = ParseUser.getCurrentUser();
		        	JSONArray arr = new JSONArray();
		        	for (Object object:obj.getList("users")) {
		        		if (!object.equals(user.getUsername())) {
		        			arr.put(object);
		        		}
		        	}
		        	obj.put("users", arr);
		        	
		        	arr = new JSONArray();
		        	for (Object object:user.getList("courses")) {
		        		if (!object.equals(obj.getString("number"))) {
		        			arr.put(object);
		        		}
		        	}
		        	user.put("courses", arr);
		        	
		        	user.saveInBackground();
		        	obj.saveInBackground();
		        	MeetFragment.revertHome();
		        }
		        else {
		        	// there was an error that should be handled
		        }
		    }
		});
		        	
		
	}

	private void deleteCourseFromUser() {
		// TODO Auto-generated method stub
		
	}

}
