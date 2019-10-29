package com.guima.base.kits;

public class OsResult<T>
{
	public boolean status = false;
	public T result = null;
	
	public void set(boolean status, T type)
	{
		this.status = status;
		this.result = type;
	}
}
