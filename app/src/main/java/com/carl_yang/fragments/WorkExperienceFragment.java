package com.carl_yang.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.carl_yang.adapter.WorkExpAdapter;
import com.carl_yang.resume.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkExperienceFragment extends Fragment {

    private RecyclerView recyclerView;
    private WorkExpAdapter madapter;
    XRefreshView xRefreshView;

    LinearLayoutManager layoutManager;
    private Context context;

    public WorkExperienceFragment(){

    }

    public WorkExperienceFragment(Context context) {
        // Required empty public constructor
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_work_experience, container, false);
        xRefreshView = (XRefreshView) view.findViewById(R.id.xrefreshview);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_test_rv);
        recyclerView.setHasFixedSize(true);

        madapter=new WorkExpAdapter(view.getContext());
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(madapter);
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xRefreshView.stopRefresh();
                    }
                }, 500);
            }

            public void onLoadMore(boolean isSilence) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                    }
                }, 1000);
            }
        });
        return view;
    }

    public static WorkExperienceFragment newInstance(Activity ac) {
        return new WorkExperienceFragment(ac);
    }
}
