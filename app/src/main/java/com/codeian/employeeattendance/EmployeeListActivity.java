package com.codeian.employeeattendance;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.codeian.employeeattendance.Helpers.Hash;
import com.codeian.employeeattendance.Helpers.Settings;
import com.codeian.employeeattendance.Model.EmployeeList;
import com.codeian.employeeattendance.Network.NetworkManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An activity representing a list of Employees. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EmployeeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EmployeeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static List<EmployeeList> employeeListFiltered;
    private List<EmployeeList> employeeList;
    private SearchView searchView;
    RecyclerView recyclerView;

    SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPass();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.employee_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        recyclerView = findViewById(R.id.employee_list);
        assert recyclerView != null;
        checkConfig();

        getAllUser();
    }

    private void checkConfig(){
        String apibase = Settings.INSTANCE.getApiUrl();

        if(apibase.isEmpty()){
            Intent settings = new Intent(EmployeeListActivity.this,SettingsPrefActivity.class);
            settings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(settings);
        }

    }

    private void checkPass(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_pop, null);
        dialogBuilder.setView(dialogView);

        final EditText adminPass = (EditText) dialogView.findViewById(R.id.passWord);
        final TextView wrongPass = (TextView) dialogView.findViewById(R.id.passWordNotice);

        dialogBuilder.setTitle("Enter Admin Password");
        dialogBuilder.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        final AlertDialog b = dialogBuilder.create();

        b.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) b).getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
                positiveButtonLL.gravity = Gravity.CENTER;
                button.setLayoutParams(positiveButtonLL);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //do something with edt.getText().toString();
                        String getPassInput = adminPass.getText().toString().trim();
                        Boolean isValid = Hash.matchPass(getPassInput);

                        if(isValid){
                            b.dismiss();
                            startActivity(new Intent(EmployeeListActivity.this, SettingsPrefActivity.class));
                            Log.e("Password","Matched");
                        }else{
                            wrongPass.setText("Enter a valid password.");
                            wrongPass.setVisibility(View.VISIBLE);
                            Log.e("Password","Not Matched");
                        }
                        //Dismiss once everything is OK
                    }
                });
            }
        });

        b.show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, String response) {
        GsonBuilder builder = new GsonBuilder();
        Gson mGson = builder.create();
        List<EmployeeList> employeeLists = new ArrayList<EmployeeList>();
        String employees = null;
        try {
            JSONObject data = new JSONObject(response);
            String isSuccess = data.getString("success");

            if(isSuccess.equals("true")){
                JSONArray employeeArray = data.getJSONArray("employee_list");
                employees = employeeArray.toString();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        employeeLists = Arrays.asList(mGson.fromJson( employees , EmployeeList[].class));

        adapter = new SimpleItemRecyclerViewAdapter(this, employeeLists, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    public void getAllUser(){
        // Initialize a new StringRequest
        String mUrlString = "http://bvigrimscloud.com/employee-attendance/public/api/get_employee_list";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        setupRecyclerView((RecyclerView) recyclerView, response);
//                        Log.e("LIST",response);
                        //mTextView.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when get error
                        //Snackbar.make(mCLayout,"Error...",Snackbar.LENGTH_LONG).show();
                    }
                }
        );

        // Add StringRequest to the RequestQueue
        NetworkManager.getInstance(getBaseContext()).addToRequestQueue(stringRequest);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {

        private EmployeeListActivity mParentActivity;
        List<EmployeeList> mValues;
        private boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmployeeList item = (EmployeeList) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(EmployeeDetailFragment.ARG_ITEM_ID, item.getId().toString());
                    arguments.putString(EmployeeDetailFragment.ARG_USER_NAME, item.getName());
                    arguments.putString(EmployeeDetailFragment.ARG_USER_EMAIL, item.getEmail());

                    EmployeeDetailFragment fragment = new EmployeeDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.employee_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, EmployeeDetailActivity.class);
                    intent.putExtra(EmployeeDetailFragment.ARG_ITEM_ID, item.getId().toString());

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(EmployeeListActivity parent,
                                      List<EmployeeList> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
            employeeListFiltered = items;
        }

//        public SimpleItemRecyclerViewAdapter() {
//
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.employee_list_content, parent, false);
            return new ViewHolder(view);
        }

        public void updateAdapterData(List<EmployeeList> newlist) {
            mValues = newlist;
            this.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            //holder.mIdView.setText(mValues.get(position).getId().toString());
            holder.mContentView.setText(mValues.get(position).getName());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();

                    Log.e("Filter DATA",charString);

                    if (charString.isEmpty()) {
                        employeeList = employeeListFiltered;
                    } else {
                        List<EmployeeList> FilteredEmployeeList = new ArrayList<>();
                        for (EmployeeList row : mValues) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                                Log.e("MATCHED DATA",row.getName());

                                FilteredEmployeeList.add(row);
                            }
                        }

                        employeeList = FilteredEmployeeList;


                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = employeeList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mValues = (List<EmployeeList>) filterResults.values;
                    adapter.notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            //final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                //mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.tvName);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //final SimpleItemRecyclerViewAdapter adapter = new SimpleItemRecyclerViewAdapter();
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
//                Log.e("SEARCH DATA",query);
//                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);

                //Log.e("SEARCH DATA",query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

}
