package com.codewaves.dcell

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codewaves.dcell.databinding.ActivityMainBinding
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val strings = (1..30).map {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
                .substring(0..Random.nextInt(15, 70))
        }

        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = CustomAdapter(strings)
    }
}

class CustomAdapter(private val dataSet: List<String>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView1: TextView
        val textView2: TextView
        val textView3: TextView

        init {
            textView1 = view.findViewById(R.id.text1)
            textView2 = view.findViewById(R.id.text2)
            textView3 = view.findViewById(R.id.text3)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_test, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView1.text = dataSet[position]
        viewHolder.textView2.text = "Optional text"
        viewHolder.textView3.text = "Bottom text"
    }

    override fun getItemCount() = dataSet.size
}

class DynamicLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = resolveSize(Int.MAX_VALUE, widthMeasureSpec)
        val contentWidth = widthSize - paddingStart - paddingEnd

        var isLargeTitle = false
        val height = children.foldIndexed(0) { index, height, child ->
            child.measure(
                MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )

            if (0 == index && child is TextView && child.lineCount > 1) {
                isLargeTitle = true
            }

            val isVisible = (1 != index || !isLargeTitle)
            child.isVisible = isVisible
            if (isVisible) height + child.measuredHeight else height
        }

        val heightSize = resolveSize(height + paddingTop + paddingBottom, heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        children.fold(0) { top, child ->
            child.layout(
                paddingStart,
                paddingTop + top,
                paddingStart + child.measuredWidth,
                paddingTop + top + child.measuredHeight
            )
            if (child.isVisible) top + child.measuredHeight else top
        }
    }
}