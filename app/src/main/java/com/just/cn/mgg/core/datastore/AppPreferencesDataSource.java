package com.just.cn.mgg.core.datastore;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Lightweight SharedPreferences-backed data source replacing the coroutine-based DataStore version.
 */
public final class AppPreferencesDataSource {

    private static final String STORE_NAME = "user_preferences";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PUSH_NOTIFICATION = "push_notification";
    private static final String KEY_ACTIVITY_PROMOTION = "activity_promotion";
    private static final String KEY_LOCATION_SERVICE = "location_service";

    private final SharedPreferences preferences;

    private final MutableLiveData<String> authTokenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> userIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> nicknameLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pushNotificationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> activityPromotionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> locationServiceLiveData = new MutableLiveData<>();

    private AppPreferencesDataSource(SharedPreferences preferences) {
        this.preferences = preferences;
        authTokenLiveData.setValue(preferences.getString(KEY_AUTH_TOKEN, ""));
        userIdLiveData.setValue(preferences.getInt(KEY_USER_ID, 0));
        nicknameLiveData.setValue(preferences.getString(KEY_NICKNAME, ""));
        pushNotificationLiveData.setValue(preferences.getBoolean(KEY_PUSH_NOTIFICATION, true));
        activityPromotionLiveData.setValue(preferences.getBoolean(KEY_ACTIVITY_PROMOTION, true));
        locationServiceLiveData.setValue(preferences.getBoolean(KEY_LOCATION_SERVICE, false));
    }

    public static AppPreferencesDataSource create(Context context) {
        Context appContext = context.getApplicationContext();
        SharedPreferences preferences = appContext.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
        return new AppPreferencesDataSource(preferences);
    }

    public LiveData<String> getAuthTokenFlow() {
        return authTokenLiveData;
    }

    public LiveData<Integer> getUserIdFlow() {
        return userIdLiveData;
    }

    public LiveData<String> getNicknameFlow() {
        return nicknameLiveData;
    }

    public LiveData<Boolean> getPushNotificationEnabled() {
        return pushNotificationLiveData;
    }

    public LiveData<Boolean> getActivityPromotionEnabled() {
        return activityPromotionLiveData;
    }

    public LiveData<Boolean> getLocationServiceEnabled() {
        return locationServiceLiveData;
    }

    public void setAuthToken(String token) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
        authTokenLiveData.postValue(token);
    }

    public void setUserId(int id) {
        preferences.edit().putInt(KEY_USER_ID, id).apply();
        userIdLiveData.postValue(id);
    }

    public void setNickname(String nickname) {
        preferences.edit().putString(KEY_NICKNAME, nickname).apply();
        nicknameLiveData.postValue(nickname);
    }

    public void setPushNotificationEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_PUSH_NOTIFICATION, enabled).apply();
        pushNotificationLiveData.postValue(enabled);
    }

    public void setActivityPromotionEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_ACTIVITY_PROMOTION, enabled).apply();
        activityPromotionLiveData.postValue(enabled);
    }

    public void setLocationServiceEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_LOCATION_SERVICE, enabled).apply();
        locationServiceLiveData.postValue(enabled);
    }

    public void clear() {
        preferences.edit().clear().apply();
        authTokenLiveData.postValue("");
        userIdLiveData.postValue(0);
        nicknameLiveData.postValue("");
        pushNotificationLiveData.postValue(true);
        activityPromotionLiveData.postValue(true);
        locationServiceLiveData.postValue(false);
    }
}
