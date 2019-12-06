package com.varkalys.homework1.protocol;

import java.io.IOException;

public interface Future<T> {
    void onResult(T result) throws IOException;
}
