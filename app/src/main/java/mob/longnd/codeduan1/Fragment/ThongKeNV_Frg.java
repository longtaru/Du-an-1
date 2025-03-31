package mob.longnd.codeduan1.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Adapter.ThongKeNV_Adapter;
import com.example.duan1.Dao.LuuHD_Dao;
import com.example.duan1.Model.LuuHoaDon;
import com.example.duan1.R;

import java.util.ArrayList;

public class ThongKeNV_Frg extends Fragment {

    public RecyclerView recycler_TKNV;
    public LuuHD_Dao luuHDDAO;
    ArrayList<LuuHoaDon> listNhanVien;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_k_nhan_vien_frgm, container, false);

//        Ánh xạ các View;
        ImageView btnBackTKNV = view.findViewById(R.id.btnBackTKNV);
        TextView txtTKNVSoLg = view.findViewById(R.id.txtTKNVSoLg);
        recycler_TKNV = view.findViewById(R.id.recycler_TKNV);
        luuHDDAO = new LuuHD_Dao(getContext());

        listNhanVien = luuHDDAO.tkNhanVien();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_TKNV.setLayoutManager(linearLayoutManager);
        ThongKeNV_Adapter adapterTKNV = new ThongKeNV_Adapter(getContext(), listNhanVien);
        recycler_TKNV.setAdapter(adapterTKNV);

        txtTKNVSoLg.setText(listNhanVien.size() + " người");

        btnBackTKNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Account_Frg());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}