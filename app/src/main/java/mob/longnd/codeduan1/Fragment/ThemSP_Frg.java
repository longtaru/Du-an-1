package mob.longnd.codeduan1.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duan1.Dao.SanPham_Dao;
import com.example.duan1.Model.TheLoai;
import com.example.duan1.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ThemSP_Frg extends Fragment {
    public static final int RESULT_OK = -1;
    public static final int DEFAULT = 0;
//    private ImageView AddImg;
    private EditText edName, edPrice, edMoTa, btnAddSP, btnHuySP;
    private  ImageView addImage;
    AutoCompleteTextView edtLoaiSP;
    private SanPham_Dao sanPhamDAO;
    final int REQUEST_CODE_GALLERY = 999;
    int SELECT_PICTURE_CREATE = 200;
    int SELECT_PICTURE_UPDATE = 100;

    String strTenSP, strGiaban, strLoaiSP, strMota;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_s_p_frgm, container, false);
        // Ánh xạ
        sanPhamDAO = new SanPham_Dao(getActivity());
        ImageView btnBackThemSP = view.findViewById(R.id.btnBackThemSP);

//        addImg = view.findViewById(R.id.add_image);
        addImage = view.findViewById(R.id.add_image);
        edName = view.findViewById(R.id.edNameSP);
        edPrice = view.findViewById(R.id.edPrice);
        edMoTa = view.findViewById(R.id.edMoTa);
        edtLoaiSP = view.findViewById(R.id.edtLoaiSP);
        btnAddSP = view.findViewById(R.id.btnAcceptSP);
        btnHuySP = view.findViewById(R.id.btnHuySp);

        //khi ấn vào thêm ảnh sẽ đưa đến thư viện để chọn ảnh
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE_UPDATE);
                Toast.makeText(getActivity(), "mmmmmmmmm", Toast.LENGTH_SHORT).show();
            }
        });


        btnBackThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Account_Frg());
            }
        });



        // Set Data cho spnLoaiSP
        ArrayList<TheLoai> listTheLoai = sanPhamDAO.getDSLSP();
        ArrayList<String> listTenTL = new ArrayList<>();
        ArrayList<Integer> listMaTL = new ArrayList<>();
        int listTheLoaiSize = listTheLoai.size();
        if (listTheLoaiSize != 0) {
            for (int i = 0; i < listTheLoaiSize; i++) {
                TheLoai theLoaiModel = listTheLoai.get(i);
                listTenTL.add(theLoaiModel.getTenLoai());
                listMaTL.add(theLoaiModel.getMaLoai());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (getContext(), android.R.layout.select_dialog_item, listTenTL);

        edtLoaiSP.setThreshold(1);
        edtLoaiSP.setAdapter(adapter);

        // Set sự kiện Click Button Thêm
        btnAddSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strTenSP = edName.getText().toString();
                strGiaban = edPrice.getText().toString();
                strMota = edMoTa.getText().toString();
                strLoaiSP = edtLoaiSP.getText().toString();
                boolean checkTL = false;

                // AutoComplete Text, Kiểm tra tồn tại loại sản phẩm.
                int index = 0;
                for (int i = 0; i < listTheLoaiSize; i++) {
                    String mTenLoai = listTenTL.get(i);
                    if (mTenLoai.equals(strLoaiSP)) {
                        index = i;
                        checkTL = true;
                        break;
                    }
                }

                int maLSP = 0;
                if (checkTL) {
                    maLSP = listMaTL.get(index);
                }

                BitmapDrawable bitmapDrawable = (BitmapDrawable) addImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                byte[] b = baos.toByteArray();
//                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                if (checkEdt()) {
                    if (checkTL) {
                        sanPhamDAO.insertData(b, strTenSP, Double.parseDouble(strGiaban), maLSP, strMota);
                        Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        resetEdt();
                    } else {
                        edtLoaiSP.setError("Loại sản phẩm không tồn tại!");
                        edtLoaiSP.setText("");
                    }
                }
            }
        });

        // Set sự kiện Click Button Hủy
        btnHuySP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetEdt();
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null){
                if(requestCode == SELECT_PICTURE_CREATE){
                    addImage.setImageURI(selectedImageUri);
                }else{
                    addImage.setImageURI(selectedImageUri);
                }

            }

        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = (getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Reset Edittext
    private void resetEdt() {
//        AddImg.setImageResource(R.drawable.img_add_img);
        edName.setText("");
        edName.setHintTextColor(Color.BLACK);
        edPrice.setText("");
        edPrice.setHintTextColor(Color.BLACK);
        edtLoaiSP.setText("");
        edtLoaiSP.setHintTextColor(Color.BLACK);
        edMoTa.setText("");
        edMoTa.setHintTextColor(Color.BLACK);
    }

    // Check Form
    private boolean checkEdt() {
        boolean checkAdd = true;
        if (strTenSP.isEmpty()) {
            edName.setError("Vui lòng nhập!");
            edName.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (strGiaban.isEmpty()) {
            edPrice.setError("Vui lòng nhập!");
            edPrice.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (strLoaiSP.isEmpty()) {
            edtLoaiSP.setError("Vui lòng nhập!");
            edtLoaiSP.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        if (strMota.isEmpty()) {
            edMoTa.setError("Vui lòng nhập!");
            edPrice.setHintTextColor(Color.RED);
            checkAdd = false;
        }

        return checkAdd;
    }
}
