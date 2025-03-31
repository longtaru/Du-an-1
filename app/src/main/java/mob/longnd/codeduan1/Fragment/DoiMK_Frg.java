package mob.longnd.codeduan1.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.User;
import com.example.duan1.R;


public class DoiMK_Frg extends Fragment {

    EditText edOldPass, edNewPass, edConfirmPass;
    EditText btnChange, btnCancel;
    ImageView imgHide1, imgHide2, imgHide3;
    User_Dao userDAO;
    String username, password;
    int maUser;
    boolean chkCheck;
    String oldPass, newPass, confirmPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doi_m_k_frgm, container, false);
        ImageView btnBackDoiMK = view.findViewById(R.id.btnBackDoiMK);
        btnBackDoiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Account_Frg());
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ánh xạ
        edOldPass = view.findViewById(R.id.edOldPass);
        edNewPass = view.findViewById(R.id.edNewPass);
        edConfirmPass = view.findViewById(R.id.edConfirmPass);
        btnChange = view.findViewById(R.id.btnChange);
        btnCancel = view.findViewById(R.id.btnCancel);
        imgHide1 = view.findViewById(R.id.img_hidePassword1);
        imgHide2 = view.findViewById(R.id.img_hidePassword2);
        imgHide3 = view.findViewById(R.id.img_hidePassword3);
        userDAO = new User_Dao(getActivity());

        //set hide pass
        imgHide1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edOldPass.getInputType() != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    edOldPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgHide1.setImageResource(R.drawable.ic_hide_on);
                } else {
                    edOldPass.setInputType(129);
                    imgHide1.setImageResource(R.drawable.ic_visibility_off);
                }
            }
        });
        imgHide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edNewPass.getInputType() != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    edNewPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgHide2.setImageResource(R.drawable.ic_hide_on);
                } else {
                    edNewPass.setInputType(129);
                    imgHide2.setImageResource(R.drawable.ic_visibility_off);
                }
            }
        });
        imgHide3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edConfirmPass.getInputType() != InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    edConfirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgHide3.setImageResource(R.drawable.ic_hide_on);
                } else {
                    edConfirmPass.setInputType(129);
                    imgHide3.setImageResource(R.drawable.ic_visibility_off);
                }
            }
        });
        getDataSSR();

//        Sự kiện Button Hủy
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetForm();
            }
        });

//        Sự kiện Button đổi mật khẩu
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Get Data từ Edittext;
                oldPass = edOldPass.getText().toString();
                newPass = edNewPass.getText().toString();
                confirmPass = edConfirmPass.getText().toString();

                if (checkEdt()) {

                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_confirm);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView dialog_confirm_content = dialog.findViewById(R.id.dialog_confirm_content);
                    EditText btnDialogHuy = dialog.findViewById(R.id.btnDialogHuy);
                    EditText btnDialogXN = dialog.findViewById(R.id.btnDialogXN);

                    dialog_confirm_content.setText("Bạn chắc chắn muốn sửa đổi mật khẩu!");

//                    Set Click Dialog Hủy
                    btnDialogHuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Hủy!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

//                    Set Click Dialog Xác Nhận
                    btnDialogXN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User user = userDAO.getUser(maUser);
                            user.setPassword(newPass);
                            boolean checkUpdate = userDAO.updateUser(user);
                            if (checkUpdate) {
                                Toast.makeText(getActivity(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                remmemberUser(username, newPass, chkCheck);
                                getDataSSR();
                                loadFragment(new Account_Frg());
                            } else {
                                Toast.makeText(getActivity(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private boolean checkEdt() {
        boolean checkAdd = true;

//        Kiểm tra trống MK cũ, MK cũ sai
        if (oldPass.isEmpty()) {
            edOldPass.setError("Vui lòng nhập!");
            edOldPass.setHintTextColor(Color.RED);
            checkAdd = false;
        } else {
            if (!oldPass.equals(password)) {
                edOldPass.setError("Mật khẩu sai!");
                checkAdd = false;
            }
        }

//        Kiểm tra trống MK mới, MK mới giống MK cũ
        if (newPass.isEmpty()) {
            edNewPass.setError("Vui lòng nhập!");
            edNewPass.setHintTextColor(Color.RED);
            checkAdd = false;
        } else {
            if (newPass.equals(password)) {
                edNewPass.setError("Mật khẩu mới trùng mật khẩu cũ!");
                checkAdd = false;
            }
        }

//        Kiểm tra trống xác nhận MK, XNMK khác MK mới
        if (confirmPass.isEmpty()) {
            edConfirmPass.setError("Vui lòng nhập!");
            edConfirmPass.setHintTextColor(Color.RED);
            checkAdd = false;
        } else {
            if (!confirmPass.equals(newPass)) {
                edConfirmPass.setError("Xác nhận mật khẩu sai!");
                checkAdd = false;
            }
        }

        return checkAdd;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void remmemberUser(String u, String p, boolean status) {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!status) {
            editor.clear();
        } else {
            editor.putString("USERNAME", u);
            editor.putString("PASSWORD", p);
            editor.putBoolean("REMEMBER", true);
        }
        editor.commit();
    }

    //    Get Data SharedPreferences
    public void getDataSSR() {
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", MODE_PRIVATE);
        maUser = pref.getInt("MA", 0);
        username = pref.getString("USERNAME", "");
        password = pref.getString("PASSWORD", "");
        chkCheck = pref.getBoolean("REMEMBER", false);
    }

    //    Reset Edittext
    public void resetForm() {
        edOldPass.setText("");
        edOldPass.setHintTextColor(Color.BLACK);
        edOldPass.setTextColor(Color.BLACK);
        edOldPass.setError(null);
        edNewPass.setText("");
        edNewPass.setHintTextColor(Color.BLACK);
        edNewPass.setTextColor(Color.BLACK);
        edNewPass.setError(null);
        edConfirmPass.setText("");
        edConfirmPass.setHintTextColor(Color.BLACK);
        edConfirmPass.setTextColor(Color.BLACK);
        edConfirmPass.setError(null);
    }

}