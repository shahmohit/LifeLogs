package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.VideoView;

/**
 * Created by mohit on 9/14/14.
 */
public class VideoDialogFragment extends DialogFragment {
    VideoView vView = null;
    EditText vTags;
    AlertDialog dialog;
    String mFileName;

    static VideoDialogFragment newInstance(String fPath) {
        VideoDialogFragment f = new VideoDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("fPath", fPath);
        f.setArguments(args);

        return f;
    }

    public interface VideoDialogListener {
        public void onVideoPositiveClick(DialogFragment dialog);

        public void onVideoNegativeClick(DialogFragment dialog);
    }

    VideoDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (VideoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileName = getArguments().getString("fPath");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.video_tags, null);
        v.setBackgroundResource(Color.TRANSPARENT);
        builder.setView(v);
        this.vView = (VideoView) v.findViewById(R.id.vsurface_view);
        this.vView.setVideoPath(mFileName);
        this.vView.start();
        vTags = (EditText) v.findViewById(R.id.video_tags);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onVideoPositiveClick(VideoDialogFragment.this);
                VideoDialogFragment.this.getDialog().dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onVideoNegativeClick(VideoDialogFragment.this);
                VideoDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    public String getFilePath() {
        return mFileName;
    }

    public String getTags() {
        String tags = vTags.getText().toString();
        if (tags == null) {
            tags = "";
        }
        return tags;
    }

}
