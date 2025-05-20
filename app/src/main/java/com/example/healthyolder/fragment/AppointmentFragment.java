package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;

import com.example.healthyolder.R;
import com.example.healthyolder.adapter.GoodsAdapter;
import com.example.healthyolder.adapter.TypeAdapter;
import com.example.healthyolder.bean.Configs;
import com.example.healthyolder.bean.GoodsItem;
import com.example.healthyolder.bean.LoginResult;
import com.example.healthyolder.bean.Urls;
import com.example.healthyolder.util.HttpUtil;
import com.example.healthyolder.util.ObjectCallBack;
import com.example.healthyolder.util.ToastUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import okhttp3.Call;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class AppointmentFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView rvType;
    private StickyListHeadersListView listView;
    private EditText etSearch;
    
    private ArrayList<GoodsItem> dataList, typeList, resultList;
    private ArrayList<GoodsItem> originalDataList, originalTypeList;
    private SparseArray<GoodsItem> selectedList;
    private SparseIntArray groupSelect;
    private int start_price;
    private GoodsAdapter myAdapter;
    private TypeAdapter typeAdapter;
    private NumberFormat nf;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.frag_appointment, null);
        }
        ButterKnife.bind(this, view);
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        dataList = new ArrayList<>();
        typeList = new ArrayList<>();
        selectedList = new SparseArray<>();
        groupSelect = new SparseIntArray();
        
        initData();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Do not call super to avoid Exception
    }

    private void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("status", Configs.USER_DOCTOR);
        HttpUtil.getResponse(Urls.DETAILUSER, params, this, new ObjectCallBack<LoginResult>(LoginResult.class) {
            @Override
            public void onSuccess(LoginResult o) {
                int num = 1;
                List<LoginResult.DataBean> arrayList = new ArrayList<>();
                arrayList.addAll(o.getData());
                Collections.sort(arrayList);
                for (int i = 0; i < arrayList.size(); i++) {
                    GoodsItem item = null;
                    item = new GoodsItem(Integer.valueOf(arrayList.get(i).getU_id()), arrayList.get(i).getU_name(),
                            Integer.valueOf(arrayList.get(i).getU_department_id()), arrayList.get(i).getDd_name(),
                            arrayList.get(i).getU_icon(), arrayList.get(i).getU_remark());
                    dataList.add(item);
                    if (i == 0) {
                        typeList.add(item);
                    } else {
                        if (Integer.valueOf(arrayList.get(i).getU_department_id()) != Integer.valueOf(arrayList.get(i - 1).getU_department_id())) {
                            typeList.add(item);
                        }
                    }
                }
                
                // Save original lists for search filtering
                originalDataList = new ArrayList<>(dataList);
                originalTypeList = new ArrayList<>(typeList);
                
                initView();
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
            }
        });
    }

    private void initView() {
        rvType = view.findViewById(R.id.typeRecyclerView);
        listView = view.findViewById(R.id.itemListView);
        etSearch = view.findViewById(R.id.etSearch);
        
        rvType.setLayoutManager(new LinearLayoutManager(getActivity()));
        typeAdapter = new TypeAdapter(this, typeList);
        rvType.setAdapter(typeAdapter);

        myAdapter = new GoodsAdapter(dataList, this);
        listView.setAdapter(myAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (dataList.isEmpty()) {
                    return;
                }
                
                GoodsItem item = dataList.get(firstVisibleItem);
                if (typeAdapter.selectTypeId != item.typeId) {
                    typeAdapter.selectTypeId = item.typeId;
                    typeAdapter.notifyDataSetChanged();
                    rvType.smoothScrollToPosition(getSelectedGroupPosition(item.typeId));
                }
            }
        });
        
        // Setup search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterDoctors(s.toString());
            }
        });
    }
    
    private void filterDoctors(String query) {
        if (query.isEmpty()) {
            // Restore original lists
            dataList.clear();
            dataList.addAll(originalDataList);
            typeList.clear();
            typeList.addAll(originalTypeList);
        } else {
            // Filter data
            ArrayList<GoodsItem> filteredDoctors = new ArrayList<>();
            ArrayList<GoodsItem> filteredTypes = new ArrayList<>();
            SparseIntArray typeIds = new SparseIntArray();
            
            for (GoodsItem doctor : originalDataList) {
                if (doctor.name.toLowerCase().contains(query.toLowerCase()) || 
                    doctor.typeName.toLowerCase().contains(query.toLowerCase())) {
                    filteredDoctors.add(doctor);
                    
                    // Keep track of type IDs for filtered doctors
                    if (typeIds.indexOfKey(doctor.typeId) < 0) {
                        typeIds.put(doctor.typeId, 1);
                        // Find the corresponding type item
                        for (GoodsItem type : originalTypeList) {
                            if (type.typeId == doctor.typeId) {
                                filteredTypes.add(type);
                                break;
                            }
                        }
                    }
                }
            }
            
            dataList.clear();
            dataList.addAll(filteredDoctors);
            typeList.clear();
            typeList.addAll(filteredTypes);
        }
        
        // Refresh adapters
        myAdapter.notifyDataSetChanged();
        typeAdapter.notifyDataSetChanged();
        
        // Update selected type ID if necessary
        if (!typeList.isEmpty()) {
            typeAdapter.selectTypeId = typeList.get(0).typeId;
            rvType.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onClick(View v) {
        // Handle button clicks if needed
    }

    // Get the selected item count by ID
    public int getSelectedItemCountById(int id) {
        GoodsItem temp = selectedList.get(id);
        if (temp == null) {
            return 0;
        }
        return temp.count;
    }
    
    // Get the selected group count by type ID
    public int getSelectedGroupCountByTypeId(int typeId) {
        return groupSelect.get(typeId);
    }

    // Get the selected group position
    public int getSelectedGroupPosition(int typeId) {
        for (int i = 0; i < typeList.size(); i++) {
            if (typeId == typeList.get(i).typeId) {
                return i;
            }
        }
        return 0;
    }

    public void onTypeClicked(int typeId) {
        listView.setSelection(getSelectedPosition(typeId));
    }

    private int getSelectedPosition(int typeId) {
        int position = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).typeId == typeId) {
                position = i;
                break;
            }
        }
        return position;
    }
}