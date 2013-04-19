package com.android.gallery3d.util;

import java.util.concurrent.Future;

public interface FutureListener<T> {

	void onFutureDone(Future<T> future);
}
