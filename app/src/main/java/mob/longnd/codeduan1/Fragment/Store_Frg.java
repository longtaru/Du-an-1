package mob.longnd.codeduan1.Fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Adapter.HoaDon_Adapter;
import com.example.duan1.Adapter.Store_Adapter;
import com.example.duan1.Dao.GioHang_Dao;
import com.example.duan1.Dao.HoaDon_Dao;
import com.example.duan1.Dao.LuuHD_Dao;
import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.GioHang;
import com.example.duan1.Model.HoaDon;
import com.example.duan1.Model.LuuHoaDon;
import com.example.duan1.Model.User;
import com.example.duan1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Store_Frg extends Fragment {

    RecyclerView recycle_gioHang;
    GioHang_Dao gioHangDAO;
    User_Dao userDAO;
    HoaDon_Dao hoaDonDAO;
    LuuHD_Dao luuHDDAO;
    ArrayList<GioHang> listGioHang;
    public static TextView txtGHTongTien;
    double tongTien = 0;
    EditText edtGHTenKH;
    EditText edtGHDiaChi;
    ImageView iconRefreshStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_frgm, container, false);

        edtGHTenKH = view.findViewById(R.id.edtGHTenKH);

        edtGHDiaChi = view.findViewById(R.id.edtGHDiaChi);

        gioHangDAO = new GioHang_Dao(getContext());
        userDAO = new User_Dao(getContext());
        hoaDonDAO = new HoaDon_Dao(getContext());
        luuHDDAO = new LuuHD_Dao(getContext());
        recycle_gioHang = view.findViewById(R.id.recycle_giohang);
        listGioHang = gioHangDAO.getGioHang();
        txtGHTongTien = view.findViewById(R.id.txtGHTongTien);
        iconRefreshStore = view.findViewById(R.id.iconRefreshStore);
        createData();
        iconRefreshStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listGioHang = gioHangDAO.getGioHang();
                int listGHSize = listGioHang.size();
                if (listGHSize != 0){
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_confirm);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView dialog_confirm_content = dialog.findViewById(R.id.dialog_confirm_content);
                    EditText btnDialogHuy = dialog.findViewById(R.id.btnDialogHuy);
                    EditText btnDialogXN = dialog.findViewById(R.id.btnDialogXN);

                    dialog_confirm_content.setText("Bạn chắc chắn muốn xóa sản phẩm trong giỏ hàng!");

