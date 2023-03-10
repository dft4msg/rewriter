package com.yourorg.menu;

public abstract class BaseMenu {

    /**
     * use {@link BaseMultiSelectMenu}
     * 
     * @return
     */
    @Deprecated
    protected boolean isMultiSelect() {
        return false;
    }

    /**
     * use {@link BaseSingleSelectMenu}
     * 
     * @return
     */
    @Deprecated
    protected boolean isSingleSelect() {
        return true;
    }

    /**
     * use {@link BaseEmptySelectMenu}
     * 
     * @return
     */
    @Deprecated
    protected boolean isEmptySelect() {
        return false;
    }

    protected abstract void execute();

}
