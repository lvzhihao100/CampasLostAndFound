package com.edu.claf.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.BaseApplication;
import com.edu.claf.R;
import com.edu.claf.Utils.BitmapUtils;
import com.edu.claf.Utils.FileUtils;
import com.edu.claf.bean.User;
import com.edu.claf.view.ProgressView;
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
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 账户设置界面
 */
public class AccountSettingActivity extends AppCompatActivity {


    //拍照获取图片
    private static final int REQUESTCODE_TAKE = 1;
    //从相册选取图片
    private static final int REQUESTCODE_PICK = 2;
    //裁剪图片
    private static final int REQUESTCODE_CUTTING = 3;
    //重置昵称
    private static final int RESET_NICK_NAME = 5;
    //请求修改学校信息
    private static final int REQUESTCODE_SCHOOL = 6;
    //用户头像文件夹地址
    private static String ICON_FILE_LOCATION = null;
    //其他用户头像文件夹地址
    private static String IMAGE_FILE_LOCATION = null;
    //文件路径
    private static String PATH = null;
    //文件名称
    private String fileName;


    @InjectView(R.id.nickname_useless)
    TextView tvNicknameUseless;
    @InjectView(R.id.nickname)
    TextView tvNickName;
    @InjectView(R.id.tv_change_password)
    TextView tvChangePassword;
    @InjectView(R.id.tv_telephone)
    TextView tvTelephone;
    @InjectView(R.id.tv_user_portrait)
    TextView tvUserPortrait;
    @InjectView(R.id.iv_portrait)
    ImageView ivPortrait;
    @InjectView(R.id.tv_as_change_place)
    TextView tvChangPlace;
    @InjectView(R.id.tv_as_change_school)
    TextView tvChangedSchool;
    private User mUser;

