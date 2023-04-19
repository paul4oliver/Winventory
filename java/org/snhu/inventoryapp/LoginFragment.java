package org.snhu.inventoryapp;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

// Overview screen
public class LoginFragment extends Fragment {
    private InventoryDatabase dbHandler;
    private  String  mUsername;
    private  String  mPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = new InventoryDatabase(requireContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // editText for username
        EditText usernameEditText = rootView.findViewById(R.id.u_name_login);
        // editText for match to username
        EditText userMatchEditText = rootView.findViewById(R.id.u_match_login);

        // Authenticate user when sign_button clicked
        View signInButton = rootView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = userMatchEditText.getText().toString();

                mUsername = dbHandler.getUser(username).getUsername();
                mPassword = dbHandler.getUser(username).getUsernameMatch();

                // Check if username and password match
                if (username.equals(mUsername) && password.equals(mPassword)) {
                    ((MainActivity) getActivity()).setString(username); // Set the username so that user info can be retrieved elsewhere in the app
                    Navigation.findNavController(v).navigate(R.id.show_item_list); // Navigate to List Fragment
                } else {
                    Toast.makeText(activity, "Incorrect username/password, try again.", Toast.LENGTH_SHORT).show();
                    clearForm((ViewGroup) rootView.findViewById(R.id.login_fragment));
                }
            }
        });

        // Change view to register fragment when register_button clicked
        View registerButton = rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.register_fragment);
            }

        });

        return rootView;
    }

    // Method to Clear all editText in layout
    // Reference https://stackoverflow.com/questions/5740708/android-clearing-all-edittext-fields-with-clear-button
    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }
}