package com.android.lifelogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by mohit on 9/14/14.
 */

public class ImageDialogFragment extends DialogFragment {
    ImageView iView = null;
    EditText iTags;
    AlertDialog dialog;
    String mFileName;

    static ImageDialogFragment newInstance(String fPath) {
        ImageDialogFragment f = new ImageDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("fPath", fPath);
        f.setArguments(args);

        return f;
    }

    public interface ImageDialogListener {
        public void onImagePositiveClick(DialogFragment dialog);

        public void onImageNegativeClick(DialogFragment dialog);
    }

    ImageDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ImageDialogListener) activity;
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
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.image_tags, null);
        v.setBackgroundResource(Color.TRANSPARENT);
        builder.setView(v);
        this.iView = (ImageView) v.findViewById(R.id.isurface_view);
        this.iView.setImageURI(Uri.parse("file://" + mFileName));
        this.iView.setScaleType(ImageView.ScaleType.FIT_XY);
        iTags = (EditText) v.findViewById(R.id.image_tags);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onImagePositiveClick(ImageDialogFragment.this);
                ImageDialogFragment.this.getDialog().dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onImageNegativeClick(ImageDialogFragment.this);
                ImageDialogFragment.this.getDialog().cancel();
            }
        });
        // Create the AlertDialog object and return it
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
        String tags = iTags.getText().toString();
        if (tags == null) {
            tags = "";
        }
        return tags;
    }
}