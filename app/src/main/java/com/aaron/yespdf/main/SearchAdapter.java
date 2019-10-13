package com.aaron.yespdf.main;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.yespdf.R;
import com.aaron.yespdf.common.CoverHolder;
import com.aaron.yespdf.common.DBHelper;
import com.aaron.yespdf.common.DataManager;
import com.aaron.yespdf.common.EmptyHolder;
import com.aaron.yespdf.common.bean.PDF;
import com.aaron.yespdf.common.event.RecentPDFEvent;
import com.aaron.yespdf.preview.PreviewActivity;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aaron aaronzzxup@gmail.com
 */
class SearchAdapter extends AbstractAdapter<PDF> implements Filterable {

    private static final int TYPE_EMPTY = 1;
    private static final int TYPE_CONTENT = 2;

    private boolean inverse;
    private RecentPDFEvent recentPDFEvent;

    private List<PDF> filterList;

    SearchAdapter(List<PDF> sourceList, boolean canSelect) {
        super(sourceList, canSelect);
        recentPDFEvent = new RecentPDFEvent();
    }

    void update() {
        DataManager.updatePDFs();
    }

    boolean isEmpty() {
        return filterList == null || filterList.isEmpty();
    }

    void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null) {
                    filterList = null;
                    FilterResults results = new FilterResults();
                    results.values = filterList;
                    return results;
                }
                String keyword = constraint.toString();
                if (!StringUtils.isEmpty(keyword)) {
                    filterList = new ArrayList<>();
                    for (PDF pdf : sourceList) {
                        boolean contains = pdf.getName().contains(keyword) != inverse;
                        if (contains) {
                            filterList.add(pdf);
                        }
                    }
                } else {
                    filterList = null;
                }
                FilterResults results = new FilterResults();
                results.values = filterList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterList = (List<PDF>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    RecyclerView.ViewHolder createHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            View itemView = inflater.inflate(R.layout.app_recycler_item_emptyview, parent, false);
            return new EmptyHolder(itemView);
        }
        View itemView = inflater.inflate(CoverHolder.DEFAULT_LAYOUT, parent, false);
        return new CoverHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    void bindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof CoverHolder && position < getItemCount()) {
            CoverHolder holder = (CoverHolder) viewHolder;
            PDF pdf = filterList.get(position);
            String cover = pdf.getCover();
            String bookName = pdf.getName();
            holder.tvTitle.setText(bookName);
            holder.tvProgress.setText(context.getString(R.string.app_already_read) + pdf.getProgress());
            if (!StringUtils.isEmpty(cover)) {
                ImageLoader.load(context, new DefaultOption.Builder(cover).into(holder.ivCover));
            } else {
                holder.ivCover.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.ivCover.setImageResource(R.drawable.app_img_none_cover);
            }
            handleCheckBox(holder.cb, position);
        } else if (viewHolder instanceof EmptyHolder) {
            EmptyHolder holder = (EmptyHolder) viewHolder;
            holder.itvEmpty.setVisibility(View.VISIBLE);
            holder.itvEmpty.setText(R.string.app_have_no_file);
            holder.itvEmpty.setIconTop(R.drawable.app_img_file);
        }
    }

    @Override
    void bindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        bindHolder(viewHolder, position);
    }

    @Override
    int itemCount() {
        if (filterList == null || filterList.isEmpty()) {
            return 1;
        }
        return filterList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (filterList == null || filterList.isEmpty()) {
            return TYPE_EMPTY;
        }
        return TYPE_CONTENT;
    }

    @Override
    void onTap(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof CoverHolder) {
            PDF pdf = filterList.get(position);
            long cur = System.currentTimeMillis();
            @SuppressLint("SimpleDateFormat")
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            pdf.setLatestRead(Long.parseLong(TimeUtils.millis2String(cur, df)));
            DBHelper.updatePDF(pdf);
            DBHelper.insertRecent(pdf);
            DataManager.updateRecentPDFs();
            PreviewActivity.start(context, pdf);
            EventBus.getDefault().post(recentPDFEvent);
        }
    }

    @Override
    void checkCurrent(RecyclerView.ViewHolder viewHolder, int position) {

    }
}
