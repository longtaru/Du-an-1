package mob.longnd.codeduan1.Fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Adapter.Product_Adapter;
import com.example.duan1.Dao.SanPham_Dao;
import com.example.duan1.Model.SanPham;
import com.example.duan1.R;

import java.util.ArrayList;

public class Product_Frg extends Fragment {

    private RecyclerView recyclerProduct;
    private Product_Adapter sanPhamAdapter;
    private ArrayList<SanPham> list = new ArrayList<>();
    SanPham_Dao sanPhamDAO;
    int rdoCheck = 0;
    AutoCompleteTextView edt_search;
    TextView txtSearch, txtNotifi3;
    ImageView icDeleteSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_frgm, container, false);
        recyclerProduct = view.findViewById(R.id.recyclerProduct);
        ImageView filterProduct = view.findViewById(R.id.filterProduct);
        edt_search = view.findViewById(R.id.edt_search);
        txtSearch = view.findViewById(R.id.txtSearch);
        icDeleteSearch = view.findViewById(R.id.icDeleteSearch);
        txtNotifi3 = view.findViewById(R.id.txtNotifi3);

        sanPhamDAO = new SanPham_Dao(getContext());

        ArrayList<SanPham> listSP = sanPhamDAO.getAllProduct(0);
        ArrayList<String> listTenSp = new ArrayList<>();
        ArrayList<Integer> listMaSp = new ArrayList<>();
        for (int i = 0; i < listSP.size(); i++) {
            SanPham sanPham = listSP.get(i);
            listTenSp.add(sanPham.getTenSanPham());
            listMaSp.add(sanPham.getId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, listTenSp);

        edt_search.setThreshold(1);
        edt_search.setAdapter(adapter);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchInput = edt_search.getText().toString();

                if (!searchInput.isEmpty()){
                    txtSearch.setVisibility(View.VISIBLE);
                    txtSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: " + searchInput);
                            ArrayList<Integer> listMaSearch = new ArrayList<>();
                            for (int i = 0; i < listTenSp.size(); i++) {
                                if (listTenSp.get(i).contains(searchInput)){
                                    listMaSearch.add(listMaSp.get(i));
                                }
                            }

                            if (listMaSearch.size() == 0){
                                recyclerProduct.setVisibility(View.GONE);
                                txtNotifi3.setVisibility(View.VISIBLE);
                            }

                            ArrayList<SanPham> listOut = new ArrayList<>();
                            for (int i = 0; i < listMaSearch.size(); i++) {
                                listOut.add(sanPhamDAO.getSPofMaSP(listMaSearch.get(i)));
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerProduct.setLayoutManager(linearLayoutManager);
                            Product_Adapter sanPhamAdapter = new Product_Adapter(getContext(), listOut);
                            recyclerProduct.setAdapter(sanPhamAdapter);
                            sanPhamAdapter.notifyDataSetChanged();

                            txtSearch.setVisibility(View.GONE);
                            icDeleteSearch.setVisibility(View.VISIBLE);
                            icDeleteSearch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    edt_search.setText(null);
                                    icDeleteSearch.setVisibility(View.GONE);
                                    recyclerProduct.setVisibility(View.VISIBLE);
                                    txtNotifi3.setVisibility(View.GONE);
                                    createData(0);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sanPhamAdapter = new Product_Adapter(getActivity(), list);
        sanPhamAdapter.notifyDataSetChanged();
        rdoCheck = 0;
        createData(0);

//        Set sự kiện OnClick Filter
        filterProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_filters);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                RadioButton rdoSPAll = dialog.findViewById(R.id.rdoSPAll);
                RadioButton rdoSPGia = dialog.findViewById(R.id.rdoSPGia);
                RadioButton rdoSPTL = dialog.findViewById(R.id.rdoSPTL);

                switch (rdoCheck) {
                    case 0: {
                        rdoSPAll.setChecked(true);
                    }
                    break;
                    case 1: {
                        rdoSPGia.setChecked(true);
                    }
                    break;
                    case 2: {
                        rdoSPTL.setChecked(true);
                    }
                }

                rdoSPAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            rdoCheck = 0;
                            createData(0);
                            dialog.dismiss();
                        }
                    }
                });

                rdoSPGia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            rdoCheck = 1;
                            createData(1);
                            dialog.dismiss();
                        }
                    }
                });

                rdoSPTL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            rdoCheck = 2;
                            createData(2);
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        return view;
    }

    private void createData(int rdoCheck) {
        SanPham_Dao sanPhamDAO = new SanPham_Dao(getActivity());
        ArrayList<SanPham> list = (ArrayList<SanPham>) sanPhamDAO.getAllProduct(rdoCheck);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProduct.setLayoutManager(linearLayoutManager);
        Product_Adapter sanPhamAdapter = new Product_Adapter(getContext(), list);
        recyclerProduct.setAdapter(sanPhamAdapter);
        sanPhamAdapter.notifyDataSetChanged();
    }
}