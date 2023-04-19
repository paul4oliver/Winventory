package org.snhu.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private String mUsername;
    private Boolean firstSMS = true;

    public String getUsername(){
        return mUsername;
    }

    public void setString(String string){
        this.mUsername = string;
    }

    public Boolean getFirstSMS(){
        return firstSMS;
    }

    public void setFirstSMS(Boolean setValue){
        this.firstSMS = setValue;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfig = new AppBarConfiguration.
                    Builder(R.id.login_fragment,        // Hide up button from these fragments
                    R.id.register_fragment,
                    R.id.list_fragment).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}