package ca.sfu.memoryhub.ui.Gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to the Gallery!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}