package com.app.chendurfincorp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.chendurfincorp.R;
import com.app.chendurfincorp.adapter.HomeAdapter;
import com.app.chendurfincorp.data.HomeMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView rvHome;
    HomeAdapter homeAdapter;
    List<HomeMenu> homeList;
    RecyclerView.LayoutManager mLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeList = new ArrayList<>();
        homeAdapter = new HomeAdapter(getActivity(), homeList);
        rvHome = view.findViewById(R.id.rv_home);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvHome.setLayoutManager(mLayoutManager);
        rvHome.setAdapter(homeAdapter);
        fetchMenu();
        return  view;
    }

    private void fetchMenu() {

        int [] icons = new int[]{

                R.drawable.ic_attendence_home,
                R.drawable.ic_leave_home,
                R.drawable.ic_slip_home,
                R.drawable.ic_query_home

        };
        HomeMenu menu = new HomeMenu("Attendence Report", icons[0]);
        homeList.add(menu);
            menu = new HomeMenu("Leave Request", icons[1]);
            homeList.add(menu);
            menu = new HomeMenu("Pay Slip", icons[2]);
            homeList.add(menu);
            menu = new HomeMenu("Submit Queries", icons[3]);
            homeList.add(menu);

        homeAdapter.notifyDataSetChanged();
    }

}
