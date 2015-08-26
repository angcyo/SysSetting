package com.angcyo.fragment.port;

import com.angcyo.fragment.holder.IconTreeItemHolder;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by angcyo on 2015-04-07 007.
 */
public interface OnTreeNodeClickListener {
    void onTreeItemClick(TreeNode node, IconTreeItemHolder.IconTreeItem item);
}
