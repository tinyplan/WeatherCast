package com.xh189050934.weathercast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xh189050934.weathercast.bean.City;

import java.util.List;

public class CityFragment extends Fragment {
    public static final String TAG = "CityFragment";

    //组件
    private RecyclerView mUserCityRecyclerView;
    private UserCityAdapter mAdapter;
    private CallBacks mCallBacks;

    public interface CallBacks{
        void onAddCity();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBacks = (CallBacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    public static CityFragment newInstance(){
        return new CityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);//添加菜单栏
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_city,container,false);

        mUserCityRecyclerView = v.findViewById(R.id.city_recycle);
        mUserCityRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                //处理滑动删除
                //直接从数据库中删除该Item的数据
                CityLab.get(getActivity()).deleteCity(mAdapter.mCityList.get(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(),R.string.delete_city_success,Toast.LENGTH_SHORT).show();
                //通知Adapter有Item被移除了
                updateUI();
            }
        });
        itemTouchHelper.attachToRecyclerView(mUserCityRecyclerView);

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            updateUI();
        }
    }

    /**
     * 创建菜单栏
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_city, menu);
    }

    /**
     * 菜单栏监听事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_city:
                System.out.println("add");
                addCity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addCity(){
        mCallBacks.onAddCity();
    }

    /**
     * 更新UI
     */
    private void updateUI(){
        //取出新的数据
        CityLab cityLab = CityLab.get(getContext());
        List<City> cityList = cityLab.getCities();

        if(mAdapter == null){
            //若adapter为null,表明第一次加载
            mAdapter = new UserCityAdapter(cityList);
            mUserCityRecyclerView.setAdapter(mAdapter);
        }else {
            //不为null,则设置新的数据
            mAdapter.setCityList(cityList);
            //通知更新
            mAdapter.notifyDataSetChanged();
        }
    }

    private class UserCityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //数据
        private City mCity;
        //组件
        private TextView mIdTextView;
        private TextView mNameTextView;

        public UserCityHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mIdTextView = itemView.findViewById(R.id.tv_user_city_id);
            mNameTextView = itemView.findViewById(R.id.tv_user_city_name);
        }

        public void bind(City city){
            mCity = city;
            mIdTextView.setText(mCity.getId());
            mNameTextView.setText(mCity.getName());
        }

        @Override
        public void onClick(View v) {
            Intent intent = WeatherActivity.newIntent(getContext(),mCity);
            startActivity(intent);
        }
    }

    private class UserCityAdapter extends RecyclerView.Adapter<UserCityHolder>{

        private List<City> mCityList;

        public UserCityAdapter(List<City> cityList) {
            mCityList = cityList;
        }

        @NonNull
        @Override
        public UserCityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //版本问题 将设置holder的布局移到此处
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_user_city,viewGroup,false);
            return new UserCityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserCityHolder holder, int i) {
            City city = mCityList.get(i);
            holder.bind(city);
        }

        @Override
        public int getItemCount() {
            return mCityList.size();
        }

        public void setCityList(List<City> cityList){
            mCityList = cityList;
        }
    }
}
