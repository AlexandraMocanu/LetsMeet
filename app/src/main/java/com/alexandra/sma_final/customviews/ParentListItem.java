package com.alexandra.sma_final.customviews;

import java.util.List;

public interface ParentListItem {
    List<?> getChildItemList();

    boolean isInitiallyExpanded();
}