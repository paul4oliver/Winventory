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
public class RegisterFragment extends Fragment {
    private InventoryDatabase dbHandler;
    private  String  mUsername;

    public RegisterFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        //  editText  for username
        EditText usernameEditText = rootView.findViewById(R.id.u_name);
        //  editText  for username match
        EditText userMatchEditText = rootView.findViewById(R.id.u_match);
        //  editText  for confirm username match
        EditText userConfirmMatchEditText = rootView.findViewById(R.id.confirm_u_match);
        //  editText  for confirm username match
        EditText phoneEditText = rootView.findViewById(R.id.phone_number);

        // Register user when sign_up_button clicked and return to login fragment
        View signUpButton = rootView.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = userMatchEditText.getText().toString();
                String confirmPassword = userConfirmMatchEditText.getText().toString();
                String phoneNumber = phoneEditText.getText().toString();

                mUsername = dbHandler.getUser(username).getUsername();

                // Check if optional phone number entered
                if(isBlank(phoneNumber)) {
                    phoneNumber = "0";
                }

                // Check that username, password, and confirm password fields are not empty
                if (isBlank(username) || isBlank(password) || isBlank(confirmPassword)){
                    Toast.makeText(activity, "Enter all fields!", Toast.LENGTH_SHORT).show();
                } else{

                    // Check that passwords match
                    if (!password.equals(confirmPassword)) {
                        Toast.makeText(activity, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        clearForm((ViewGroup) rootView.findViewById(R.id.register_fragment));
                    } else{
                        // Check that username is available/unique
                        if (mUsername == null) {
                            dbHandler.addUser(username, password, phoneNumber); // Add new user
                            Toast.makeText(activity, "Account Created!", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).navigate(R.id.back_to_login_fragment); // Navigate back to Login Fragment

                        }
                        else {
                            Toast.makeText(activity, "Username unavailable!", Toast.LENGTH_SHORT).show();
                            clearForm((ViewGroup) rootView.findViewById(R.id.register_fragment));
                        }
                    }
                }
            }
        });

        // Change view to login fragment when login_back clicked
        View SignInButton = rootView.findViewById(R.id.Login_back_button);
        SignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.back_to_login_fragment);
            }
        });

        return rootView;
    }

    // Method to check if a string is null/empty
    static boolean isBlank(String string) {
        return string == null || string.trim().length() == 0;
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