    SelectPicPopupWindow menuWindow;
    private Uri photoUri;
    private Uri imageUri;
    private File photoPath;
    private File imagePath;
    private FileOutputStream out;
    private String url;
    private FileOutputStream fos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_account_setting);
        BaseApplication.getApplication().addActivity(this);
        mUser = BmobUser.getCurrentUser(this, User.class);
        ButterKnife.inject(this);
        if (mUser != null) {
            ICON_FILE_LOCATION = FileUtils.getIconDir(mUser.getMobilePhoneNumber()).getAbsolutePath();
            IMAGE_FILE_LOCATION = FileUtils.getImageDir().getAbsolutePath();
            PATH = FileUtils.getIconDir(mUser.getMobilePhoneNumber()).getAbsolutePath();
            photoUri = Uri.parse(ICON_FILE_LOCATION);
            imageUri = Uri.parse(IMAGE_FILE_LOCATION);
            photoPath = FileUtils.getIconDir(mUser.getMobilePhoneNumber());
            imagePath = FileUtils.getImageDir();
            fileName = mUser.getMobilePhoneNumber();
            tvTelephone.setText(mUser.getMobilePhoneNumber());
            tvNickName.setText(mUser.getNickName());
            String realPath = PATH + File.separator + mUser.getMobilePhoneNumber() + ".jpg";
            ivPortrait.setImageBitmap(BitmapUtils.getBitmapFromSdCard(realPath, 2));
            tvChangedSchool.setText(mUser.getSchoolName());
        } else {
            tvTelephone.setText("");
        }
    }


    /**
     * 重置用户昵称
     * @param view
     */
    @OnClick(R.id.nickname_useless)
    public void resetNickName(View view) {
        String nickName = tvNickName.getText().toString().trim();
        Intent intent = new Intent(AccountSettingActivity.this, ResetNickNameActivity.class);
        intent.putExtra("nickname", nickName);
        startActivityForResult(intent, RESET_NICK_NAME);
    }

    /**
     * 重置用户密码
     * @param view
     */
    @OnClick(R.id.tv_change_password)
    public void resetPassword(View view) {
        Intent intent = new Intent(AccountSettingActivity.this, ResetPasswordActivity.class);
        intent.putExtra("type","accountSettingReset");
        startActivity(intent);
    }


    /**
     * 用户重置头像
     * @param view
     */
    @OnClick(R.id.tv_user_portrait)
    public void setUserPortrait(View view) {
        //实例化SelectPicPopupWindow
        menuWindow = new SelectPicPopupWindow(AccountSettingActivity.this, itemsOnClick);
        //显示窗口
        menuWindow.showAtLocation(AccountSettingActivity.this.findViewById(R.id.account_setting),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
    }

    /**
     * 用户重置学校信息
     * @param view
     */
    @OnClick(R.id.tv_as_change_place)
    public void resetSchool(View view) {
        Intent intent = new Intent(this, ChooseProvinceActivity.class);
        intent.putExtra("type", "location_school");
        startActivityForResult(intent, REQUESTCODE_SCHOOL);
    }

    /**
     * 对请求activity的返回数据做出处理
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //拍照
            case REQUESTCODE_TAKE:
                cropImageUri(photoUri, 300, 300, REQUESTCODE_CUTTING);
                break;
            //选择照片
            case REQUESTCODE_PICK:
                if (data != null) {
                    Bitmap bitmap = data.getParcelableExtra("data");
                    saveBitmap(bitmap, photoPath, fileName + ".jpg");
                    ivPortrait.setImageBitmap(bitmap);
                    uploadLogo(new File(FileUtils.getIconDir(mUser.getMobilePhoneNumber()), mUser.getMobilePhoneNumber() + ".jpg"));
                }
                break;
            //对照片进行裁剪
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    Bitmap bitmap = decodeUriAsBitmap(photoUri);
                    Bitmap bitmap1 = decodeUriAsBitmap(imageUri);
                    saveBitmap(bitmap, photoPath, fileName + ".jpg");
                    uploadLogo(new File(FileUtils.getIconDir(mUser.getMobilePhoneNumber()), mUser.getMobilePhoneNumber() + ".jpg"));
                    ivPortrait.setImageBitmap(bitmap);
                }
                break;
            //重置昵称返回新昵称
            case RESET_NICK_NAME:
                if (data != null) {
                    tvNickName.setText(data.getStringExtra("newNickName"));
                }
                break;
            //重置学校返回的新学校
            case REQUESTCODE_SCHOOL:
                if (data != null) {
                    String school = data.getStringExtra("choose_school");
                    String province = data.getStringExtra("choose_province");
                    final ProgressView progressView = new ProgressView(this);
                    ProgressView.getProgressDialog(this, "重置中。。。");
                    User user = BmobUser.getCurrentUser(this, User.class);
                    user.setSchoolName(school);
                    user.setSchoolCity(province);
                    //更新用户学校信息
                    user.update(this, user.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AccountSettingActivity.this, "学校重置成功", Toast.LENGTH_SHORT).show();
                            ProgressView.progressDialogDismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(AccountSettingActivity.this, "学校重置失败", Toast.LENGTH_SHORT).show();
                            ProgressView.progressDialogDismiss();
                        }
                    });
                    tvChangedSchool.setText(school);
                }
                break;
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            menuWindow.dismiss();
            switch (view.getId()) {
                //选择通过拍照方式得到用户图像
                case R.id.btn_take_photo:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File outputImage2 = new File(photoPath, fileName + ".jpg");
                    try {
                        if (outputImage2.exists()) {
                            outputImage2.delete();
                        }
                        outputImage2.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //将File对象转换为Uri并启动照相程序
                    photoUri = Uri.fromFile(outputImage2);
                    //下面这句指定调用相机拍照后的照片存储的路劲
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                //选择通过相册方式修改用户头像
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
                    intent.putExtra("noFaceDetection", true);
                    startActivityForResult(intent, REQUESTCODE_PICK);
                    break;
                case R.id.btn_cancel:
                default:
                    break;
            }
        }
    };


    /**
     * 根据图片Uri得到bitmap图片
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
     * @param uri 同一资源标识符
     * @param outputX 横向放缩
     * @param outputY 纵向放缩
     * @param requestCode 请求码
     */
    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保存用户头像到本地
     */
    public void saveBitmap(Bitmap bm, File path, String fileName) {
        File f = new File(path, fileName);
        File image = new File(imagePath, fileName);
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
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void backPersonCentral(View view) {
        onBackPressed();
    }


    /**
     * 上传新的用户头像
     * @param userlogo
     */
    public void uploadLogo(final File userlogo) {
        deleteUserLogo(mUser);
        if (userlogo != null) {
            final BmobFile logo = new BmobFile(userlogo);
            final ProgressDialog progressDialog = new ProgressDialog(AccountSettingActivity.this);
            progressDialog.setMessage("正在设置。。。");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            logo.uploadblock(AccountSettingActivity.this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    url = logo.getFileUrl(AccountSettingActivity.this);
                    Toast.makeText(AccountSettingActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    updatePhotoUrl(mUser);
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    /**
     * 删除更新前用户头像
     * @param user
     */
    private void deleteUserLogo(User user) {
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

    /**
     * 更新用户头像保存地址
     * @param user
     */
    public void updatePhotoUrl(User user) {
        User updateUser = new User();
        updateUser.setPhotoUrl(url);
        updateUser.update(this, user.getObjectId(), new UpdateListener() {
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

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
