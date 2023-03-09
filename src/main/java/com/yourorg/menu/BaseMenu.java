package com.yourorg.menu;

public abstract class BaseMenu {
    
    /**
     * use {@link BaseMultiSelectMenu}
     * @return
     */
    @Deprecated
    protected abstract boolean isMultiSelect();

    protected abstract void execute();

}
