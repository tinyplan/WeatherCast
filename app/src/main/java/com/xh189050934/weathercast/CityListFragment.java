package com.xh189050934.weathercast;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xh189050934.weathercast.bean.City;
import com.xh189050934.weathercast.bean.CityListVO;
import com.xh189050934.weathercast.utils.CityUtils;

import java.util.List;

public class CityListFragment extends Fragment {
    public static final String TAG = "CityListFragment";
    private static final String ARG_CITY_LIST = "cityList";

    private RecyclerView mCityRecyclerView;
    private CityAdapter mAdapter;

    private List<City> mCityList;

    private CallBacks mCallBacks;

    public interface CallBacks{
        void onClickCity(City city);
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

    public static CityListFragment newInstance(){
        CityListFragment fragment = new CityListFragment();
        /*Bundle args = new Bundle();
        args.putSerializable(ARG_CITY_LIST,null);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new CityTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_city,container,false);

        mCityRecyclerView = v.findViewById(R.id.city_recycle);
        mCityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*CityListVO cityListVO = (CityListVO) getArguments().get(ARG_CITY_LIST);
        if(cityListVO != null){
            mCityList = cityListVO.getCityList();
            updateUI();
        }else{
            new CityTask().execute();
        }*/

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //保存状态信息
        /*if(!this.isStateSaved()) {
            Bundle args = saveData(mCityList);
            this.setArguments(args);
        }*/
    }

    private Bundle saveData(List<City> cityList){
        Bundle bundle = new Bundle();
        CityListVO cityListVO = new CityListVO(mCityList);
        bundle.putSerializable(ARG_CITY_LIST,cityListVO);
        return bundle;
    }

    private void updateUI(){
        mAdapter = new CityAdapter(mCityList);
        mCityRecyclerView.setAdapter(mAdapter);
    }

    private class CityTask extends AsyncTask<Void, Void, List<City>> {
        @Override
        protected List<City> doInBackground(Void... voids) {
            return new CityUtils().getCityList();
        }

        @Override
        protected void onPostExecute(List<City> cities) {
            mCityList = cities;
            updateUI();
        }
    }

    private class CityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //数据
        private City mCity;
        //组件
        private TextView mIdTextView;
        private TextView mNameTextView;

        public CityHolder(@NonNull View itemView) {
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
            mCallBacks.onClickCity(mCity);
        }
    }

    private class CityAdapter extends RecyclerView.Adapter<CityHolder>{

        private List<City> mCityList;

        public CityAdapter(List<City> cityList) {
            mCityList = cityList;
        }

        @NonNull
        @Override
        public CityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //版本问题 将设置holder的布局移到此处
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_user_city,viewGroup,false);
            return new CityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CityHolder holder, int i) {
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
