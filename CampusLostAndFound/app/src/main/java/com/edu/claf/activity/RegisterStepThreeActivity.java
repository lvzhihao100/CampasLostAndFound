package com.edu.claf.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.Utils.RegexUtils;
import com.edu.claf.bean.User;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 用户注册界面三
 */
public class RegisterStepThreeActivity extends AppCompatActivity {

    //拍照获取图片
    private static final int REQUESTCODE_TAKE = 1;
    private static final int REQUESTCODE_PICK = 2;
    private static final int REQUESTCODE_CUTTING = 3;
    private static String ICON_FILE_LOCATION = null;
    private String fileName;

    SelectPicPopupWindow menuWindow;

    private Uri photoUri;
    private File photoPath;
    private FileOutputStream out;

    @InjectView(R.id.btn_register_ind_page_three_finish)
    Button btnRegisterFinish;

    @InjectView(R.id.et_register_nickname)
    EditText etNickName;
    String mNickName;

    @InjectView(R.id.et_register_password)
    EditText etPassword;
    String mPassword;

    @InjectView(R.id.et_register_confirm_password)
    EditText etConfirmPassword;
    String mConfirmPassword;

    @InjectView(R.id.iv_password_confirm)
    ImageView ivPasswordConfirm;

    @InjectView(R.id.iv_register_head_portrait)
    ImageView ivRegisterHeadPortrait;

