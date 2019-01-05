package com.alexandra.sma_final.view;

import java.util.List;

public interface ParentListItem {
    List<?> getChildItemList();

    boolean isInitiallyExpanded();
}