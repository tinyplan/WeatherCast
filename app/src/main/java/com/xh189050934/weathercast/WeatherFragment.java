package com.xh189050934.weathercast;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xh189050934.weathercast.bean.DailyWeather;
import com.xh189050934.weathercast.bean.HourWeather;
import com.xh189050934.weathercast.bean.Life;
import com.xh189050934.weathercast.bean.NowWeather;
import com.xh189050934.weathercast.bean.Weather;
import com.xh189050934.weathercast.utils.DateFormattor;
import com.xh189050934.weathercast.utils.WeatherUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private static final String KEY_NOW = "now";
    private static final String KEY_DAILY = "daily";
    private static final String KEY_HOURLY = "hourly";
    private static final String KEY_LIFE = "life";
    private static final String ARG_CITY_NAME = "cityName";

    //组件定义
    private TextView mCityTextView;
    private TextView mTmpTextView;
    private TextView mConditionTextView;
    private TextView mWindTextView;
    private TextView mPcpnTextView;
    private ImageView mConditionImageView;
    private RecyclerView mHourlyRecyclerView;
    private RecyclerView mDailyRecyclerView;
    private RecyclerView mLifeRecyclerView;
    private CallBacks mCallBacks;

    //变量定义
    private NowWeather nowWeather;
    private List<Weather> hourlyWeatherList;
    private List<Weather> dailyWeatherList;
    private List<Weather> mLifeList;
    private String cityName;

    public interface CallBacks{
        String getCityName();
    }

    public static WeatherFragment newInstance(){
        return new WeatherFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);//添加菜单栏
        cityName = mCallBacks.getCityName();
        new WeatherTask().execute(cityName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather,container,false);

        mCityTextView = v.findViewById(R.id.tv_city);
        mTmpTextView = v.findViewById(R.id.tv_tmp);
        mConditionTextView = v.findViewById(R.id.tv_condition);
        mWindTextView = v.findViewById(R.id.tv_wind);
        mPcpnTextView = v.findViewById(R.id.tv_pcpn);
        mConditionImageView = v.findViewById(R.id.iv_condition);

        mHourlyRecyclerView = v.findViewById(R.id.hourly_recycle);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        layout.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHourlyRecyclerView.setLayoutManager(layout);

        mDailyRecyclerView = v.findViewById(R.id.daily_recycle);
        mDailyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLifeRecyclerView = v.findViewById(R.id.life_recycle);
        mLifeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        return v;
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

    /**
     * 创建菜单栏
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_select_city, menu);
    }

    /**
     * 菜单栏监听事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_city:
                System.out.println("select");
                selectCity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectCity(){
        Intent intent = new Intent(getActivity(), CityActivity.class);
        startActivity(intent);
    }

    private void setupAdapter() {
        //先判断是否挂在完毕
        if (isAdded()) {
            //设置当前天气
            String tmp = nowWeather.getTmp()+getActivity().getString(R.string.tmp_suffix);
            String wind = nowWeather.getWind_dir() + " " + nowWeather.getWind_sc()+"级";
            String pcpn = nowWeather.getPcpn()+getActivity().getString(R.string.pcpn_suffix);
            mCityTextView.setText(cityName+"市");
            mTmpTextView.setText(tmp);
            mConditionTextView.setText(nowWeather.getCond_txt());
            mWindTextView.setText(wind);
            mPcpnTextView.setText("降水量:"+pcpn);
            int iconId = getContext().getResources()
                    .getIdentifier("w" + nowWeather.getCond_code(), "drawable", getContext().getPackageName());
            mConditionImageView.setImageResource(iconId);
            //设置未来天气
            mDailyRecyclerView.setAdapter(new WeatherAdapter(dailyWeatherList));
            mHourlyRecyclerView.setAdapter(new HourlyWeatherAdapter(hourlyWeatherList));
            mLifeRecyclerView.setAdapter(new LifeAdapter(mLifeList));
        }
    }

    /**
     * 获取天气信息线程
     * 参数 ： 城市名称
     */
    private class WeatherTask extends AsyncTask<String,Void, Map<String,List<Weather>>> {
        @Override
        protected Map<String,List<Weather>> doInBackground(String... params) {
            HashMap<String,List<Weather>> map = new HashMap<>();
            //连接并获取数据
            WeatherUtils utils = new WeatherUtils();
            map.put(KEY_NOW,utils.getWeather(params[0],WeatherUtils.NOW));
            map.put(KEY_HOURLY,utils.getWeather(params[0],WeatherUtils.HOURLY));
            map.put(KEY_DAILY,utils.getWeather(params[0],WeatherUtils.FORECAST));
            map.put(KEY_LIFE,utils.getWeather(params[0],WeatherUtils.LIFE));
            return map;
        }

        @Override
        protected void onPostExecute(Map<String,List<Weather>> weatherMap) {
            //刷新数据
            List<Weather> nowList = weatherMap.get(KEY_NOW);
            List<Weather> hourlyList = weatherMap.get(KEY_HOURLY);
            List<Weather> dailyList = weatherMap.get(KEY_DAILY);
            List<Weather> lifeList = weatherMap.get(KEY_LIFE);
            if (!nowList.isEmpty()) {
                nowWeather = (NowWeather) nowList.get(0);
            }
            if (!dailyList.isEmpty()){
                dailyWeatherList = dailyList;
            }
            if(!hourlyList.isEmpty()){
                hourlyWeatherList = hourlyList;
            }
            if(!lifeList.isEmpty()){
                mLifeList = lifeList;
            }
            //获取数据成功时，设置adapter
            setupAdapter();
        }
    }

    /**
     * 天气预报Recycle
     */
    private class WeatherHolder extends RecyclerView.ViewHolder{
        //数据
        private DailyWeather mWeather;
        //组件
        private TextView mDateTextView;
        private TextView mConditionTextView;
        private ImageView mConditionImage;
        private TextView mTmpTextView;

        public WeatherHolder(@NonNull View itemView) {
            super(itemView);
            mDateTextView = itemView.findViewById(R.id.re_tv_date);
            mConditionImage = itemView.findViewById(R.id.re_iv_cond);
            mConditionTextView = itemView.findViewById(R.id.re_tv_cond);
            mTmpTextView = itemView.findViewById(R.id.re_tv_tmp);
        }

        public void bind(Weather weather){
            mWeather = (DailyWeather) weather;
            mDateTextView.setText(mWeather.getDate());
            mConditionTextView.setText(mWeather.getCond_txt_d());
            String tmp = mWeather.getTmp_min() +
                    getActivity().getString(R.string.tmp_suffix) +" ~ " +
                    mWeather.getTmp_max()+getActivity().getString(R.string.tmp_suffix);
            mTmpTextView.setText(tmp);
            int iconId = getContext().getResources().getIdentifier("w" + mWeather.getCond_code_d(), "drawable", getContext().getPackageName());
            mConditionImage.setImageResource(iconId);
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder>{

        private List<Weather> mWeatherList;

        public WeatherAdapter(List<Weather> weatherList) {
            mWeatherList = weatherList;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //版本问题 将设置holder的布局移到此处
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_daily_weather,viewGroup,false);
            return new WeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder weatherHolder, int i) {
            Weather weather = mWeatherList.get(i);
            weatherHolder.bind(weather);
        }

        @Override
        public int getItemCount() {
            return mWeatherList.size();
        }

        public void setDailyWeather(List<Weather> dailyWeathers){
            mWeatherList = dailyWeathers;
        }
    }

    /**
     * 逐小时预报Recycle
     */
    private class HourlyWeatherHolder extends RecyclerView.ViewHolder{
        //数据
        private HourWeather mWeather;
        //组件
        private TextView mTmpTextView;
        private ImageView mConditionImageView;
        private TextView mConditionTextView;
        private TextView mTimeTextView;

        public HourlyWeatherHolder(@NonNull View itemView) {
            super(itemView);
            //组件定义
            mTmpTextView = itemView.findViewById(R.id.hour_tv_tmp);
            mConditionImageView = itemView.findViewById(R.id.hour_iv_con);
            mConditionTextView = itemView.findViewById(R.id.hour_tv_con);
            mTimeTextView = itemView.findViewById(R.id.hour_tv_time);
        }

        public void bind(Weather weather){
            mWeather = (HourWeather) weather;
            mTmpTextView.setText(mWeather.getTmp()+getActivity().getString(R.string.tmp_suffix));
            mConditionTextView.setText(mWeather.getCond_txt());
            mTimeTextView.setText(mWeather.getTime());

            int iconId = getContext().getResources().getIdentifier("w" + mWeather.getCond_code(), "drawable", getContext().getPackageName());
            mConditionImageView.setImageResource(iconId);
        }
    }

    private class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherHolder>{

        private List<Weather> mWeatherList;

        public HourlyWeatherAdapter(List<Weather> weatherList) {
            mWeatherList = weatherList;
        }

        @NonNull
        @Override
        public HourlyWeatherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //版本问题 将设置holder的布局移到此处
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_hourly_weather,viewGroup,false);
            return new HourlyWeatherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HourlyWeatherHolder holder, int i) {
            Weather weather = mWeatherList.get(i);
            holder.bind(weather);
        }

        @Override
        public int getItemCount() {
            return mWeatherList.size();
        }

        public void setHourlyWeather(List<Weather> hourWeathers){
            mWeatherList = hourWeathers;
        }
    }

    /**
     * 生活指数Recycle
     */
    private class LifeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //数据
        private Life mLife;
        //组件
        private ImageView mLifeImageView;
        private TextView mTypeTextView;
        private TextView mIndexTextView;

        public LifeHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            //组件定义
            mLifeImageView = itemView.findViewById(R.id.life_iv_type);
            mTypeTextView = itemView.findViewById(R.id.life_tv_type);
            mIndexTextView = itemView.findViewById(R.id.life_tv_index);
        }

        public void bind(Weather weather){
            mLife = (Life) weather;
            DateFormattor df = new DateFormattor();
            mTypeTextView.setText(df.swtichType(mLife.getType()));
            mIndexTextView.setText(mLife.getBrf());
            int iconId = getContext().getResources().getIdentifier(mLife.getType(), "drawable", getContext().getPackageName());
            mLifeImageView.setImageResource(iconId);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(),mLife.getTxt(),Toast.LENGTH_LONG).show();
        }
    }

    private class LifeAdapter extends RecyclerView.Adapter<LifeHolder>{

        private List<Weather> mLifeList;

        public LifeAdapter(List<Weather> weatherList) {
            mLifeList = weatherList;
        }

        @NonNull
        @Override
        public LifeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            //版本问题 将设置holder的布局移到此处
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.list_item_life,viewGroup,false);
            return new LifeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LifeHolder holder, int i) {
            Weather weather = mLifeList.get(i);
            holder.bind(weather);
        }

        @Override
        public int getItemCount() {
            return mLifeList.size();
        }

        public void setHourlyWeather(List<Weather> lifeList){
            mLifeList = lifeList;
        }
    }

}
