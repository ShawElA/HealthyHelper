package com.example.healthyolder.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * Created by Administrator on 2025/9/7.
 */

public class AppointmentFragment extends Fragment implements View.OnClickListener {

    View view;
    private RecyclerView rvType;
    private StickyListHeadersListView listView;
    private ArrayList<GoodsItem> dataList,typeList, resultList;
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
//        EventBus.getDefault().register(this);
        ButterKnife.bind(this, view);
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        dataList = new ArrayList<>();
        typeList = new ArrayList<>();
        selectedList = new SparseArray<>();
        groupSelect = new SparseIntArray();
//        if (isFav){
//            commontool.setRightImage(R.mipmap.collect_select);
//        }else {
//            commontool.setRightImage(R.mipmap.collect_unselect);
//        }
        initData();
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private void initData(){
        Map<String, String> params = new HashMap<>();
        params.put("status", Configs.USER_DOCTOR);
        HttpUtil.getResponse(Urls.DETAILUSER, params, this, new ObjectCallBack<LoginResult>(LoginResult.class) {
            @Override
            public void onSuccess(LoginResult o) {
                int num = 1;
                List<LoginResult.DataBean> arrayList = new ArrayList<>();
                arrayList.addAll(o.getData());
                Collections.sort(arrayList);
                for (int i = 0; i < arrayList.size(); i++){
                    GoodsItem item = null;
                    item = new GoodsItem(Integer.valueOf(arrayList.get(i).getU_id()), arrayList.get(i).getU_name(),
                            Integer.valueOf(arrayList.get(i).getU_department_id()), arrayList.get(i).getDd_name(),
                            arrayList.get(i).getU_icon(), arrayList.get(i).getU_remark());
                    dataList.add(item);
                    if (i == 0){
                        typeList.add(item);
                    }else {
                        if (Integer.valueOf(arrayList.get(i).getU_department_id()) != Integer.valueOf(arrayList.get(i-1).getU_department_id())){
                            typeList.add(item);
                        }
                    }


                }
                initView();
            }

            @Override
            public void onFail(Call call, Exception e) {
                ToastUtil.showBottomToast(R.string.loadUnsuccessfully);
            }
        });
    }

    private void initView(){

        rvType = (RecyclerView) getActivity().findViewById(R.id.typeRecyclerView);

        listView = (StickyListHeadersListView) getActivity().findViewById(R.id.itemListView);

        rvType.setLayoutManager(new LinearLayoutManager(getActivity()));
        typeAdapter = new TypeAdapter(this,typeList);
        rvType.setAdapter(typeAdapter);
//        rvType.addItemDecoration(new DividerDecoration(getActivity()));

        myAdapter = new GoodsAdapter(dataList,this);
        listView.setAdapter(myAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                GoodsItem item = dataList.get(firstVisibleItem);
                if(typeAdapter.selectTypeId != item.typeId) {
                    typeAdapter.selectTypeId = item.typeId;
                    typeAdapter.notifyDataSetChanged();
                    rvType.smoothScrollToPosition(getSelectedGroupPosition(item.typeId));
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
//            case R.id.bottom:
//                showBottomSheet();
//                break;
//            case R.id.clear:
//                clearCart();
//                break;
//            case R.id.tvSubmit:
//                resultList = new ArrayList<>();
//                double totalPrice = 0;
//                for (int i=0; i < selectedList.size(); i++){
//                    GoodsItem item = selectedList.valueAt(i);
//                    totalPrice += (item.price*item.count);
//                    resultList.add(item);
//                }
//                Bundle b = new Bundle();
//                b.putParcelableArrayList("list", resultList);
//                b.putString("price", totalPrice + "");
//                IntentUtil.startActivity(getActivity(), PostOrderForPurchaseImmediatelyAcy.class, b);
//                break;
            default:
                break;
        }
    }

    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id){
        GoodsItem temp = selectedList.get(id);
        if(temp==null){
            return 0;
        }
        return temp.count;
    }
    //根据类别Id获取属于当前类别的数量
    public int getSelectedGroupCountByTypeId(int typeId){
        return groupSelect.get(typeId);
    }

    //根据类别id获取分类的Position 用于滚动左侧的类别列表
    public int getSelectedGroupPosition(int typeId){
        for(int i=0;i<typeList.size();i++){
            if(typeId==typeList.get(i).typeId){
                return i;
            }
        }
        return 0;
    }

    public void onTypeClicked(int typeId){
        listView.setSelection(getSelectedPosition(typeId));
    }

    private int getSelectedPosition(int typeId){
        int position = 0;
        for(int i=0;i<dataList.size();i++){
            if(dataList.get(i).typeId == typeId){
                position = i;
                break;
            }
        }
        return position;
    }

}