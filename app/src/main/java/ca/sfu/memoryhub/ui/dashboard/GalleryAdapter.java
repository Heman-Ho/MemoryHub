package ca.sfu.memoryhub.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import ca.sfu.memoryhub.MatchGame;
import ca.sfu.memoryhub.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final List<String> imageUrls;
    private final List<String> descriptions;

    public GalleryAdapter(Context context, List<String> imageUrls, List<String> descriptions) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each gallery item
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Use Glide to load the image into the ImageView
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog mDialog;
                mDialog = new Dialog(context);
                ImageView fullscreenImageView;
                TextView fullscreenTextView;
                int widthOfScreen, heightOfScreen;

                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                widthOfScreen = displayMetrics.widthPixels;
                heightOfScreen = displayMetrics.heightPixels;

                int widthOfDialog = (int) (0.85 * widthOfScreen);
                int heightOfDialog = (int)(0.85 * heightOfScreen);
                float textSize = widthOfScreen * 0.02f;

                mDialog.setContentView(R.layout.fullscreen_image);
                Objects.requireNonNull(mDialog.getWindow())
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                fullscreenImageView = mDialog.findViewById(R.id.fullscreenImageView);
                fullscreenTextView = mDialog.findViewById(R.id.fullscreenTextView);

                // Set up parameters for pop up based on screen dimensions
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = widthOfDialog; // 0.8 * widthOfScreen
                layoutParams.height = heightOfDialog; // 0.8 * widthOfScreen
                mDialog.getWindow().setAttributes(layoutParams);

                // Set up parameters for ImageView and TextView
                fullscreenImageView.getLayoutParams().width = widthOfDialog;
                fullscreenImageView.getLayoutParams().height = (int)(heightOfDialog * 0.8);
                fullscreenTextView.getLayoutParams().width = widthOfDialog;
                fullscreenTextView.getLayoutParams().height = (int)(heightOfDialog * 0.2);
                fullscreenTextView.setTextSize(textSize);
                fullscreenTextView.setText(descriptions.get(position));
                Glide.with(context).load(imageUrls.get(position)).into(fullscreenImageView); // ImageView of the card
                // Display the current card's image
                mDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size(); // Return the number of items in the list
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Reference to the ImageView in the gallery_item layout
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}


