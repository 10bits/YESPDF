package com.aaron.yespdf.main

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.aaron.base.image.DefaultOption
import com.aaron.base.image.ImageLoader
import com.aaron.yespdf.R
import com.aaron.yespdf.common.CoverHolder
import com.aaron.yespdf.common.DBHelper
import com.aaron.yespdf.common.DataManager
import com.aaron.yespdf.common.EmptyHolder
import com.aaron.yespdf.common.bean.PDF
import com.aaron.yespdf.common.event.RecentPDFEvent
import com.aaron.yespdf.preview.PreviewActivity
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import org.greenrobot.eventbus.EventBus
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author Aaron aaronzzxup@gmail.com
 */
internal class CollectionAdapter(
        commInterface: ICommInterface<PDF>,
        sourceList: List<PDF>
) : AbstractAdapter<PDF>(commInterface, sourceList) {

    private val recentPDFEvent: RecentPDFEvent = RecentPDFEvent()

    override fun createHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = inflater.inflate(CoverHolder.DEFAULT_LAYOUT, parent, false)
        return CoverHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun bindHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is CoverHolder && position < itemCount) {
            val pdf = sourceList[position]
            val cover = pdf.cover
            val bookName = pdf.name
            viewHolder.tvTitle.text = bookName
            viewHolder.tvProgress.text = context.getString(R.string.app_already_read) + pdf.progress
            if (!StringUtils.isEmpty(cover)) {
                ImageLoader.load(context, DefaultOption.Builder(cover).into(viewHolder.ivCover))
            } else {
                viewHolder.ivCover.scaleType = ImageView.ScaleType.FIT_XY
                viewHolder.ivCover.setImageResource(R.drawable.app_img_none_cover)
            }
            handleCheckBox(viewHolder.cb, position)
        } else if (viewHolder is EmptyHolder) {
            viewHolder.itvEmpty.visibility = View.VISIBLE
            viewHolder.itvEmpty.setText(R.string.app_have_no_all)
            viewHolder.itvEmpty.setIconTop(R.drawable.app_img_all)
        }
    }

    override fun bindHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            bindHolder(viewHolder, position)
        } else {
            if (viewHolder is CoverHolder && position < itemCount) {
                handleCheckBox(viewHolder.cb, position)
            }
        }
    }

    override fun itemCount(): Int {
        return sourceList.size
    }

    override fun onTap(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        if (viewHolder is CoverHolder) {
            if (viewHolder.cb.visibility == View.VISIBLE) {
                val pdf = sourceList[position]
                val isChecked = !viewHolder.cb.isChecked
                viewHolder.cb.isChecked = isChecked
                if (viewHolder.cb.isChecked) {
                    selectList.add(pdf)
                } else {
                    selectList.remove(pdf)
                }
                checkArray.put(position, isChecked)
                commInterface!!.onSelect(selectList, selectList.size == itemCount)
            } else {
                val pdf = sourceList[position]
                val cur = System.currentTimeMillis()
                @SuppressLint("SimpleDateFormat") val df: DateFormat = SimpleDateFormat("yyyyMMddHHmmss")
                pdf.latestRead = TimeUtils.millis2String(cur, df).toLong()
                DBHelper.updatePDF(pdf)
                DBHelper.insertRecent(pdf)
                DataManager.updatePDFs()
                PreviewActivity.start(context, pdf)
                EventBus.getDefault().post(recentPDFEvent)
                viewHolder.itemView.postDelayed({
                    if (commInterface != null) {
                        (commInterface as DialogFragment).dismiss()
                    }
                }, 400)
            }
        }
    }

    override fun checkCurrent(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        if (viewHolder is CoverHolder) {
            viewHolder.cb.isChecked = true
        }
    }
}