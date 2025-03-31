package mob.longnd.codeduan1.Fragment;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Adapter.ThongKeDT_Adapter;
import com.example.duan1.Dao.LuuHD_Dao;
import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.LuuHoaDon;
import com.example.duan1.Model.User;
import com.example.duan1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ThongKeDT_Frg extends Fragment {

    EditText edtTuNgay,edtDenNgay,btnThongKe;
    ImageView btnBackTKDT;
    TextView txtTongDoanhThu, txtNotifi2;
    boolean ipDateStart, ipDateEnd;
    String dateStart, dateEnd;
    LuuHD_Dao luuHDDAO;
    RecyclerView recycler_TKDT;
    ArrayList<LuuHoaDon> listHD = new ArrayList<>();
    RadioGroup rdoTKDTGr;
    RadioButton rdoTKDTAll, rdoTKDTNV;
    AutoCompleteTextView edtTKDTTenNV;
    LinearLayout boxTenNV, layoutListDT;
    User_Dao userDAO;
    int quyenNow, maUserInput, maUserNow, rdoCheck, caseTK;
    String tenNVInput = "";
    boolean isNameValid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_k_doanh_thu_frgm, container, false);

//        Ánh xạ
        btnBackTKDT = view.findViewById(R.id.btnBackTKDT);
        edtTuNgay = view.findViewById(R.id.edtTKDTStart);
        edtDenNgay = view.findViewById(R.id.edtTKDTEnd);
        btnThongKe = view.findViewById(R.id.edtThongKeDoanhThu);
        txtTongDoanhThu = view.findViewById(R.id.txtTongDoanhThu);
        recycler_TKDT = view.findViewById(R.id.recycler_TKDT);
        rdoTKDTGr = view.findViewById(R.id.rdoTKDTGr);
        rdoTKDTAll = view.findViewById(R.id.rdoTKDTAll);
        boxTenNV = view.findViewById(R.id.boxTenNV);
        rdoTKDTNV = view.findViewById(R.id.rdoTKDTNV);
        layoutListDT = view.findViewById(R.id.layoutListDT);
        txtNotifi2 = view.findViewById(R.id.txtNotifi2);
        edtTKDTTenNV = view.findViewById(R.id.edtTKDTTenNV);

        maUserInput = 0;
        caseTK = 0;

//        Khởi tạo các Contructors
        userDAO = new User_Dao(getContext());
        luuHDDAO = new LuuHD_Dao(getContext());

//        Lấy quyển User Đăng nhập
        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);
        maUserNow = pref.getInt("MA", 0);
        User user = userDAO.getUser(maUserNow);
        quyenNow = user.getMaChucVu();

//        Nếu Account đang đăng nhập là nhân viên -> Ẩn box nhập tên nhân viên và RadioGroup
        if (quyenNow != 1){
            rdoTKDTGr.setVisibility(View.GONE);
            boxTenNV.setVisibility(View.GONE);
            maUserInput = maUserNow;
            caseTK = 1;
            rdoCheck = -1;
            listHD = luuHDDAO.getAllHoaDon(caseTK, maUserInput);
            goneListTK();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recycler_TKDT.setLayoutManager(layoutManager);
            ThongKeDT_Adapter TKDTAdapter = new ThongKeDT_Adapter(getContext(), listHD);
            recycler_TKDT.setAdapter(TKDTAdapter);
            double doanhThu = luuHDDAO.getAllDoanhThu(caseTK, maUserInput);
            String fDoanhThu = String.format("%,.0f VNĐ", doanhThu);
            txtTongDoanhThu.setText(fDoanhThu);
        }
        else {
            tkAllDoanhThu();
            goneListTK();
        }

//        Check Radio Button Check
        switch (rdoCheck) {
            case 0: {
                rdoTKDTAll.setChecked(true);
                boxTenNV.setVisibility(View.GONE);
                caseTK = 2;
                tkAllDoanhThu();
                goneListTK();
            }
            break;
            case 1: {
                rdoTKDTNV.setChecked(true);
                caseTK = 3;
                clearListTK();
                goneListTK();
            }
            break;
        }

//        Sự kiện Rdo Check
        rdoTKDTAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdoCheck = 0;
                    boxTenNV.setVisibility(View.GONE);
                    caseTK = 2;
                    maUserInput = -1;
                    tkAllDoanhThu();
                    goneListTK();
                }
            }
        });

        rdoTKDTNV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rdoCheck = 1;
                    caseTK = 3;
                    boxTenNV.setVisibility(View.VISIBLE);
                    clearListTK();
                    goneListTK();
                }
            }
        });

