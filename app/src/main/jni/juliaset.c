//
// Created by kyon on 2019/02/23.
//
#include <jni.h>

JNIEXPORT void JNICALL Java_com_example_kyon_myapplication_JuliaSetTestView_HelloJni
        (JNIEnv *env, jobject thiz, jintArray cnt_arr, jint w, jint h, jdouble ar, jdouble ai)
{
    jint* p_cnt_arr = (*env)->GetIntArrayElements(env,cnt_arr,0);

    const double zr_min=-2.0, zi_min=-2.0, zr_max=2.0, zi_max=2.0;
    const double dx = (zr_max - zr_min) / w;
    const double dy = (zi_max - zi_min) / h;
    double zr,zi,wr,wi,val;
    int x,y,cnt;
    for (y = 0; y < h; y++) {
        for (x = 0; x < w; x++) {
            zr = zr_min + x * dx;
            zi = zi_min + y * dy;
            cnt = 0;
            do {
                wr = (zr + zi) * (zr - zi) + ar;
                wi = 2 * zr * zi + ai;
                val = (wr * wr) + (wi * wi);
                zr = wr;
                zi = wi;
                if (cnt++ > 64) {
                    cnt = -1;
                    break;
                }
            } while (val < 4);
            p_cnt_arr[(y*w)+(x)] = cnt;
        }
    }
}
