package com.lzy.widget.vertical;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/10
 * 描    述：
 * 修订历史：
 * ================================================
 */
public interface ObservableView {
    /**
     * 是否滚动到顶部了
     */
    boolean isTop();

    /**
     * 是否滚动到底部了
     */
    boolean isBottom();
}