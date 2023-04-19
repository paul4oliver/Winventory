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
import java.util.Locale;

// Overview screen
public class DetailFragment extends Fragment {
    private Item mItem;
    private Long mId;
    private String mName;
    private String mQuantity;
    private int intQuantity;
    private String mDesc;
    public static final String ARG_ITEM_ID = "item_id";
    private InventoryDatabase dbHandler;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int itemId;

        // Get the item ID and count from the fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            itemId = args.getInt(ARG_ITEM_ID);

            // Get the selected item and info
            dbHandler = new InventoryDatabase(requireContext());
            mItem = dbHandler.getItem(itemId);
            mId = mItem.getId();
            mName = mItem.getName();
            mQuantity = mItem.getCount();
            intQuantity = Integer.parseInt(mQuantity);
            mDesc = mItem.getDescription();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        EditText quantityEditText = rootView.findViewById(R.id.item_count);
        EditText nameEditText = rootView.findViewById(R.id.item_name);
        EditText descriptionEditText = rootView.findViewById(R.id.item_description);

        // Make sure mItem is not null
        if (mItem != null) {

            //Set item name
            nameEditText.setText(mName);
            //Set item quantity
            quantityEditText.setText(mQuantity);
            //Set item description
            descriptionEditText.setText(mDesc);
        }

        // Decrease item quantity when decrease button pressed
        View decreaseButton = rootView.findViewById(R.id.decrease_button);
        decreaseButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                try {
                    if(intQuantity >= 1) {
                        intQuantity -= 1;
                        mItem.setCount(Integer.toString(intQuantity));
                        quantityEditText.setText(String.format(Locale.getDefault(), "%d", intQuantity));
                    } else {
                        Toast.makeText(activity, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (NumberFormatException e) {
                    // This is thrown when the String contains characters other than digits
                    System.out.println("Invalid count");
                }
            }
        });

        // Increase item quantity when increase button pressed
        View increaseButton = rootView.findViewById(R.id.increase_button);
        increaseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intQuantity += 1;
                mItem.setCount(Integer.toString(intQuantity));
                quantityEditText.setText(String.format(Locale.getDefault(), "%d", intQuantity));
            }
        });

        // Update item when check mark icon button pressed and return to list
        View updateButton = rootView.findViewById(R.id.update_item_button);
        updateButton.setOnClickListener(new View.OnClickListener() {

            Activity activity = getActivity();

            @Override
            public void onClick(View v) {
                String mName = nameEditText.getText().toString();
                String mQuantity = quantityEditText.getText().toString();
                String mDesc = descriptionEditText.getText().toString();

                // Check that name and quantity are not empty/blank
                if(isBlank(mName) && isBlank(mQuantity)){
                    // Check that quantity is greater than or equal to 0
                    try {
                        if (Integer.parseInt(mQuantity) >= 0) {

                            dbHandler.updateItem(mId, mName, mQuantity, mDesc);
                            Navigation.findNavController(v).navigate(R.id.show_item_list);

                            Activity activity = getActivity();

                            if(Integer.parseInt(mQuantity) == 0) {
                                sendSMS(activity, mName);
                            }
                        } else {
                            Toast.makeText(activity, "Quantity must be greater than 0!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NumberFormatException e) {
                        // This is thrown when the String contains characters other than digits
                        Toast.makeText(activity, "Quantity must be a number!", Toast.LENGTH_SHORT).show();
                        quantityEditText.setText("0");
                        intQuantity = 0;
                        mItem.setCount(Integer.toString(intQuantity));
                        System.out.println("Invalid quantity");
                    }
                } else{
                    Toast.makeText(activity,"Name and Quantity required!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Remove item when remove button pressed and return to List Fragment
        View removeButton = rootView.findViewById(R.id.remove_item);
        removeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dbHandler.deleteItem(mId);
                Navigation.findNavController(v).navigate(R.id.show_item_list);
            }
        });

        return rootView;
    }

    // Method to check if a string is null/empty
    static boolean isBlank(String string) {
        return string != null && string.trim().length() != 0;
    }

    // Method that will send an SMS notification when inventory is low
    public void sendSMS(Activity activity, String itemName) {
        int permissionSms= ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        String username = ((MainActivity) getActivity()).getUsername();
        String userPhoneNumber = dbHandler.getUser(username).getPhoneNumber();

        // Only do so if phone number is not default 0 value
        if(!userPhoneNumber.equals("0") && permissionSms == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userPhoneNumber, null, "Low inventory for: " + itemName, null, null);
            Toast.makeText(activity, "SMS SENT!", Toast.LENGTH_SHORT).show();
        }
    }
}