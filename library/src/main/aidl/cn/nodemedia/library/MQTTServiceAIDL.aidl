// MQTTServiceAIDL.aidl
package cn.nodemedia.library;

// Declare any non-default types here with import statements

interface MQTTServiceAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    // void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

      boolean publishMsg(String topic, String content);

}
