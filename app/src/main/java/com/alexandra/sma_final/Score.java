package com.alexandra.sma_final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Score extends Fragment {

    private TextView mKarma;
    private ImageButton mUpButton;
    private ImageButton mDownButton;
    private TextView mVote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.score_tab, container, false);

        // set karma
        mKarma = (TextView) v.findViewById(R.id.karma);
        String karma = "Karma: ";
        karma = karma + ((UserActivity)getActivity()).getUser().getKarma();
        karma = karma + " karma";
        mKarma.setText(karma);

        mUpButton = (ImageButton) v.findViewById(R.id.upButton);
        mDownButton = (ImageButton) v.findViewById(R.id.downButton);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            private int clicked = 0;

            @Override
            public void onClick(View view) {
                // set rating + 1
                if(clicked == 0){
                    mVote.setText("+1");
                    clicked = 1;
                }else{
                    mVote.setText("0");
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
                    mVote.setText("-1");
                    clicked = 1;
                }else{
                    mVote.setText("0");
                    clicked = 0;
                }

                mUpButton.setPressed(false);
                mDownButton.setPressed(true);
            }
        });
        mVote = (TextView) v.findViewById(R.id.vote);
        mVote.setText("0");

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
