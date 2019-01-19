package com.alexandra.sma_final.server;

public interface AsyncResponse<T> {

    void processFinish(T output);

}