//        Set Data cho edtTenNV
        ArrayList<User> listUser = userDAO.getAllUser();
        ArrayList<String> listTenUser = new ArrayList<>();
        ArrayList<Integer> listMaUser = new ArrayList<>();
        int listUserSize = listUser.size();
        if (listUserSize != 0){
            for (int i = 0; i < listUserSize; i++) {
                User userModel = listUser.get(i);
                listTenUser.add(userModel.getFullName());
                listMaUser.add(userModel.getID_User());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, listTenUser);

        edtTKDTTenNV.setThreshold(1);
        edtTKDTTenNV.setAdapter(adapter);

//        Get ngày bắt đầu
        edtTuNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(calendar.DAY_OF_MONTH);
                int thang = calendar.get(calendar.MONTH);
                int nam = calendar.get(calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        String date = "";
                        calendar.set(mYear, mMonth, mDay);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy 00:00:00");
                        date = simpleDateFormat.format(calendar.getTime());
                        String subDate = date.substring(0,10);
                        edtTuNgay.setText(subDate);
                        dateStart = date;
                        ipDateStart = true;
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

//        Get ngày kết thúc
        edtDenNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(calendar.DAY_OF_MONTH);
                int thang = calendar.get(calendar.MONTH);
                int nam = calendar.get(calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        calendar.set(mYear, mMonth, mDay);
                        String date = "";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy 23:59:59");
                        date = simpleDateFormat.format(calendar.getTime());
                        String subDate = date.substring(0,10);
                        edtDenNgay.setText(subDate);
                        dateEnd = date;
                        ipDateEnd = true;
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });

//        Sự kiện Click button Thống kê
        btnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNameValid = false;
                boolean checkDoanhThu = true;
//                Check Tên nhân viên trống nếu CaseTK = 3;
                if (caseTK == 3){
//                    Lấy tên nhân viên từ Edittext
                    tenNVInput = edtTKDTTenNV.getText().toString();
                    if (tenNVInput.isEmpty()){
                        checkDoanhThu = false;
                        edtTKDTTenNV.setHintTextColor(Color.RED);
                        edtTKDTTenNV.setError("Vui lòng nhập!");
                    } else {
//                    Check tên nhân viên không tồn tại
                        ArrayList<User> listNv = userDAO.getAllUser();
                        for (int i = 0; i < listNv.size(); i++) {
                            if (listNv.get(i).getFullName().equals(tenNVInput)){
                                isNameValid = true;
                            }
                        }
                        Log.d(TAG, "isNameValid: " + isNameValid);
                        if (!isNameValid){
                            edtTKDTTenNV.setError("Tên nhân viên không tồn tại!");
                            edtTKDTTenNV.setText(null);
                            Log.d(TAG, "Running!");
                            checkDoanhThu = false;
                            Log.d(TAG, "checkDoanhThu: " + checkDoanhThu);
                        }
                    }
                }
//                Check trống ngày bắt đầu & ngày kết thúc
                if (!ipDateStart){
                    checkDoanhThu = false;
                    edtTuNgay.setHintTextColor(Color.RED);
                    edtTuNgay.setError("Vui lòng nhập!");
                }
                if (!ipDateEnd){
                    checkDoanhThu = false;
                    edtDenNgay.setHintTextColor(Color.RED);
                    edtDenNgay.setError("Vui lòng nhập!");
                }
                if (checkDoanhThu){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date dDateS = null;
                    try {
                        dDateS = simpleDateFormat.parse(dateStart);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date dDateE = null;
                    try {
                        dDateE = simpleDateFormat.parse(dateEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    Check ngày bắt đầu <= ngày kết thúc
                    if (dDateS.before(dDateE)){
//                        Gán giá trị mã nhân viên
                        if (caseTK == 3){
                            int index = 0;
                            for (int i = 0; i < listUserSize; i++) {
                                String mTenUser = listTenUser.get(i);
                                if (mTenUser.equals(tenNVInput)){
                                    index = i;
                                }
                            }
                            maUserInput = listMaUser.get(index);
                        }

//                        Gọi thống kê tổng doanh thu
                        double doanhThu = luuHDDAO.getTongDoanhThu(dateStart, dateEnd, caseTK, maUserInput);
                        String fDoanhThu = String.format("%,.0f VNĐ", doanhThu);
                        txtTongDoanhThu.setText(fDoanhThu);
//                        Get ArrayList
                        listHD = luuHDDAO.getDSHoaDon(dateStart, dateEnd, caseTK, maUserInput);
                        goneListTK();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        recycler_TKDT.setLayoutManager(layoutManager);
                        ThongKeDT_Adapter TKDTAdapter = new ThongKeDT_Adapter(getContext(), listHD);
                        recycler_TKDT.setAdapter(TKDTAdapter);
                    }
                    else {
                        Toast.makeText(getContext(), "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    layoutListDT.setVisibility(View.GONE);
                }
            }
        });

//        Back
        btnBackTKDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Account_Frg());
            }
        });
        return view;
    };
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void tkAllDoanhThu(){
        listHD = luuHDDAO.getAllHoaDon(2, maUserInput);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_TKDT.setLayoutManager(layoutManager);
        ThongKeDT_Adapter TKDTAdapter = new ThongKeDT_Adapter(getContext(), listHD);
        recycler_TKDT.setAdapter(TKDTAdapter);
        double doanhThu = luuHDDAO.getAllDoanhThu(2, maUserInput);
        String fDoanhThu = String.format("%,.0f VNĐ", doanhThu);
        txtTongDoanhThu.setText(fDoanhThu);
    }

    public void clearListTK(){
        if (listHD.size() != 0){
            listHD.clear();
        }
        txtTongDoanhThu.setText("0 VNĐ");
        edtTKDTTenNV.setText(null);
        edtTuNgay.setText(null);
        edtDenNgay.setText(null);
    }

    public void goneListTK(){
        if (listHD.size() == 0){
            layoutListDT.setVisibility(View.GONE);
        }
        else {
            layoutListDT.setVisibility(View.VISIBLE);
        }
    }

}