package com.example.galleryios18.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galleryios18.R;
import com.example.galleryios18.utils.rateapp.RotationRatingBar;

public class RateDialog extends Dialog {

    private final Context context;

    private RotationRatingBar rateBar;

    private boolean isRateAppTemp = false;
    private ListenerRate listenerRate;

    public void setListenerRate(ListenerRate listenerRate) {
        this.listenerRate = listenerRate;
    }

    public RateDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_app);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        this.context = context;
        initDialog();
        setOnDismissListener(dialog -> rateBar.setRating(0));
    }

    private void initDialog() {
        FrameLayout layoutParent = findViewById(R.id.layout_parent);
        TextView tvRate = findViewById(R.id.tv_submit);
        TextView tvLater = findViewById(R.id.tv_not_now);
        rateBar = findViewById(R.id.simpleRatingBar);

        layoutParent.setOnClickListener(v -> dismiss());

        tvRate.setOnClickListener(v -> {
            if (isRateAppTemp && rateBar.getRating() > 0) {
                if ((int) (rateBar.getRating()) >= 2) {
                    openMarket();
                }
                listenerRate.rateFiveStar();
                notShowDialogWhenUserRateHigh();
            } else {
                Toast.makeText(context, context.getText(R.string.please_rate_5_star), Toast.LENGTH_SHORT).show();
            }
        });

        tvLater.setOnClickListener(v -> {
            dismiss();
            listenerRate.close();
        });

        rateBar.setOnRatingChangeListener((ratingBar, rating) -> isRateAppTemp = true);
    }

    private void openMarket() {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getResources().getString(R.string.link_share_market) + context.getPackageName())));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void notShowDialogWhenUserRateHigh() {
        dismiss();
    }

    public interface ListenerRate {
        void rateFiveStar();

        void close();
    }
}
