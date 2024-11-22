package ca.sfu.memoryhub.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import ca.sfu.memoryhub.R;

public class DashboardFragment extends Fragment {

    private StorageReference storageReference;
    private Uri imageUri;
    private MaterialButton selectImageButton;
    private MaterialButton uploadButton;

    // Register activity result launcher to handle image selection
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData(); // Set the imageUri when an image is picked
                    Toast.makeText(getContext(), "Image selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // This is where the fragment view is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false); // Inflate your fragment layout
    }

    // This is where you initialize the views and set up listeners, once the view is created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext());
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize buttons
        selectImageButton = view.findViewById(R.id.selectImageButton);
        uploadButton = view.findViewById(R.id.uploadButton);

        // Handle image selection button click
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent); // Launch the image picker
        });

        // Handle upload button click
        uploadButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImage(); // Upload the selected image
            } else {
                Toast.makeText(getContext(), "Please select an image first!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to upload the image to Firebase Storage
    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference for the file in Firebase storage, under the user's UID
        StorageReference ref = storageReference.child("images/" + userId + "/" + UUID.randomUUID().toString());

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}



