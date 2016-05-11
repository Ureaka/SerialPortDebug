LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= SerialPort.cpp 

LOCAL_MODULE:= serial_port_jni
LOCAL_STATIC_LIBRARIES := \
	libcutils \
	libc \
	liblog

LOCAL_PROPRIETARY_MODULE := true
include $(BUILD_SHARED_LIBRARY)