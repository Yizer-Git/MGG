package com.just.cn.mgg.ui.profile;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.just.cn.mgg.R;
import com.just.cn.mgg.utils.SPUtils;
import com.just.cn.mgg.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 个人资料编辑页
 */
public class PersonalInfoActivity extends AppCompatActivity {

    private ShapeableImageView ivAvatar;
    private TextInputEditText etNickname;
    private MaterialButton btnSelectBirthday;
    private RadioButton rbGenderMale;
    private RadioButton rbGenderFemale;
    private RadioButton rbGenderUnknown;

    private String avatarUri;
    private String birthday;

    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        initToolbar();
        initLaunchers();
        initViews();
        loadUserInfo();
    }

    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initLaunchers() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        avatarUri = uri.toString();
                        ivAvatar.setImageURI(uri);
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        Uri uri = saveBitmap(bitmap);
                        if (uri != null) {
                            avatarUri = uri.toString();
                            ivAvatar.setImageBitmap(bitmap);
                        } else {
                            ToastUtils.show(this, getString(R.string.server_error));
                        }
                    }
                }
        );
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.ivAvatar);
        etNickname = findViewById(R.id.etNickname);
        btnSelectBirthday = findViewById(R.id.btnSelectBirthday);
        MaterialButton btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        rbGenderMale = findViewById(R.id.rbGenderMale);
        rbGenderFemale = findViewById(R.id.rbGenderFemale);
        rbGenderUnknown = findViewById(R.id.rbGenderUnknown);

        btnChangeAvatar.setOnClickListener(v -> showAvatarOptions());
        ivAvatar.setOnClickListener(v -> showAvatarOptions());
        btnSelectBirthday.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        avatarUri = SPUtils.getUserAvatar(this);
        if (TextUtils.isEmpty(avatarUri)) {
            avatarUri = SPUtils.getString(this, com.just.cn.mgg.utils.Constants.KEY_AVATAR);
        }
        if (!TextUtils.isEmpty(avatarUri)) {
            ivAvatar.setImageURI(Uri.parse(avatarUri));
        }

        String nickname = SPUtils.getUsername(this);
        if (!TextUtils.isEmpty(nickname)) {
            etNickname.setText(nickname);
            etNickname.setSelection(nickname.length());
        }

        int gender = SPUtils.getUserGender(this);
        if (gender == 1) {
            rbGenderMale.setChecked(true);
        } else if (gender == 2) {
            rbGenderFemale.setChecked(true);
        } else {
            rbGenderUnknown.setChecked(true);
        }

        birthday = SPUtils.getUserBirthday(this);
        updateBirthdayButton();
    }

    private void showAvatarOptions() {
        String[] options = new String[]{
                getString(R.string.personal_info_avatar_option_gallery),
                getString(R.string.personal_info_avatar_option_camera),
                getString(R.string.personal_info_avatar_option_cancel)
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.personal_info_avatar)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        galleryLauncher.launch("image/*");
                    } else if (which == 1) {
                        cameraLauncher.launch(null);
                    } else {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        if (!TextUtils.isEmpty(birthday)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                calendar.setTime(sdf.parse(birthday));
            } catch (ParseException ignored) {
            }
        }
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    birthday = sdf.format(cal.getTime());
                    updateBirthdayButton();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void updateBirthdayButton() {
        if (TextUtils.isEmpty(birthday)) {
            btnSelectBirthday.setText(R.string.personal_info_select_birthday);
        } else {
            btnSelectBirthday.setText(birthday);
        }
    }

    private void saveProfile() {
        String nickname = etNickname.getText() != null ? etNickname.getText().toString().trim() : "";
        if (nickname.isEmpty()) {
            etNickname.setError(getString(R.string.personal_info_nickname_hint));
            return;
        }

        int gender = 0;
        if (rbGenderMale.isChecked()) {
            gender = 1;
        } else if (rbGenderFemale.isChecked()) {
            gender = 2;
        }

        SPUtils.saveUsername(this, nickname);
        SPUtils.saveUserGender(this, gender);
        if (!TextUtils.isEmpty(birthday)) {
            SPUtils.saveUserBirthday(this, birthday);
        }
        if (!TextUtils.isEmpty(avatarUri)) {
            SPUtils.saveUserAvatar(this, avatarUri);
        }

        ToastUtils.show(this, getString(R.string.personal_info_save_success));
        setResult(RESULT_OK);
        finish();
    }

    @Nullable
    private Uri saveBitmap(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        File outputDir = new File(getFilesDir(), "avatar");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            return null;
        }
        File file = new File(outputDir, "avatar_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return Uri.fromFile(file);
        } catch (IOException e) {
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
