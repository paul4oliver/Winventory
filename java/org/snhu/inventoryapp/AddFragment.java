package org.snhu.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

// Overview screen
public class AddFragment extends Fragment {
    private InventoryDatabase dbHandler;

    public AddFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        //Display editText and hint for new item name
        EditText nameTextView = rootView.findViewById(R.id.item_name);
        nameTextView.setHint("Enter item Name");
        //Display new item quantity
        EditText quantityTextView = rootView.findViewById(R.id.item_count);
        //Display editText and hint for new item description
        EditText descriptionTextView = rootView.findViewById(R.id.item_description);
        descriptionTextView.setHint("Enter a description");

        // Increase item quantity when increase button pressed
        View increaseButton = rootView.findViewById(R.id.increase_button);
        increaseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int count;

                try {
                    count = Integer.parseInt(quantityTextView.getText().toString());
                    count += 1;
                    quantityTextView.setText(String.valueOf(count));
                }
                catch (NumberFormatException e) {
                    // This is thrown when the String contains characters other than digits
                    System.out.println("Invalid count");
                }
            }
        });

        // Decrease item quantity when increase button pressed
        View decreaseButton = rootView.findViewById(R.id.decrease_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                int count;

                try {
                    count = Integer.parseInt(quantityTextView.getText().toString());
                    if(count >= 1) {
                        count -= 1;
                        quantityTextView.setText(String.valueOf(count));
                    } else {
                        Toast.makeText(activity, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NumberFormatException e) {
                    // This is thrown when the String contains characters other than digits
                    System.out.println("Invalid quantity");
                }
            }
        });

        // Add new item when check mark icon button pressed and return to list
        View addButton = rootView.findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                String iName = nameTextView.getText().toString();
                String iQuantity = quantityTextView.getText().toString();
                String iDesc = descriptionTextView.getText().toString();

                // Check that name and quantity are not empty/blank
                if(isBlank(iName) && isBlank(iQuantity)){
                    // Check that quantity is greater than or equal to 0
                    try {
                        if (Integer.parseInt(iQuantity) >= 0) {
                            dbHandler.addItem(iName, iQuantity, iDesc);
                            Navigation.findNavController(v).navigate(R.id.show_item_list);

                            Activity activity = getActivity();

                            if(Integer.parseInt(iQuantity) == 0) {
                                sendSMS(activity, iName);
                            }
                        } else {
                            Toast.makeText(activity, "Quantity must be greater than 0!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NumberFormatException e) {
                        // This is thrown when the String contains characters other than digits
                        Toast.makeText(activity, "Quantity must be a number!", Toast.LENGTH_SHORT).show();
                        quantityTextView.setText("0");
                        System.out.println("Invalid quantity");
                    }
                } else{
                    Toast.makeText(activity,"Name and Quantity required!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel new item when trash can icon button pressed and return to list
        View cancelButton = rootView.findViewById(R.id.delete_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.show_item_list);
            }
        });

        return rootView;
    }

    static boolean isBlank(String string) {
        return string != null && string.trim().length() != 0;
    }

    // Method that will send an SMS notification when inventory is low
    public void sendSMS(Activity activity, String itemName) {
        int permissionSms= ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        String username = ((MainActivity) getActivity()).getUsername();
        String userPhoneNumber = dbHandler.getUser(username).getPhoneNumber();

        // Only do so if phone number is not default 0 value and SMS permission is granted
        if(!userPhoneNumber.equals("0") && permissionSms == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userPhoneNumber, null, "Low inventory for: " + itemName, null, null);
            Toast.makeText(activity, "SMS SENT!", Toast.LENGTH_SHORT).show();
        }
    }
}