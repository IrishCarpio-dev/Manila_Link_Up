package com.manilalinkup.app.utilities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.DistrictAdapter;
import com.manilalinkup.app.adapters.LocationAdapter;
import com.manilalinkup.app.models.PhotonResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAutocompleteHelper {
    public static void attachDistrictAutocomplete(EditText editText) {
        Context context = editText.getContext();

        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_search_popup, null);
        RecyclerView rv = popupView.findViewById(R.id.popup_recycler);

        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        DistrictAdapter adapter = new DistrictAdapter(address -> {
            editText.setText(address);
            editText.clearFocus();
            popupWindow.dismiss();
        });
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> results = DistrictAutocompleteManager.getInstance().getFilteredResults(s.toString());
                if (!results.isEmpty() && s.length() > 0) {
                    adapter.updateList(results);
                    if (!popupWindow.isShowing()) popupWindow.showAsDropDown(editText);
                } else {
                    popupWindow.dismiss();
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }


    public static void attachAddressAutocomplete(EditText editText) {
        Context context = editText.getContext();
        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable[] runnable = {null};

        View popupView = LayoutInflater.from(context).inflate(R.layout.layout_search_popup, null);
        RecyclerView rv = popupView.findViewById(R.id.popup_recycler);

        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        LocationAdapter adapter = new LocationAdapter(address -> {
            editText.setText(address);
            editText.clearFocus();
            popupWindow.dismiss();
        });
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (runnable[0] != null) handler.removeCallbacks(runnable[0]);

                runnable[0] = () -> {
                    if (s.length() >= 2) {
                        PhotonClient.getInstance().search(s.toString(), new Callback<PhotonResponseModel>() {
                            @Override
                            public void onResponse(Call<PhotonResponseModel> call, Response<PhotonResponseModel> response) {
                                if (response.isSuccessful() && response.body() != null && !response.body().features.isEmpty()) {
                                    adapter.setList(response.body().features);
                                    popupWindow.setWidth(editText.getWidth());
                                    if (!popupWindow.isShowing()) popupWindow.showAsDropDown(editText);
                                } else {
                                    popupWindow.dismiss();
                                }
                            }
                            @Override public void onFailure(Call<PhotonResponseModel> call, Throwable t) { popupWindow.dismiss(); }
                        });
                    } else {
                        popupWindow.dismiss();
                    }
                };
                handler.postDelayed(runnable[0], 250);
            }
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
