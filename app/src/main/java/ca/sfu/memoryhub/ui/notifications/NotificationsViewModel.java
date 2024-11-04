package ca.sfu.memoryhub.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to the Games section! More Games will be added soon");
    }

    public LiveData<String> getText() {
        return mText;
    }
}