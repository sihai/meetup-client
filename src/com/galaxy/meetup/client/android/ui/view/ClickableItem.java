/**
 * galaxy inc.
 * meetup client for android
 */
package com.galaxy.meetup.client.android.ui.view;

import java.util.Comparator;

import android.graphics.Rect;

/**
 * 
 * @author sihai
 *
 */
public interface ClickableItem extends Comparator<ClickableItem> {

	ClickableItemsComparator sComparator = new ClickableItemsComparator();
	
	CharSequence getContentDescription();

    Rect getRect();

    boolean handleEvent(int i, int j, int k);
    
    //===========================================================================
    //						Inner class
    //===========================================================================
    class ClickableItemsComparator implements Comparator<ClickableItem> {

    	public int compare(ClickableItem clickableitem, ClickableItem clickableitem1) {
    		int i = -1;
    		Rect rect = clickableitem.getRect();
    		Rect rect1 = clickableitem1.getRect();
    		if(rect.bottom > rect1.top) {
    			if(rect.top >= rect1.bottom) {
    	            i = 1;
    	        } else {
    	            i = rect.left - rect1.left;
    	            if(i == 0) {
    	                int j = rect.top - rect1.top;
    	                if(j != 0) {
    	                    i = j;
    	                } else {
    	                    int k = rect.bottom - rect1.bottom;
    	                    if(k != 0) {
    	                        i = k;
    	                    } else {
    	                        int l = rect.right - rect1.right;
    	                        if(l != 0)
    	                            i = l;
    	                        else
    	                            i = clickableitem.hashCode() - clickableitem1.hashCode();
    	                    }
    	                }
    	            }
    	        }
    		} else {
    			i = -1;
    		}
    		return i;
    	}
    }
}