//                Set Click Button Dialog Hủy
                    btnDialogHuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Hủy", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    btnDialogXN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < listGHSize; i++) {
                                gioHangDAO.deleteGiohang(listGioHang.get(i));
                            }
                            createData();
                            txtGHTongTien.setText("0 VNĐ");
                            Toast.makeText(getContext(), "Đã xóa giỏ hàng!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    
                    
                }
            }
        });


        EditText btnGioHangTT = view.findViewById(R.id.btnGioHangTT);
        btnGioHangTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenKH = edtGHTenKH.getText().toString();
                String diachiKH = edtGHDiaChi.getText().toString();
                createData();
                if (listGioHang.size() == 0){
                    Toast.makeText(getContext(), "Vui lòng chọn sản phẩm!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //                Kiểm tra nhập tên khách hàng
                    if (tenKH.isEmpty() || diachiKH.isEmpty()){
                        edtGHTenKH.setHintTextColor(Color.RED);
                        edtGHTenKH.setError("Vui lòng nhập!");
                        edtGHDiaChi.setHintTextColor(Color.RED);
                        edtGHDiaChi.setError("Vui lòng nhập!");
                    }
                    else {
                        edtGHTenKH.setHintTextColor(Color.BLACK);
                        edtGHDiaChi.setHintTextColor(Color.BLACK);

//                    Lấy tên nhân viên
                        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);
                        int maUser = pref.getInt("MA", 0);
                        User user = userDAO.getUser(maUser);
                        String fullName = user.getFullName();

//                    Lấy ngày tạo hóa đơn
                        Date nowDate = Calendar.getInstance().getTime();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        String ngayTaoHD = simpleDateFormat.format(nowDate);

//                Lấy thông tin hóa đơn - Hiển thị lên dialog
                        Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_thanh_toan);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//                    Ánh xạ View

                        EditText btnHoaDonHuy = dialog.findViewById(R.id.btnHoaDonHuy);
                        EditText btnHoaDonXN = dialog.findViewById(R.id.btnHoaDonXN);

                        TextView txtHDTenNV = dialog.findViewById(R.id.txtHDTenNV);
                        TextView txtHDTenKH = dialog.findViewById(R.id.txtHDTenKH);

                        TextView txtHDNgayBan = dialog.findViewById(R.id.txtHDNgayBan);
                        RecyclerView recycle_hoaDon = dialog.findViewById(R.id.recycle_hoaDon);
                        TextView txtHDTongTien = dialog.findViewById(R.id.txtHDTongTien);

                        TextView txtHDDiaChiKH = dialog.findViewById(R.id.txtHDDiaChi);

//                    Settext cho các View

                        txtHDTenNV.setText(fullName);
                        txtHDTenKH.setText(tenKH);
                        txtHDNgayBan.setText(ngayTaoHD);
                        txtHDDiaChiKH.setText(diachiKH);
                        String outTongTien = String.format("%,.0f", tongTien);
                        txtHDTongTien.setText(outTongTien + "Đ");


                        listGioHang = gioHangDAO.getGioHang();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        recycle_hoaDon.setLayoutManager(linearLayoutManager);
                        HoaDon_Adapter hoaDonAdapter = new HoaDon_Adapter(getContext(), listGioHang);
                        recycle_hoaDon.setAdapter(hoaDonAdapter);

//                Sự kiện Button Hủy
                        btnHoaDonHuy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

//                Sự kiện Button Xác nhận -> Chuyển Hóa đơn vào bảng Lưu hóa đơn
                        btnHoaDonXN.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                            Tạo Model HoaDon, Thêm vào bảng Lưu Hóa đơn
                                HoaDon hoaDon = new HoaDon(maUser, tenKH, ngayTaoHD, 1, diachiKH);
                                boolean check = hoaDonDAO.addHoaDon(hoaDon);
                                if (!check){
                                    Toast.makeText(getContext(), "Fail!", Toast.LENGTH_SHORT).show();
                                }
                                ArrayList<HoaDon> listHoaDon = hoaDonDAO.getHoaDon();
                                int listHDSize = listHoaDon.size();
//                            Lấy ra danh sách Hóa đơn
                                if (listHDSize > 0){
                                    boolean checkLuuHD = true;
                                    for (int i = 0; i < listHDSize; i++) {
                                        HoaDon hoaDonModel = listHoaDon.get(i);
//                                    Tạo Model Lưu Hóa đơn
                                        LuuHoaDon luuHoaDon = new LuuHoaDon(hoaDonModel.getMaHoaDon(),
                                                hoaDonModel.getMaUser(),
                                                hoaDonModel.getTenUser(),
                                                hoaDonModel.getTenKhachHang(),
                                                hoaDonModel.getNgayLapHD(),
                                                hoaDonModel.getMaSP(),
                                                hoaDonModel.getTenSP(),
                                                hoaDonModel.getSoLuong(),
                                                hoaDonModel.getSize(),
                                                hoaDonModel.getDonGia(),
                                                hoaDonModel.getTrangThai(),
                                                hoaDonModel.getDiaChi(),
                                                hoaDonModel.getThanhTien());


//                                    Lưu hóa đơn vào bảng Lưu Hóa đơn
                                        boolean checkAddHD = luuHDDAO.addLuuHD(luuHoaDon);
                                        if (!checkAddHD){
                                            Toast.makeText(getContext(), "Lưu HD Fail!", Toast.LENGTH_SHORT).show();
                                            checkLuuHD = false;
                                        }
                                    }
                                    if (checkLuuHD){
//                                    Lưu hóa đơn thành công -> Xóa thông tin giỏ hàng, hóa đơn
                                        int listGHSize = listHoaDon.size();
                                        if (listGHSize != 0){
                                            for (int i = 0; i < listGHSize; i++) {
                                                gioHangDAO.deleteGiohang(listGioHang.get(i));
                                            }
                                        }
                                        if (listHDSize != 0){
                                            for (int i = 0; i < listHDSize; i++) {
                                                hoaDonDAO.deleteHoaDon(listHoaDon.get(i));
                                            }
                                        }
                                        createData();
                                        Toast.makeText(getContext(), "Mua hàng thành công!", Toast.LENGTH_SHORT).show();
                                        txtGHTongTien.setText("0 VNĐ");
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });

        return view;
    }

    private void createData() {
        gioHangDAO = new GioHang_Dao(getContext());
        listGioHang = gioHangDAO.getGioHang();
        if (listGioHang.size() == 0){
            recycle_gioHang.setVisibility(View.GONE);
        }
        else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recycle_gioHang.setLayoutManager(linearLayoutManager);

            tongTien = gioHangDAO.tongTienGiohang();
            String outTongTien = String.format("%,.0f", tongTien);
            txtGHTongTien.setText(outTongTien + " VNĐ");

            edtGHTenKH.setText(null);
            edtGHTenKH.setError(null);

            edtGHDiaChi.setText(null);
            edtGHDiaChi.setError(null);

            Store_Adapter adapterGioHang = new Store_Adapter(getContext(), listGioHang);
            recycle_gioHang.setAdapter(adapterGioHang);
        }
    }

}