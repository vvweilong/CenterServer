// IServiceAIDL.aidl
package com.example.homecenter;
import com.example.homecenter.IServiceCallback;

// Declare any non-default types here with import statements

interface IServiceAIDL {

    void sendCommand(String cmd);

    void registCallback(IServiceCallback callback);
}
