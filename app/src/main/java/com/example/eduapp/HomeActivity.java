package com.example.eduapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eduapp.adapters.BannerAdapter;
import com.example.eduapp.adapters.BatchAdapter;
import com.example.eduapp.model.Batch;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayout;
    private RecyclerView recyclerBatches;
    private BatchAdapter batchAdapter;
    private SearchView searchView;
    private BottomNavigationView bottomNav;

    private String currentCategory = "All";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerBatches = findViewById(R.id.recyclerBatches);
        searchView = findViewById(R.id.searchView);
        bottomNav = findViewById(R.id.bottomNav);

        // Setup banner
        List<Integer> banners = new ArrayList<>();
        banners.add(R.drawable.banner1);
        banners.add(R.drawable.banner2);
        BannerAdapter bannerAdapter = new BannerAdapter(banners);
        viewPagerBanner.setAdapter(bannerAdapter);
        // optional: auto-scroll using handler (omitted for brevity)

        // Setup categories tabs
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Offline"));
        tabLayout.addTab(tabLayout.newTab().setText("Mahapack"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentCategory = tab.getText().toString();
                batchAdapter.setFilters(currentQuery, currentCategory);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup batches list
        List<Batch> sample = createSampleBatches();
        batchAdapter = new BatchAdapter(sample, batch -> {
            // open detail or show toast
            // Intent intent = new Intent(MainActivity.this, BatchDetailActivity.class);
            // intent.putExtra("batch_title", batch.getTitle());
            // startActivity(intent);
        });

        recyclerBatches.setLayoutManager(new LinearLayoutManager(this));
        recyclerBatches.setAdapter(batchAdapter);

        // Setup search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                batchAdapter.setFilters(currentQuery, currentCategory);
                return true;
            }
            @Override public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                batchAdapter.setFilters(currentQuery, currentCategory);
                return true;
            }
        });

        // Bottom nav click
        bottomNav.setOnItemSelectedListener(item -> {
            handleBottomNav(item);
            return true;
        });

    }

    private void handleBottomNav(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_study) {
            // already on study/home
        } else if (itemId == R.id.nav_batches) {        // maybe scroll to top
            recyclerBatches.smoothScrollToPosition(0);
        } else if (itemId == R.id.nav_test) {
            // startActivity(new Intent(this, TestActivity.class));
        } else if (itemId == R.id.nav_store) {
            // startActivity(new Intent(this, StoreActivity.class));
        } else if (itemId == R.id.nav_library) {
            // startActivity(new Intent(this, LibraryActivity.class));
        }
    }


    private List<Batch> createSampleBatches() {
        List<Batch> list = new ArrayList<>();
        list.add(new Batch("Bank Mahapack", R.drawable.batch1, "Mahapack", true));
        list.add(new Batch("IBPS RRB PO Batch", R.drawable.batch2, "Offline", false));
        list.add(new Batch("General Banking Course", R.drawable.batch3, "All", false));
        list.add(new Batch("Advanced Mahapack", R.drawable.batch4, "Mahapack", true));
        // add more
        return list;
    }
}
