package com.alexandra.sma_final.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.alexandra.sma_final.MyApplication;
import com.alexandra.sma_final.R;
import com.alexandra.sma_final.activities.UserActivity;
import com.alexandra.sma_final.customviews.MontserratTextView;

import androidx.fragment.app.Fragment;
import realm.Rating;

public class Score extends Fragment {

    private MontserratTextView mKarma;
    private ImageButton mUpButton;
    private ImageButton mDownButton;
    private MontserratTextView mVote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.score_tab, container, false);

        // set karma
        mKarma = (MontserratTextView) v.findViewById(R.id.karma);

        mVote = (MontserratTextView) v.findViewById(R.id.vote);
        if(((UserActivity)getActivity()).getUserProfileOwner() != null){
            Integer score = ((UserActivity)getActivity()).getUserProfileOwner().getKarma();
            mVote.setText(score.toString());
        }

        mUpButton = (ImageButton) v.findViewById(R.id.upButton);
        mDownButton = (ImageButton) v.findViewById(R.id.downButton);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            private int clicked = 0;

            @Override
            public void onClick(View view) {
                // set rating + 1
                if(clicked == 0){
                    Rating newRating = new Rating();
                    newRating.setScore(Integer.valueOf(((UserActivity)getActivity()).getUserProfileOwner().getKarma()+1));
                    newRating.setUserId(((UserActivity)getActivity()).getUserProfileOwner().getId());
                    ((MyApplication)getActivity().getApplication()).requestGateway.putRating(newRating);
                    mVote.setText(((UserActivity)getActivity()).getUserProfileOwner().getKarma()+1);
                    clicked = 1;
                }else{
                    clicked = 0;
                }

                mUpButton.setPressed(true);
                mDownButton.setPressed(false);
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            private int clicked = 0;

            @Override
            public void onClick(View view) {
                // set rating - 1
                if(clicked == 0){
                    Rating newRating = new Rating();
                    newRating.setScore(Integer.valueOf(((UserActivity)getActivity()).getUserProfileOwner().getKarma()-1));
                    newRating.setUserId(((UserActivity)getActivity()).getUserProfileOwner().getId());
                    ((MyApplication)getActivity().getApplication()).requestGateway.putRating(newRating);
                    mVote.setText(((UserActivity)getActivity()).getUserProfileOwner().getKarma()+1);
                    clicked = 1;
                }else{
                    clicked = 0;
                }

                mUpButton.setPressed(false);
                mDownButton.setPressed(true);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
