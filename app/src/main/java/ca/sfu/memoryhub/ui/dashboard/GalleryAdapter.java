package ca.sfu.memoryhub.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import ca.sfu.memoryhub.MatchGame;
import ca.sfu.memoryhub.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private List<String> imageUrls;
    private List<String> imageTitles;
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
//THEIR VERSION
//    public void searchDataList(ArrayList<DataClass> searchList){
//        datalist = searchList;
//        notifyDataSetChanged();
//    }
//    MY VERSION
//    public void searchDataList(ArrayList<String> searchList){
//        imageUrls = searchList;
//        notifyDataSetChanged();
//    }

    public void searchDataList(ArrayList<String> searchList){//searchList will have a bunch of titles
        List<String> urlsToSearch = new ArrayList<>();
        StorageReference  storageReference = FirebaseStorage.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userImagesRef = storageReference.child("images/" + userId);

        userImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {

                        // Get downloadUrl and image's metadata.
                        Task<Uri> downloadUrlTask = item.getDownloadUrl();
                        Task<StorageMetadata> metadataTask = item.getMetadata();

                        // Wait until both tasks are completed
                        Tasks.whenAllSuccess(downloadUrlTask, metadataTask).addOnSuccessListener(tasks -> {
                            Uri uri = (Uri) tasks.get(0);  // Download URL is the first task
                            StorageMetadata metadata = (StorageMetadata) tasks.get(1);  // Metadata is the second task

                            // Retrieve title and description from metadata
                            String title = metadata.getCustomMetadata("title");


                            if(searchList.contains(title.toLowerCase())){
                                urlsToSearch.add(uri.toString());
                                imageUrls = urlsToSearch;
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
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


