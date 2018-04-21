package com.fin.connect;

import java.io.Serializable;

public interface Connect {
    Object sendRequest(Serializable... serializables);
}
