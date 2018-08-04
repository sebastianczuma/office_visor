package com.example.sebastianczuma.officevisor.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.sebastianczuma.officevisor.Activities.AddBuilding;
import com.example.sebastianczuma.officevisor.R;

/**
 * Created by sebastianczuma on 08.12.2016.
 */

public class FragmentThird extends Fragment {

    public FragmentThird() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_third, container, false);

        ImageButton addNewBuilding = (ImageButton) view.findViewById(R.id.imageButton);
        addNewBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddBuilding.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
