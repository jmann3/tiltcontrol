package com.mikedg.android.tiltcontrolcontroller;



import android.os.Bundle;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikedg.android.tiltcontrolcontroller.R;


public class TutorialScreenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_Image = "param1";
    private static final String ARG_Title = "param2";
    private static final String ARG_Description = "param3";

    // TODO: Rename and change types of parameters
    private int mImage;
    private String mTitle;
    private String mDescription;


    public static TutorialScreenFragment newInstance(int image, String title, String description) {
        TutorialScreenFragment fragment = new TutorialScreenFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_Image, image);
        args.putString(ARG_Title, title);
        args.putString(ARG_Description, description);
        fragment.setArguments(args);
        return fragment;
    }
    public TutorialScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImage = getArguments().getInt(ARG_Image);
            mTitle = getArguments().getString(ARG_Title);
            mDescription = getArguments().getString(ARG_Description);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_tutorial_screen, container, false);

        // set up Views
        ImageView imageView = (ImageView)rootView.findViewById(R.id.phone_image);
        TextView titleText = (TextView)rootView.findViewById(R.id.section_title);
        TextView descriptionText = (TextView)rootView.findViewById(R.id.section_description);

        // populate Views
        imageView.setImageDrawable(getResources().getDrawable(mImage));

        SpannableString content = new SpannableString(mTitle);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        titleText.setText(content);

        descriptionText.setText(mDescription);

        return rootView;
    }


}
