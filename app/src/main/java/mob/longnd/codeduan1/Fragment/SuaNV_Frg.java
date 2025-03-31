package mob.longnd.codeduan1.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.User;
import com.example.duan1.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SuaNV_Frg extends Fragment {

    User user;
    EditText edtSuaTenNV, edtSuaMatKhau, edtSuaSDT, edtSuaNamSinh, btnHuySuaNV, btnAddSuaNV;
    String tenSua, passSua, sdtSua, namSinhSua;
    User_Dao userDAO;
    ImageView btnBackSuaNV;
    TextView txtSuaNVTittle;
    LinearLayout viewSuaMK;

    public SuaNV_Frg(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sua_n_v_frgm, container, false);

//        Ánh xạ
        edtSuaTenNV = view.findViewById(R.id.edtSuaTenNV);
        edtSuaMatKhau = view.findViewById(R.id.edtSuaMatKhau);
        edtSuaSDT = view.findViewById(R.id.edtSuaSDT);
        edtSuaNamSinh = view.findViewById(R.id.edtSuaNamSinh);
        btnHuySuaNV = view.findViewById(R.id.btnHuySuaNV);
        btnAddSuaNV = view.findViewById(R.id.btnAddSuaNV);
        btnBackSuaNV = view.findViewById(R.id.btnBackSuaNV);
        txtSuaNVTittle = view.findViewById(R.id.txtSuaNVTittle);
        viewSuaMK = view.findViewById(R.id.viewSuaMK);
        
        userDAO = new User_Dao(getContext());

//        Get SharedPreferences Lấy quyền của Account hiện tại đang đăng nhập
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);
        int maUser = pref.getInt("MA", 0);
        User userN = userDAO.getUser(maUser);
        int quyenUser = userN.getMaChucVu();

//        Set tittle, ẩn Mật khẩu nếu Account hiện tại là Admin
        if (quyenUser == user.getMaChucVu()){
            txtSuaNVTittle.setText("Chỉnh Sửa Thông Tin");
            viewSuaMK.setVisibility(View.GONE);
        }

//        Settext
        edtSuaTenNV.setText(user.getFullName());
        edtSuaSDT.setText(user.getSDT());
        edtSuaNamSinh.setText(user.getNamSinh() + "");
        
//        Set Click Xác Nhận
        btnAddSuaNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Gettext
                tenSua = edtSuaTenNV.getText().toString();
                passSua = edtSuaMatKhau.getText().toString();
                sdtSua = edtSuaSDT.getText().toString();
                namSinhSua = edtSuaNamSinh.getText().toString();
                int mNamSinhSua = Integer.parseInt(namSinhSua);
//                Kiểm tra check Form
                if (checkForm()){
//                    Dialog xác nhận sửa thông tin Account
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_confirm);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView dialog_confirm_content = dialog.findViewById(R.id.dialog_confirm_content);
                    EditText btnDialogHuy = dialog.findViewById(R.id.btnDialogHuy);
                    EditText btnDialogXN = dialog.findViewById(R.id.btnDialogXN);

//                    Settext nội dung tin nhắn đối với Admin và User
                    if (quyenUser == user.getMaChucVu()){
                        dialog_confirm_content.setText("Bạn chắc chắn muốn sửa thông tin cá nhân!");
                    }
                    else {
                        dialog_confirm_content.setText("Bạn chắc chắn muốn sửa thông tin nhân viên!");
                    }

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
//                            Settext Model
                            user.setFullName(tenSua);
                            if (!passSua.isEmpty()){
                                user.setPassword(passSua);
                            }
                            user.setSDT(sdtSua);
                            user.setNamSinh(mNamSinhSua);
//                            DAO Update thông tin User
                            boolean check = userDAO.updateUser(user);
                            if (check){
                                Toast.makeText(getContext(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
//                                Ẩn Dialog, trở về màn hình trước
                                if (quyenUser == user.getMaChucVu()){
                                    loadFragment(new UserInfo_Frg());
                                }
                                else {
                                    loadFragment(new ThongKeNV_Frg());
                                }
                            }
                            else {
                                Toast.makeText(getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });

//        Set Click Hủy
        btnHuySuaNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSuaTenNV.setText(user.getFullName());
                edtSuaMatKhau.setText(null);
                edtSuaSDT.setText(user.getSDT());
                edtSuaNamSinh.setText(String.valueOf(user.getNamSinh()));
            }
        });

//        Set Click Back
        btnBackSuaNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quyenUser == user.getMaChucVu()){
                    loadFragment(new UserInfo_Frg());
                }
                else {
                    loadFragment(new ViewUserInfor_Frg(user));
                }
            }
        });

        return view;
    }
    
//    Check form
    public boolean checkForm(){
        boolean checkForm = true;
        if (tenSua.isEmpty()){
            edtSuaTenNV.setError("Vui lòng nhập");
            checkForm = false;
        }
        
        if (sdtSua.isEmpty()){
            edtSuaSDT.setError("Vui lòng nhập");
            checkForm = false;
        }

        if (!sdtSua.startsWith("0")) {
            edtSuaSDT.setError("Số điện thoại không hợp lệ");
            checkForm = false;
        }
        
        if (namSinhSua.isEmpty()){
            edtSuaNamSinh.setError("Vui lòng nhập");
            checkForm = false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateAndTime = simpleDateFormat.format(new Date());
        String curYear = currentDateAndTime.substring(6,10);
        Log.d(TAG, "Current Year: " + curYear);
        int mCurYear = Integer.parseInt(curYear);
        int fromYear = mCurYear - 70;
        int toYear = mCurYear - 18;
        String strNamSinh = edtSuaNamSinh.getText().toString();
        int namSinh = Integer.parseInt(strNamSinh);
        if ((namSinh > toYear) || (namSinh < fromYear)){
            edtSuaNamSinh.setError("Năm sinh không hợp lệ!");
            edtSuaNamSinh.setText(null);
            checkForm = false;
        }

        return checkForm;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}