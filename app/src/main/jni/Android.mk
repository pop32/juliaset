LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := juliaset
LOCAL_SRC_FILES := juliaset.c
LOCAL_LDLIBS := -llog
LOCAL_ARM_MODE := arm
LOCAL_ARM_NEON := true
LOCAL_CFLAGS += -save-temps
include $(BUILD_SHARED_LIBRARY)