    private String mChooseSchool;
    private String mChooseProvince;
    private String mPhoneNumber;
    private User mUser;
    private Bitmap bitmap;
    private boolean mChoosePhoto = false;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_step_three);
        BaseApplication.getApplication().addActivity(this);
        ButterKnife.inject(this);
        initRegisterData();
        etConfirmPassword.addTextChangedListener(mTextWatcher);
    }

    /**
     * 文本监听器，用于监听第二次密码是否和第一次密码一致
     */
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mPassword = etPassword.getText().toString();
            String rePass = charSequence.toString();
            if (rePass.equals(mPassword)) {
                ivPasswordConfirm.setImageResource(R.drawable.forget_password_true);
            } else {
                ivPasswordConfirm.setImageResource(R.drawable.forget_password_confirm);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    /**
     * 验证注册数据
     * @return
     */
    private boolean vertifyRegisterData() {
        mNickName = etNickName.getText().toString().trim();
        if (TextUtils.isEmpty(mNickName)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return false;
        }
        mPassword = etPassword.getText().toString().trim();
        if (!RegexUtils.passwordLength(mPassword)) {
            Toast.makeText(this, "请输入符合要求的密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mPassword)) {
            Toast.makeText(RegisterStepThreeActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }
        mConfirmPassword = etConfirmPassword.getText().toString();
        if (!mConfirmPassword.equals(mPassword)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 获取注册界面一和注册界面二传递来的数据
     */
    private void initRegisterData() {
        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra("register_phone_number");
        mChooseProvince = intent.getStringExtra("register_province");
        mChooseSchool = intent.getStringExtra("register_school");
        ICON_FILE_LOCATION = FileUtils.getIconDir(mPhoneNumber).getAbsolutePath();
        photoUri = Uri.parse(ICON_FILE_LOCATION);
        photoPath = FileUtils.getIconDir(mPhoneNumber);
        fileName = mPhoneNumber;
        getDefaultHeadPortrait();
    }


    /**用户注册功能*/
    @OnClick(R.id.btn_register_ind_page_three_finish)
    public void RegisterFinish(View view) {
        //验证用户注册信息
        if (vertifyRegisterData()) { //用户注册信息验证成功
            //对注册用户的信息进行封装
            mUser = new User();
            mUser.setNickName(mNickName);
            mUser.setSchoolCity(mChooseProvince);
            mUser.setSchoolName(mChooseSchool);
            mUser.setMobilePhoneNumber(mPhoneNumber);
            mUser.setUsername(mPhoneNumber);
            mUser.setPassword(mPassword);
            mUser.setPhotoUrl(url);
            //使用Bmob的后端云丢用户进行注册
            mUser.signUp(this, new SaveListener() {
                @Override  //用户注册成功
                public void onSuccess() {
                    Toast.makeText(RegisterStepThreeActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    User user = BmobUser.getCurrentUser(RegisterStepThreeActivity.this, User.class);
                    //包存用户信息到本地
                    saveUserToJson(user);
                    //注册成功的同时使用注册的帐号和密码对用户进行登录操作
                    userLogin(mPhoneNumber, mPassword);
                }
                @Override //注册失败，返回失败信息
                public void onFailure(int i, String s) {
                    Toast.makeText(RegisterStepThreeActivity.this, "注册失败" + s, Toast.LENGTH_SHORT).show();
                }
            });

        } else {//用户注册信息验证失败，提醒用户核对
            Toast.makeText(this,"注册信息填写有误，请核对后注册！",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用户用注册成功的帐号登录
     * @param phoneNumber 注册用电话号码
     * @param password  注册用密码
     */
    private void userLogin(String phoneNumber, String password) {
        BmobUser.loginByAccount(this, phoneNumber, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    saveUserToJson(user);
                    Intent intent = new Intent(RegisterStepThreeActivity.this, MainActivity.class);
                    intent.putExtra("type","register");
                    startActivity(intent);
                    RegisterStepThreeActivity.this.finish();
                } else {
                    Toast.makeText(RegisterStepThreeActivity.this, "登录信息有误，请确认后登录", Toast.LENGTH_SHORT).show();

                }
            }
        });
        RegisterStepThreeActivity.this.finish();

    }


    /**
     * 以 json格式的数据保存用户信息到本地
     * @param user
     */
    public void saveUserToJson(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        File userDir = FileUtils.getUserDir(user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }
        File file = new File(FileUtils.getCacheDir(user), user.getMobilePhoneNumber() + ".json");
        if (file.exists()) {
            file.delete();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(userJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 设置用户注册头像选择窗口
     * @param view
     */
    @OnClick(R.id.iv_register_head_portrait)
    public void setRegisterHeadPortrait(View view) {
        mChoosePhoto = true;
        //实例化SelectPicPopupWindow
        menuWindow = new SelectPicPopupWindow(RegisterStepThreeActivity.this, itemsOnClick);
        //显示窗口
        menuWindow.showAtLocation(RegisterStepThreeActivity.this.findViewById(R.id.rl_register_three),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
    }


    /**
     * 如果用户没有选择上传用户头像，则设置为默认用户头像
     */
    public void getDefaultHeadPortrait() {
        Drawable defaultHeadPortrait = getResources().getDrawable(R.drawable.user_icon_default);
        Bitmap bitmap = ((BitmapDrawable) defaultHeadPortrait).getBitmap();
        saveBitmap(bitmap, photoPath, fileName + ".jpg");
        uploadLogo(new File(FileUtils.getIconDir(mPhoneNumber), fileName + ".jpg"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_TAKE:
                cropImageUri(photoUri, 300, 300, REQUESTCODE_CUTTING);
                break;
            case REQUESTCODE_PICK:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    saveBitmap(bitmap, photoPath, fileName + ".jpg");
                    ivRegisterHeadPortrait.setImageBitmap(bitmap);
                    uploadLogo(new File(FileUtils.getIconDir(mPhoneNumber), fileName + ".jpg"));
                }else{
                    getDefaultHeadPortrait();
                }
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    deleteDefaultLogo(mUser);
                    bitmap = decodeUriAsBitmap(photoUri);
                    ivRegisterHeadPortrait.setImageBitmap(bitmap);
                    uploadLogo(new File(FileUtils.getIconDir(mPhoneNumber), fileName + ".jpg"));
                }else{
                    getDefaultHeadPortrait();
                }
                break;
        }

    }

    /**
     * 删除原有用户头像
     * @param user
     */
    private void deleteDefaultLogo(User user) {
        BmobFile file = new BmobFile();
        file.setUrl(user.getPhotoUrl());
        file.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                System.out.println("删除原头像成功");
            }

            @Override
            public void onFailure(int i, String s) {
                System.out.println("删除原头像失败" + s);
            }
        });
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            menuWindow.dismiss();
            switch (view.getId()) {
                case R.id.btn_take_photo:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File outputImage = new File(imagePath, fileName + ".jpg");
                    File outputImage = new File(photoPath, fileName + ".jpg");
                    try {
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //将File对象转换为Uri并启动照相程序
                    photoUri = Uri.fromFile(outputImage);
                    //下面这句指定调用相机拍照后的照片存储的路劲
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                case R.id.btn_pick_photo:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true); // no face detection
                    startActivityForResult(intent, REQUESTCODE_PICK);
                    break;
                case R.id.btn_cancel:
                default:
                    break;
            }
        }
    };

    /**
     * Uri保存成bitmap图片
     * @param uri
     * @return
     */
    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 图片裁剪
     */
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm, File path, String fileName) {
        System.out.println(path.getAbsolutePath());
        File f = new File(path, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 上传用户图片
     * @param userlogo
     */
    public void uploadLogo(final File userlogo) {

        if (userlogo != null) {
            final BmobFile logo = new BmobFile(userlogo);
            System.out.println(userlogo.getAbsolutePath());
            final ProgressDialog progressDialog = new ProgressDialog(RegisterStepThreeActivity.this);
            progressDialog.setMessage("正在生成用户头像。。。");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            logo.uploadblock(RegisterStepThreeActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    url = logo.getFileUrl(RegisterStepThreeActivity.this);
                    System.out.println(url+"-------------------"+"上传成功");
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int i, String s) {
                    System.out.println("上传失败" + s);
                    progressDialog.dismiss();

                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        super.onDestroy();
    }

    public void insertPhotoUrl(User user) {
        user.update(this, user.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                System.out.println("更新成功。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
            }

            @Override
            public void onFailure(int i, String s) {
                System.out.println("更新失败。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。" + s);

            }
        });
    }
}
