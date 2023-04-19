package org.snhu.inventoryapp;

import android.app.Activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ListFragment extends Fragment {

    private ActivityResultLauncher<String[]> activityResultLauncher;
    private InventoryDatabase dbHandler;
    private ArrayList<Item> itemModalArrayList;
    private Boolean isFirstSMS;


    public ListFragment() {
        // Required  public constructor
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Log.e("activityResultLauncher", ""+result.toString());
                Boolean areAllGranted = true;
                for(Boolean b : result.values()) {
                    areAllGranted = areAllGranted && b;
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemModalArrayList = new ArrayList<>();
        dbHandler = new InventoryDatabase(requireContext());

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        itemModalArrayList = dbHandler.readItems();

        // Checking if permission is granted
        String[] appPerms;
        appPerms = new String[]{Manifest.permission.SEND_SMS};

        requireActivity().addMenuProvider(new MenuProvider() {

            // Show SMS notification icon
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Activity activity = getActivity();
                int permissionSms = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
                if (permissionSms == PackageManager.PERMISSION_GRANTED) {
                    menuInflater.inflate(R.menu.menu_top, menu);
                    // Send SMS if first enabled or user logs in.
                    isFirstSMS = ((MainActivity) getActivity()).getFirstSMS();
                    if(isFirstSMS){
                        sendSMS(activity);
                        ((MainActivity) getActivity()).setFirstSMS(false);
                    }
                }
                else {
                    menuInflater.inflate(R.menu.menu_top_off, menu);
                }
            }

            // Prompt a user for permission to communicate with the text messaging app and display information based on permission
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                Activity activity = getActivity();
                activityResultLauncher.launch(appPerms);

                int permissionSms = ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);

                if (permissionSms == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "SMS Notifications enabled already", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Enable SMS permission to receive low inventory notifications", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        } , getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // Click listener for the RecyclerView
        View.OnClickListener onClickListener = itemView -> {

            // Create fragment arguments containing the selected item ID
            int selectedItemId = Math.toIntExact((Long) itemView.getTag());
            Bundle args = new Bundle();
            args.putInt(DetailFragment.ARG_ITEM_ID, selectedItemId);

            // Replace list with details
            Navigation.findNavController(itemView).navigate(R.id.show_item_detail, args);
        };

        // Go to add item fragment if FAB is clicked
        View addItem = rootView.findViewById(R.id.new_button);
        addItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.show_add_item);
            }
        });

        // Send items to RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.item_list);
        recyclerView.setAdapter(new ItemAdapter(itemModalArrayList, onClickListener));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

     return rootView;
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        
        private final View.OnClickListener mOnClickListener;
        private final ArrayList<Item> itemModalArrayList;

        public ItemAdapter(ArrayList<Item> itemModalArrayList, View.OnClickListener onClickListener) {
            this.itemModalArrayList = itemModalArrayList;
            mOnClickListener = onClickListener;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            Item item = itemModalArrayList.get(position);
            holder.bind(item);
            holder.itemView.setTag(item.getId());
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return itemModalArrayList.size();
        }

    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView mNameTextView;
        private final TextView mCountTextView;
        private final TextView mIdTextView;


        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_item, parent, false));
            mIdTextView = itemView.findViewById(R.id.item_id);
            mNameTextView = itemView.findViewById(R.id.item_name);
            mCountTextView = itemView.findViewById(R.id.item_count);

        }

        public void bind(Item item) {
            mIdTextView.setText(String.valueOf(item.getId()));
            mNameTextView.setText(item.getName());
            mCountTextView.setText(item.getCount());
        }
    }

    // Method that will send an SMS notification when inventory is low
    public void sendSMS(Activity activity) {
        String sMessage = "Winventory Alert!\nLow inventory for:\n";
        int permissionSms= ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        String username = ((MainActivity) getActivity()).getUsername();
        String userPhoneNumber = dbHandler.getUser(username).getPhoneNumber();

        for(int i = 0; i < itemModalArrayList.size(); i++){
            if(Integer.parseInt(itemModalArrayList.get(i).getCount()) == 0){
                sMessage = sMessage + itemModalArrayList.get(i).getName() + "\n";
            }
        }

        // Only do so if phone number is not default 0 value and SMS permission is granted
        if(!userPhoneNumber.equals("0") && permissionSms == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(userPhoneNumber, null, sMessage, null, null);
            Toast.makeText(activity, "SMS SENT!", Toast.LENGTH_SHORT).show();
        }
    }
}





