package mobile.computing.draganddroppractice

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.AbsListView
import android.widget.FrameLayout

class MainActivity : AppCompatActivity() {
    lateinit var MyResizableView: MyResizableView
    lateinit var flDroppable: FrameLayout

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyResizableView = findViewById(R.id.resizableView)
        flDroppable = findViewById(R.id.flDroppable)

        MyResizableView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val dragData = View.DragShadowBuilder(MyResizableView)
                MyResizableView.startDragAndDrop(null, dragData, v, 0)
            }
            false
        }

        flDroppable.setOnDragListener { v, event ->
            val draggedView = event.localState as MyResizableView
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val newView = MyResizableView(this@MainActivity, null)
                    val params = FrameLayout.LayoutParams(draggedView.width, draggedView.height)
                    newView.layoutParams = params
                    flDroppable.addView(newView)
                    params.leftMargin = (event.x - newView.layoutParams.width / 2).toInt().coerceIn(0, flDroppable.width - newView.layoutParams.width)
                    params.topMargin = (event.y - newView.layoutParams.height / 2).toInt().coerceIn(0, flDroppable.height - newView.layoutParams.height)
                    newView.layoutParams = params
                }
            }
            true
        }
    }
}