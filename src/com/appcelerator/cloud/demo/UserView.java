package com.appcelerator.cloud.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appcelerator.cloud.sdk.CCConstants;
import com.appcelerator.cloud.sdk.CCMeta;
import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.CCUser;
import com.appcelerator.cloud.sdk.ACSClient;
import com.appcelerator.cloud.sdk.ACSClientError;
import com.appcelerator.cloud.sdk.oauth2.DialogError;
import com.appcelerator.cloud.sdk.oauth2.DialogListener;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.*;
import com.facebook.model.*;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class UserView extends Activity {

    static final int LAUNCH_SIGNUP = 0;
//    List<CCResponse> listOfCheckin;
    private ACSClient sdk;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sdk = DemoApplication.getSdk();
        try {
			if ( sdk.getCurrentUser() == null) {
				showLoginView();
			} else {
            	showUserView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        //Honeycomb and above don't allow networking on main thread, otherwise NetworkOnMainThreadException
        //will be thrown. Applications targeting earlier SDK versions are allowed to do networking on their 
        //main event loop threads, but it's heavily discouraged. Networking should be done in AsyncTasks.
        //This is a temporary fix to make login/logout/signup work. 
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        
    }
  
    @Override
    public void onPause()
    {
    	super.onPause();
    	// Ugly solution to hide the soft keyboard by forcing it to show first, then toggle it
    	InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 
    			InputMethodManager.HIDE_IMPLICIT_ONLY); 
    	inputManager.toggleSoftInput(0, 0);
    }
    
    protected void performLogin() {
		final ProgressDialog dialog = new ProgressDialog(UserView.this);
		dialog.setMessage("Login...");
	    dialog.show();
	    
    	String login = ((EditText) findViewById(R.id.email_address)).getText().toString();
    	String password = ((EditText) findViewById(R.id.pw)).getText().toString();
    	String errorMsg = null;
    	try {
	    	HashMap<String, Object> dataMap = new HashMap<String, Object>();
	    	dataMap.put("login", login);
	    	dataMap.put("password", password);
	    	CCResponse response = sdk.sendRequest("users/login.json", CCRequestMethod.POST, dataMap, false);
	    	CCMeta meta = response.getMeta();
	    	if( meta.getCode() != CCConstants.SUCCESS_CODE )
	    			throw new ACSClientError(meta.getMessage());
	    	
	    	showUserView();
		} catch (ACSClientError e) {
			errorMsg = e.getMessage();
    	} catch (IOException e) {
			errorMsg = e.getMessage();
		} catch (Exception e) {
			errorMsg = e.getMessage();
		}
		dialog.dismiss();
		
		if (errorMsg != null) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Login Failed");
    		alertDialog.setMessage(errorMsg);
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		   public void onClick(DialogInterface dialog, int which) {
    		      // here you can add functions
    			   dialog.dismiss();
    		   }
    		});
    		alertDialog.setIcon(R.drawable.icon);
    		alertDialog.show();
		}
    }
    
    protected void performFBLogin() {
		final ProgressDialog dialog = new ProgressDialog(UserView.this);
		dialog.setMessage("Login...");
	    dialog.show();


	    // start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      public void call(final Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	            	  Log.d("@@##", "FB user = "+user.toString());
	            	  
	            	  
	          	    String errorMsg = null;
	            	try {
	            		Map<String, Object> data = new HashMap<String, Object>();
	            		data.put("type", "facebook");
	            		data.put("token", session.getAccessToken());
	            		CCResponse response2 = sdk.sendRequest("users/external_account_login.json", CCRequestMethod.POST, data);
	            		JSONObject responseJSON = response2.getResponseData();
	            		CCMeta meta = response2.getMeta();
	            		if("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "externalAccountLogin".equals(meta.getMethod())) {
	            			JSONArray users = responseJSON.getJSONArray("users");
	            			Log.d("@@##", "users = "+users.toString());
	            			Toast.makeText(getApplicationContext(), "users = "+users.toString(), Toast.LENGTH_SHORT).show();
	            			Log.d("@@##", "sdk.getCurrentUser = "+sdk.getCurrentUser());
	            			if (sdk.getCurrentUser() == null) {
	            				Log.d("@@##", "sdk.getCurrentUser() null = "+sdk.getCurrentUser().toString());
	            			} else {
	            				Log.d("@@##", "sdk.getCurrentUser() = "+sdk.getCurrentUser().toString());
	            			}
	            		} else {
	            			Log.d("@@##", "FB login error, message = "+meta.getMessage());
	            			Toast.makeText(getApplicationContext(), "FB login error, message = "+meta.getMessage(), Toast.LENGTH_SHORT).show();
	            		}
	        	    	
	        	    	//showUserView();
	        		} catch (ACSClientError e) {
	        			errorMsg = e.getMessage();
	            	} catch (IOException e) {
	        			errorMsg = e.getMessage();
	        		} catch (Exception e) {
	        			errorMsg = e.getMessage();
	        		}
	        		dialog.dismiss();
	        		
	        		if (errorMsg != null) {
	        			AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
	            		alertDialog.setTitle("Login Failed");
	            		alertDialog.setMessage(errorMsg);
	            		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	            		   public void onClick(DialogInterface dialog, int which) {
	            		      // here you can add functions
	            			   dialog.dismiss();
	            		   }
	            		});
	            		alertDialog.setIcon(R.drawable.icon);
	            		alertDialog.show();
	        		}
	              }
	            }
	          });
	        }
	      }
	    });   
	    

    }
    
    protected void performRefresh() {
    	new GetCheckinsTask().execute();
    }
    
    protected void performLogout() {
    	
	    try {
	    	sdk.sendRequest("users/logout.json", CCRequestMethod.GET, null, false);
		} catch (ACSClientError e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		showLoginView();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SIGNUP) {
            if (resultCode == RESULT_OK) {
            	showUserView();
            }
        } else {
        	Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    protected void showLoginView()
    {
		setContentView(R.layout.signin);

        View loginButton = findViewById(R.id.signin);
        loginButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performLogin();
	    }});
        
        View signupButton = findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		startActivityForResult(new Intent(UserView.this, SignUp.class), LAUNCH_SIGNUP);
	    }});
        
        View acsAuthButton = findViewById(R.id.acsAuth); 
        acsAuthButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				performAuthorization();
		}});

        View acsSignUpButton = findViewById(R.id.acsSignUp); 
        acsSignUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				performACSSignUp();
		}});
        
        View fbLogin = findViewById(R.id.facebookAuth); 
        fbLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				performFBLogin();
		}});
    }
    
    
    protected void performAuthorization() {

		Toast.makeText(UserView.this, "Authorizing", Toast.LENGTH_SHORT).show();
		try {
			sdk.authorize(UserView.this, ACSClient.ACTION_LOGIN, new LoginDialogListener(), false);
		} catch (ACSClientError e) {
			Toast.makeText( UserView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

    }
    
    
    protected void performACSSignUp() {
		Toast.makeText(UserView.this, "Signing Up", Toast.LENGTH_SHORT).show();
		try {
			sdk.authorize(UserView.this, ACSClient.ACTION_SIGNUP, new LoginDialogListener());
		} catch (ACSClientError e) {
			Toast.makeText( UserView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    }
    
    protected void showUserView()
    {
        setContentView(R.layout.userview);
        View refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performRefresh();
	    }});
        
        View logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		performLogout();
        }});
       
        try {
            TextView name = (TextView)findViewById(R.id.UserName);
            CCUser user = sdk.getCurrentUser();
            if( user != null)
            	name.setText( user.getFirst() + " " + user.getLast() );
        } catch (Exception e) {
			e.printStackTrace();
        }
        performRefresh();
    }
  
    protected void showCheckins(List<JSONObject> checkins) {
        
    	if (checkins == null) {
    		return;
    	}
    	InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    		
        ListView list = (ListView)findViewById(R.id.CheckinListView);
        
        CheckinAdapter adapter = new CheckinAdapter(this, checkins, false);
        
        list.setAdapter(adapter);
    }
    
	private class GetCheckinsTask extends AsyncTask<Void, Void, List<JSONObject>> {
		private final ProgressDialog dialog = new ProgressDialog(UserView.this);
	    private String errorMsg = null;
	    protected void onPreExecute()
	    {
	        dialog.setMessage("Loading...");
	        dialog.show();
	    }
	    
	    protected void onPostExecute(List<JSONObject> checkins) {
	    	 if(this.dialog.isShowing())
	         {
	             this.dialog.dismiss();
	         }
	    	 if (errorMsg != null) {
	     		AlertDialog alertDialog = new AlertDialog.Builder(UserView.this).create();
	    		alertDialog.setTitle("Failed");
	    		alertDialog.setMessage(errorMsg);
	    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    		   public void onClick(DialogInterface dialog, int which) {
	    		      // here you can add functions
	    			   dialog.dismiss();
	    		   }
	    		});
	    		alertDialog.setIcon(R.drawable.icon);
	    		alertDialog.show();
	    	 } else {
	    		 showCheckins(checkins);
	    	 }
	    }

		@Override
		protected List<JSONObject> doInBackground(Void...params) {
			List<JSONObject> checkinsList = new ArrayList<JSONObject>();
			try {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("user_id", sdk.getCurrentUser().getObjectId() );
				CCResponse response = sdk.sendRequest("checkins/search.json", CCRequestMethod.GET, data, false);


				JSONObject responseJSON = response.getResponseData();
				CCMeta meta = response.getMeta();
				if("ok".equals(meta.getStatus()) 
				    && meta.getCode() == 200 
				    && "searchCheckins".equals(meta.getMethod())) {
				  try {
					JSONArray checkins = responseJSON.getJSONArray("checkins");
					for( int i = 0 ; i < checkins.length() ; i++ )
						checkinsList.add(checkins.getJSONObject(i) );
				  } catch (JSONException e) { }
				}
			} catch (ACSClientError e) {
				errorMsg = e.getLocalizedMessage();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return checkinsList;
		}
	}

	
	//***********************************************************************
	//***********************************************************************
	// LoginDialogListener - ypjin
	//***********************************************************************
	//***********************************************************************
	public final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			CCResponse result = null;
			String errorMsg = null;
			try {
				//The user has logged in, so now you can query and use their ACSClient info
				result = sdk.sendRequest("users/show/me.json", CCRequestMethod.GET, null, false);
				JSONObject resJson = result.getResponseData();
    			Toast.makeText( UserView.this, resJson.toString(), Toast.LENGTH_SHORT).show();
//				Toast.makeText( Connect.this, "Thank you for Logging In, " + firstName + " " + lastName + "!", Toast.LENGTH_SHORT).show();

				//save your access token here
				//sdk.getAccessToken();
				//sdk.getAccessExpires();
				
				showUserView();
				
			} catch( ACSClientError error ) {
				Toast.makeText( UserView.this, error.toString(), Toast.LENGTH_SHORT).show();
			} catch( Exception error ) {
				Toast.makeText( UserView.this, error.toString(), Toast.LENGTH_SHORT).show();
			}
		}
		
		public void onACSClientError(ACSClientError error) {
			Toast.makeText( UserView.this, "ACSClientError: " + error.toString(), Toast.LENGTH_LONG).show();
			//Toast.makeText( Connect.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
		}
		
		public void onError(DialogError error) {
			Toast.makeText( UserView.this, "DialogError: " + error.toString(), Toast.LENGTH_LONG).show();
			//Toast.makeText( Connect.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
		}
		 
        public void onCancel() {
			Toast.makeText( UserView.this, "Cancelled!", Toast.LENGTH_LONG).show();
        	//Toast.makeText( Connect.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
		}
	}
	
}
