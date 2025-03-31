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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Adapter.Home_Adapter;
import com.example.duan1.Dao.LuuHD_Dao;
import com.example.duan1.Dao.SanPham_Dao;
import com.example.duan1.Dao.User_Dao;
import com.example.duan1.Model.SanPham;
import com.example.duan1.Model.TheLoai;
import com.example.duan1.Model.User;
import com.example.duan1.R;

import java.util.ArrayList;

public class Home_Frg extends Fragment {

    RecyclerView recycler_SPBanChay;
    private Home_Adapter homeAdapter;
    private ArrayList<SanPham> listSpTopOut = new ArrayList<>();
    LuuHD_Dao luuHDDAO;
    SanPham_Dao sanPhamDAO;
    LinearLayout layoutParent;
    User_Dao userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_frgm, container, false);
        ImageView imgNotifi = view.findViewById(R.id.imgNotifi);
        layoutParent = view.findViewById(R.id.layoutParent);
        recycler_SPBanChay = view.findViewById(R.id.recycler_SPBanChay);
        TextView txtHello = view.findViewById(R.id.txtHello);
        luuHDDAO = new LuuHD_Dao(getContext());
        sanPhamDAO = new SanPham_Dao(getContext());
        userDAO = new User_Dao(getContext());

        SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);
        int maUserNow = pref.getInt("MA", 0);
        User user = userDAO.getUser(maUserNow);
        String fullName = user.getFullName();

        txtHello.setText("Xin chào, " + fullName + "!");

        ArrayList<SanPham> listSanPham = sanPhamDAO.getAllProduct(0);
        ArrayList<Integer> listMaSPTop = luuHDDAO.getTopSP();
        for (int i = 0; i < listMaSPTop.size(); i++) {
            for (int j = 0; j < listSanPham.size(); j++) {
                if (listMaSPTop.get(i) == listSanPham.get(j).getId()){
                    listSpTopOut.add(listSanPham.get(j));
                }
            }
        }

        ArrayList<TheLoai> listLoaiSP = sanPhamDAO.getDSLSP();
        for (int i = 0; i < listLoaiSP.size(); i++) {
            ArrayList<SanPham> listSP = sanPhamDAO.getSPofTL(listLoaiSP.get(i).getMaLoai());
            if (listSP.size() != 0){
                View addLayout = inflater.inflate(R.layout.list_san_pham, null);
                TextView tittle = addLayout.findViewById(R.id.txtSPHomeTittle);
                tittle.setText(listLoaiSP.get(i).getTenLoai());
                RecyclerView recyclerViewAdd = addLayout.findViewById(R.id.recycler_SPTheoLoai);
                Home_Adapter homeAdapter1 = new Home_Adapter(listSP, getContext());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewAdd.setLayoutManager(linearLayoutManager);
                recyclerViewAdd.setAdapter(homeAdapter1);
                layoutParent.addView(addLayout);
            }
        }

        homeAdapter = new Home_Adapter(listSpTopOut ,getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_SPBanChay.setLayoutManager(linearLayoutManager);
        recycler_SPBanChay.setAdapter(homeAdapter);

//        Notifi
        imgNotifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_notifi);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText btnDongNotifi = dialog.findViewById(R.id.btnDongNotifi);
                btnDongNotifi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

        return view;
    }
}