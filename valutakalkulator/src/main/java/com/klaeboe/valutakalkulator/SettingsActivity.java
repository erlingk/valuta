package com.klaeboe.valutakalkulator;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class SettingsActivity extends ActionBarActivity {
    public static final String KEY_ALL_CURRENCIES = "pref_allCurrencies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.pref_settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

        }
    }
}
