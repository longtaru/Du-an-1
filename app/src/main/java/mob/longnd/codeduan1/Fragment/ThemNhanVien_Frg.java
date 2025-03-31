package mob.longnd.codeduan1.Fragment;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.User;
import com.example.duan1.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThemNhanVien_Frg extends Fragment {
    EditText edtUser, edtName, edtPassword, edtSDT, edtNamSinh, btnAddThemNV, btnHuyThemNV;
    User_Dao userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_nhan_vien_frgm, container, false);
        ImageView btnBackThemNV = view.findViewById(R.id.btnBackThemNV);
        btnBackThemNV.setOnClickListener(new View.OnClickListener() {
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

//        Ánh xạ các View
        edtName = view.findViewById(R.id.edTenNhanVien);
        edtUser = view.findViewById(R.id.edUsername);
        edtPassword = view.findViewById(R.id.edPassword);
        edtSDT = view.findViewById(R.id.edSDT);
        edtNamSinh = view.findViewById(R.id.edNamSinh);
        btnAddThemNV = view.findViewById(R.id.btnAddThemNV);
        btnHuyThemNV = view.findViewById(R.id.btnHuyThemNV);
        userDAO = new User_Dao(getActivity());
//        Sự kiện Button Thêm OnClick
        btnAddThemNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Khởi tạo Model

                String fullName = edtName.getText().toString();
                String userName = edtUser.getText().toString();
                String passWord = edtPassword.getText().toString();
                String userSDT = edtSDT.getText().toString();
                String namSinh = edtNamSinh.getText().toString();

//                Check Form
                if (checkEdt()) {
//                    Kiểm tra trùng lặp UserName
                    int checkValid = userDAO.checkValid(userName);
                    if (checkValid != 0) {
//                        UserName đã tồn tại -> Thông báo lỗi, Nhập lại
                        Toast.makeText(getContext(), "Tên tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                        edtUser.setTextColor(Color.RED);
                        edtUser.setError("UserName đã tồn tại.");
                    } else {
//                        UserName chưa tồn tại -> Thêm Account
                        int mNamSinh = Integer.parseInt(namSinh);
                        User user = new User(fullName, userName, passWord, 2, userSDT, mNamSinh);
                        if (userDAO.insertUser(user) < 0) {
                            Toast.makeText(getActivity(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                            resetEdt();
                        }
                    }
                }
            }
        });
        btnHuyThemNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetEdt();
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //    Reset Edittext
    private void resetEdt() {
        edtName.setText("");
        edtName.setHintTextColor(Color.BLACK);
        edtName.setError(null);
        edtUser.setText("");
        edtUser.setHintTextColor(Color.BLACK);
        edtUser.setError(null);
        edtPassword.setText("");
        edtPassword.setHintTextColor(Color.BLACK);
        edtPassword.setError(null);
        edtSDT.setText("");
        edtSDT.setHintTextColor(Color.BLACK);
        edtSDT.setError(null);
        edtNamSinh.setText("");
        edtNamSinh.setHintTextColor(Color.BLACK);
        edtNamSinh.setError(null);
    }
//    Check Form

    private boolean checkEdt() {
        boolean checkAdd = true;

        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Vui lòng nhập!");
            edtName.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (edtUser.getText().toString().isEmpty()) {
            edtUser.setError("Vui lòng nhập!");
            edtUser.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Vui lòng nhập!");
            edtPassword.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (edtSDT.getText().toString().isEmpty()) {
            edtSDT.setError("Vui lòng nhập!");
            edtSDT.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (!edtSDT.getText().toString().startsWith("0")) {
            edtSDT.setError("Số điện thoại không hợp lệ!");
            edtSDT.setText(null);
            edtSDT.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (edtNamSinh.getText().toString().isEmpty()) {
            edtNamSinh.setError("Vui lòng nhập!");
            edtNamSinh.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateAndTime = simpleDateFormat.format(new Date());
        String curYear = currentDateAndTime.substring(6,10);
        Log.d(TAG, "Current Year: " + curYear);
        int mCurYear = Integer.parseInt(curYear);
        int fromYear = mCurYear - 70;
        int toYear = mCurYear - 18;
        String strNamSinh = edtNamSinh.getText().toString();
        int namSinh = Integer.parseInt(strNamSinh);
        if ((namSinh > toYear) || (namSinh < fromYear)){
            edtNamSinh.setError("Năm sinh không hợp lệ!");
            edtNamSinh.setText(null);
            checkAdd = false;
        }

        return checkAdd;
    }

}