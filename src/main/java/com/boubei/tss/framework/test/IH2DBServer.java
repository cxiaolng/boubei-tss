package com.boubei.tss.framework.test;

import java.sql.Connection;

public interface IH2DBServer {

    void stopServer();
    
    boolean isPrepareed();
    
    void setPrepareed(boolean isPrepareed);
    
    Connection getH2Connection();
    
}
