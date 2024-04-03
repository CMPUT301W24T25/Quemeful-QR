package com.android.quemeful_qr;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Calendar;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();

    public void selectDate(Calendar date) {
        selectedDate.setValue(date);
    }

    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }
}

