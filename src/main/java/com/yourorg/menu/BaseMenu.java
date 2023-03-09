package com.yourorg.menu;

public abstract class BaseMenu {
    
    /**
     * use {@link BaseMultiSelectMenu}
     * @return
     */
    @Deprecated
    abstract boolean isMultiSelect();

    abstract void execute();

}
