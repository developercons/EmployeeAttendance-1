package com.codeian.employeeattendance;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.codeian.employeeattendance.Helpers.Settings;
import com.codeian.employeeattendance.Model.EmployeeList;
import com.codeian.employeeattendance.Network.NetworkManager;
import com.codeian.employeeattendance.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a single Employee detail screen.
 * This fragment is either contained in a {@link EmployeeListActivity}
 * in two-pane mode (on tablets) or a {@link EmployeeDetailActivity}
 * on handsets.
 */
public class EmployeeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_USER_NAME ="user_name";
    public static final String ARG_USER_EMAIL = "user_email";

    String getArgUserName,getArgUserEmail;


    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    private List<EmployeeList> mValues;

//    private String apiBase = Settings.INSTANCE.getApiUrl();
    private String apiBase = "http://bvigrimscloud.com/employee-attendance/public/api";

    String fpData,nfcData,uID;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EmployeeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            uID = getArguments().getString(ARG_ITEM_ID);
            getArgUserName = getArguments().getString(ARG_USER_NAME);
            getArgUserEmail = getArguments().getString(ARG_USER_EMAIL);

            String getPosition = getArguments().getString(ARG_ITEM_ID);
            int position = Integer.parseInt(getPosition);
            //mValues = new ArrayList<>();

            //EmployeeList singleEmployee;
            Log.e("ID", Integer.toString(position));
            setHistoryDate();
            getUserData();

//            singleEmployee = mValues.get(position);
//
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(singleEmployee.getName());
//            }
        }
    }

    public void setHistoryDate(){
        //WeekDay
        Calendar sCalendar = Calendar.getInstance();
        String weekDay = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        //Today Date
        Date c = Calendar.getInstance().getTime();
//        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String todayDate = df.format(c);
    }
    public void getUserData(){
        // Initialize a new StringRequest
        String mUrlString = apiBase+"/user/"+uID;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                mUrlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TODO Parse JSON AND SHOW DATA TO USER CARD.
                        // Do something with response string
                        try {
                            JSONObject respond = new JSONObject(response);
                            String isSuccess = respond.getString("success");

                            if(isSuccess.equals("true")){
                                JSONObject jObject = respond.getJSONObject("userInfo");
                                JSONObject jObjectUser = jObject.getJSONObject("user");

                                JSONArray userHistoryArray = jObject.getJSONArray("user_history");
                                JSONObject userHistory = userHistoryArray.getJSONObject(0);

                                //Log.e("USER HISTORY",Integer.toString(userHistory.length()));

                                if(userHistory.length() > 0 ){
                                    String userActivityHistory = userHistory.toString();
                                    Log.e("USER HISTORY",userActivityHistory);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("UserDATA",response);
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
        stringRequest.setShouldCache(false);
        // Add StringRequest to the RequestQueue
        NetworkManager.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.employee_detail, container, false);

        ((TextView) rootView.findViewById(R.id.userName)).setText(getArgUserName);
        ((TextView) rootView.findViewById(R.id.userEmail)).setText(getArgUserEmail);

        return rootView;
    }
}
