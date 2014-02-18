package com.dgd.listremovalanimation;

public class ListItem
{
	private static long stableID = 0;

	private long mID;
	private String mText;

	public ListItem(String text)
	{
		mText = text;
		mID = stableID;
		stableID++;
	}

	public long getID()
	{
		return mID;
	}

	public String getText()
	{
		return mText;
	}

	@Override
	public String toString()
	{
		return getText();
	}

}
