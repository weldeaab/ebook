package org.benzkuai.yijing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-1-12
 * Time: 下午12:54
 * To change this template use File | Settings | File Templates.
 */
public class BookViewPagerAdapter extends PagerAdapter {

    // 只用三个view，动态填充内容
    private static final int VIEW_COUNT = 3;
    private View[] m_viewArray = null;
    private ImageView[] m_imageViewArray = null;
    private Canvas[] m_canvasArray = null;

    // 页面工厂
    BookFactory m_bookFactory = null;

    // 虽然view的个数只有3个，但源数据（页）可以有多条记录
    private int m_pageCount = 0;

    private boolean m_changeChapter = false;
//    private boolean m_positionNone = false;

    /**
     * 仅构造出适配器对象，在view尺寸计算出来后才init
     */
    public BookViewPagerAdapter() {
        super();    //To change body of overridden methods use File | Settings | File Templates.

    }

    /**
     * 计算view尺寸后才真正去init
     * @param activity
     * @param viewWidth
     * @param viewHeight
     */
    public void init(Activity activity, int viewWidth, int viewHeight)
    {
        // 对java还是不习惯，居然未new出数组对象实例来 (用惯了c指针)
        m_viewArray = new View[VIEW_COUNT];
        m_imageViewArray = new ImageView[VIEW_COUNT];
        m_canvasArray = new Canvas[VIEW_COUNT];

         // 由layout反射出view对象，使用同一种layout布局即可
        for (int i=0; i< VIEW_COUNT; i++)
        {
            m_viewArray[i] = activity.getLayoutInflater().inflate(R.layout.page, null);
            m_imageViewArray[i] = (ImageView)m_viewArray[i].findViewById(R.id.idImageView);
            Bitmap bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            m_imageViewArray[i].setImageBitmap(bmp);
            m_canvasArray[i] = new Canvas(bmp);
        }

        // 打开书 ,打开对应章节，定位到块，偏移，完成所有块的划分
        m_bookFactory = new BookFactory(activity, viewWidth, viewHeight);

        // 由此来决定如何添加页面
       setPageCount(m_bookFactory.getPageCount());
    }

    @Override
    public int getCount() {
        return m_pageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);    //To change body of overridden methods use File | Settings | File Templates.
        // 不在这处理，在实例时处理
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    // 这里是实例新数据的地方，如果要公用view的话，在这里操作最方便
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // 解决初始页面为空白的bug
        // 过滤掉这两个页面，再设置item值时，view显示正常
        if (0 == position || 1 == position)
        {
            return null;
        }
        // 当PageDown时，先destroy，再instantiate;
        // 当PageUp时，先instantiate，再destroy
        // 所以这里必须先解决“子view”与“Group”的关系，才能再建立关系
        container.removeView(getView(position%VIEW_COUNT));
        container.addView(getView(position%VIEW_COUNT));
        Log.v("k1", "instantiateItem position=("+position+")");

        // 当实例此page时，将数据绘制完毕，在拖动时就会正常显示
        // 这里传入实际的页面索引号

            m_bookFactory.drawPage(m_canvasArray[position%VIEW_COUNT], position);


        return getView(position % VIEW_COUNT);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * 设置此adapter的总元素数（为数据元素，而不是view数）
     * @param pageCount
     */
    public void setPageCount(int pageCount)
    {
        m_pageCount = pageCount;
        // 躲不过了，0，1作为直接过滤了 ,在新的SDK上不增加此方法会崩溃。
        // 编译器会报缺少此调用的错误提示
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        // 解决了直接切到一个item项目时的黑屏问题，且兼顾的效率。直接返回这个效率太低
        // 所以只在切换章节时使用
        if ( m_changeChapter)
        {
            m_changeChapter = false;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * 提供视图
     * @param index
     * @return
     */
    public View getView(int index)
    {
        return m_viewArray[index];
    }

    /**
     * 下面都是壳函数
     * @return
     */
    public BookFactory.PAGE_TYPE getPrimaryItemType()
    {
        return m_bookFactory.getPrimaryItemType();
    }

    public int getPrimaryItem()
    {
        return m_bookFactory.getPrimaryItem();
    }

    public void setPrimaryItem(int primaryItem)
    {
        m_bookFactory.setPrimaryItem(primaryItem);
    }

    public void saveBookmark0()
    {
//        m_bookFactory.saveBookmark0();
    }

    public String getPercent()
    {
        return m_bookFactory.getPercent();
    }

    public CharSequence[] getDirectory()
    {
        return m_bookFactory.getDirectory();
    }

    public void gotoChapter(int chapterNumber)
    {
        m_changeChapter = true;
        m_bookFactory.gotoChapter(chapterNumber);
    }

//    public void setPositionNone(boolean setNone)
//    {
//        m_positionNone =  setNone;
//    }
}
