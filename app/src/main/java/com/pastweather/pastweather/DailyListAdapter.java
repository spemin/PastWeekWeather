package com.pastweather.pastweather;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyListAdapter extends RecyclerView.Adapter<DailyListAdapter.DailyViewHolder>{
    DailyWeather[] listDaily;

    public DailyListAdapter(DailyWeather[] listDay) {
        this.listDaily = listDay;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        DailyViewHolder viewHolder = null;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_item,
                viewGroup, false);
        if (view != null) {
            viewHolder = new DailyViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder dailyViewHolder, int i) {
        if (i < 0) return;
        String strText = "";
        DailyWeather weather = listDaily[i];
        if (weather == null) {
            return;
        }
        DailyWeather.DayWeather dayWeather = weather.daily.data[0];
        if (dayWeather == null) return;
        SimpleDateFormat dataFormat= new SimpleDateFormat("EEE MM/dd/yy");
        strText = dataFormat.format(new Date(dayWeather.time*1000));
        if (i == 0) {
            strText = "TODAY - " + strText;
        }
        dailyViewHolder.etDate.setText(strText);
        dailyViewHolder.etSummary.setText(dayWeather.summary);
        strText = String.format("Temperature:      %.1f\u00b0/%.1f\u00b0", dayWeather.temperatureHigh,
                dayWeather.temperatureLow);
        dailyViewHolder.tvTemperature.setText(strText);
        strText=String.format("Humidity:   %.2f%%", dayWeather.humidity*100);
        dailyViewHolder.tvHumidity.setText(strText);
        strText = String.format("Wind:   %.1f", dayWeather.windSpeed);
        dailyViewHolder.tvWind.setText(strText);
        dailyViewHolder.tvSunRise.setText("");
        dailyViewHolder.tvSunSet.setText("");
    }

    @Override
    public int getItemCount() {
        return listDaily.length;
    }

    public static class DailyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.etDate)
        EditText etDate;
        @BindView(R.id.imgIcon)
        ImageView imgIcon;
        @BindView(R.id.etSummary)
        EditText etSummary;
        @BindView(R.id.tvTemperature)
        TextView tvTemperature;
        @BindView(R.id.tvHumidity)
        TextView tvHumidity;
        @BindView(R.id.tvWind)
        TextView tvWind;
        @BindView(R.id.tvSunRise)
        TextView tvSunRise;
        @BindView(R.id.tvSunSet)
        TextView tvSunSet;

        public DailyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
