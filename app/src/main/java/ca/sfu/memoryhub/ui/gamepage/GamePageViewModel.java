package ca.sfu.memoryhub.ui.gamepage;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GamePageViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public GamePageViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to MemoryHub!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
