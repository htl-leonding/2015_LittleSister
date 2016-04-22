package at.htl.client;

import java.util.List;

public interface ITracker {
    public int getInterval();
    public void setInterval();
    public Object getNextData();
    public List<Object> getAllData();
    public void Start();
    public void Stop();
}
