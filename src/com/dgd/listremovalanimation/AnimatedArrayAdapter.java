package com.dgd.listremovalanimation;

import java.util.HashMap;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AnimatedArrayAdapter extends ArrayAdapter<ListItem>
{
	// Map to store the top of the views in the list before we animate them
	private HashMap<Long, Integer> mViewTopHashMap = new HashMap<Long, Integer>();

	// We need a reference to a listview
	private ListView mListView;

	// Will hold the height of a removed view for when we are animating
	private int mRemovedItemHeight;
	
	private final int ANIMATION_DURATION = 200;

	public AnimatedArrayAdapter(Context context, ListView listView, int resource)
	{
		super(context, resource);
		mListView = listView;
	}

	public AnimatedArrayAdapter(Context context, ListView listView, int resource, int textViewResourceId)
	{
		super(context, resource, textViewResourceId);
		mListView = listView;
	}

	// Let the adapter know it has stable ids
	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	// Return the stable id of the object
	@Override
	public long getItemId(int position)
	{
		return ((ListItem) getItem(position)).getID();
	}

	// The remove with an animation
	public void animateRemoval(final int position)
	{
		mViewTopHashMap.clear();
		mListView.setEnabled(false);

		// Iterate through all the views visible in the list
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		for (int i = 0; i < mListView.getChildCount(); i++)
		{
			View child = mListView.getChildAt(i);
			int itemPosition = firstVisiblePosition + i;
			long itemID = getItemId(itemPosition);
			mViewTopHashMap.put(itemID, child.getTop());
		}

		// Get the height of the removed view, if it was on screen
		View removedView = mListView.getChildAt(position - firstVisiblePosition);
		if (removedView != null)
		{
			mRemovedItemHeight = removedView.getHeight();
		}
		else
		{
			mRemovedItemHeight = 0;
		}

		// remove the item from the data set, subsequently calls
		// notifyDataSetChanged()
		remove(getItem(position));

		// Add a pre-draw listener to the listview
		// This will allow us to mess with the views inside the listview after
		// the removal has happened and the new views are positioned,
		// but before the new views are drawn on the screen.

		mListView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener()
		{

			@Override
			public boolean onPreDraw()
			{
				// Remove this listener, having it called on every draw would be
				// expensive
				mListView.getViewTreeObserver().removeOnPreDrawListener(this);

				// Iterate through all the visible positions in the list
				int firstVisiblePosition = mListView.getFirstVisiblePosition();
				for (int i = 0; i < mListView.getChildCount(); i++)
				{
					View child = mListView.getChildAt(i);
					int itemPosition = firstVisiblePosition + i;
					long itemID = getItemId(itemPosition);

					int delta;

					// The view was in the list before and we need to animate it
					if (mViewTopHashMap.containsKey(itemID))
					{
						int startTop = mViewTopHashMap.get(itemID);
						delta = startTop - child.getTop();
					}
					// This view was not in the list before but has since moved
					// into it
					else
					{
						// The view came from below the removed view
						if (itemPosition > position - 1)
						{
							delta = (mRemovedItemHeight + mListView.getDividerHeight());
						}
						// The view came from above the removed view
						else
						{
							delta = -(mRemovedItemHeight + mListView.getDividerHeight());
						}
					}

					// animate the view
					child.setTranslationY(delta);
					if (i == 0)
					{
						// reenable the listview after the animation
						child.animate().translationY(0).setDuration(ANIMATION_DURATION).setListener(new AnimatorListener()
						{

							@Override
							public void onAnimationStart(Animator animation)
							{
							}

							@Override
							public void onAnimationRepeat(Animator animation)
							{
							}

							@Override
							public void onAnimationEnd(Animator animation)
							{
								// enable the listview
								mListView.setEnabled(true);
							}

							@Override
							public void onAnimationCancel(Animator animation)
							{
							}
						});
					}
					else
					{
						// just animate
						child.animate().translationY(0).setDuration(ANIMATION_DURATION);
					}
				}
				
				//re-enable the listview if all views were removed
				if(mListView.getChildCount() == 0)
				{
					mListView.setEnabled(true);
				}

				// Returning false would stop the draw pass, but we still want
				// to draw
				return true;
			}
		});
	}
}
