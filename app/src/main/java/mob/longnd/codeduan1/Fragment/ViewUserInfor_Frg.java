package mob.longnd.codeduan1.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.User;
import com.example.duan1.R;


public class ViewUserInfor_Frg extends Fragment {

    TextView txtViewInfoFullName, txtViewInfoUserName, txtViewInfoChucVu, txtViewInfoSDT, txtViewInfoNamSinh;
    EditText btnViewInfoSua, btnViewInfoXoa;
    User user;
    User_Dao userDAO;
    ImageView btnBackViewUserInfo;

    public ViewUserInfor_Frg(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_user_infor_frgm, container, false);

//        Ánh xạ
        txtViewInfoFullName = view.findViewById(R.id.txtViewInfoFullName);
        txtViewInfoUserName = view.findViewById(R.id.txtViewInfoUserName);
        txtViewInfoChucVu = view.findViewById(R.id.txtViewInfoChucVu);
        txtViewInfoSDT = view.findViewById(R.id.txtViewInfoSDT);
        txtViewInfoNamSinh = view.findViewById(R.id.txtViewInfoNamSinh);
        btnViewInfoSua = view.findViewById(R.id.btnViewInfoSua);
        btnViewInfoXoa = view.findViewById(R.id.btnViewInfoXoa);
        btnBackViewUserInfo = view.findViewById(R.id.btnBackViewUserInfo);

        userDAO = new User_Dao(getContext());

//        Settext
        txtViewInfoFullName.setText(user.getFullName());
        txtViewInfoUserName.setText(user.getUsername());
        txtViewInfoChucVu.setText(user.getTenChucVu());
        txtViewInfoSDT.setText(user.getSDT());
        txtViewInfoNamSinh.setText(user.getNamSinh() + "");

        if (user.getMaChucVu() == 1){
            btnViewInfoXoa.setVisibility(View.GONE);
            btnViewInfoSua.setVisibility(View.GONE);
        }

//        Button Sửa - Chuyển Fragment hiển thị thông tin nhân viên
        btnViewInfoSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SuaNV_Frg(user));
            }
        });

//        Button Xóa
        btnViewInfoXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Dialog xác nhận xóa
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_confirm);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView dialog_confirm_content = dialog.findViewById(R.id.dialog_confirm_content);
                EditText btnDialogHuy = dialog.findViewById(R.id.btnDialogHuy);
                EditText btnDialogXN = dialog.findViewById(R.id.btnDialogXN);

                dialog_confirm_content.setText("Bạn chắc chắn muốn xóa nhân viên!");

//                Set Click Button Dialog Hủy
                btnDialogHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Hủy", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

//                Set Click Button Dialog Xác Nhận Xóa
                btnDialogXN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Gọi DAO xóa nhân viên
                        boolean kiemtra = userDAO.deleteUser(user.getID_User());
                        if (kiemtra){
                            Toast.makeText(getContext(), "Đã xóa khách hàng!", Toast.LENGTH_SHORT).show();
                            loadFragment(new ThongKeNV_Frg());
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

//        Back
        btnBackViewUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ThongKeNV_Frg());
            }
        });
        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}